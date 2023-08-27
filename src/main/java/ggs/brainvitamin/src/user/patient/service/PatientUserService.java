package ggs.brainvitamin.src.user.patient.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.jwt.TokenProvider;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.AuthorityEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.patient.dto.ActivitiesDto;
import ggs.brainvitamin.src.user.patient.dto.ProfilesRequestDto;
import ggs.brainvitamin.src.user.patient.dto.TokenDto;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static ggs.brainvitamin.config.BaseResponseStatus.*;
import static ggs.brainvitamin.src.user.patient.dto.PatientUserDto.*;
import static ggs.brainvitamin.src.user.patient.dto.TokenDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientUserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Long createPatientUser(SignUpDto signUpDto, CommonCodeDetailDto codeDetailDto) throws BaseException {

        // 전화번호 중복 체크
        userRepository.findByPhoneNumberAndStatus(signUpDto.getPhoneNumber(), Status.ACTIVE)
                .ifPresent(none -> {
                    throw new BaseException(USER_ALREADY_EXISTS);
                });
      
        // 닉네임 중복 체크
        userRepository.findByNicknameAndStatus(signUpDto.getNickname(), Status.ACTIVE)
                .ifPresent(none -> {
                    throw new BaseException(NICKNAME_ALREADY_EXISTS);
                });

        // 사용자 권한 정보 추가
        Set<AuthorityEntity> authorityEntity = new HashSet<>();
        authorityEntity.add(new AuthorityEntity("ROLE_USER"));
        authorityEntity.add(new AuthorityEntity("ROLE_PATIENT"));

        // 공통 코드 entity 구성
        CommonCodeDetailEntity userTypeCode = CommonCodeDetailEntity.builder()
                .id(codeDetailDto.getId())
                .codeDetail(codeDetailDto.getCodeDetail())
                .codeDetailName(codeDetailDto.getCodeDetailName())
                .commonCode(codeDetailDto.getCommonCode())
                .build();

        // UserEntity 구성 후 DB에 저장
        UserEntity userEntity = UserEntity.builder()
                .name(signUpDto.getName())
                .nickname(signUpDto.getNickname())
                .phoneNumber(signUpDto.getPhoneNumber())
                .fontSize(signUpDto.getFontSize())
                .authorities(authorityEntity)
                .userTypeCode(userTypeCode)
                .build();

        return userRepository.save(userEntity).getId();
    }

    public LoginResponseDto login(String phoneNumber) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(phoneNumber, "");

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AccessTokenDto accessToken = tokenProvider.createAccessToken(authentication);
        RefreshTokenDto refreshToken = tokenProvider.createRefreshToken(authentication);

        // redis에 refresh 토큰 정보 저장
        // key: RT:{userId}, value: refreshToken
        // (만료 시각 아닌 '유효 시간'으로)
        setRefreshTokenInRedis(refreshToken);

        UserEntity userEntity = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        PatientDetailDto patientDetailDto = PatientDetailDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .nickname(userEntity.getNickname())
                .fontSize(userEntity.getFontSize())
                .build();

        return LoginResponseDto.builder()
                .patientDetailDto(patientDetailDto)
                .tokenDto(new TokenDto(accessToken, refreshToken))
                .build();
    }

    public Optional<UserEntity> getUserWithAuthorities(String phoneNumber) {
        return userRepository.findOneWithAuthoritiesByPhoneNumberAndStatus(phoneNumber, Status.ACTIVE);
    }

    public void logout(TokenDto tokenDto) {

        if (!tokenProvider.isValidAccessToken(tokenDto.getAccessTokenDto().getAccessToken())) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCESS_TOKEN);
        }

        Authentication authentication =
                tokenProvider.getAuthenticationByAccessToken(tokenDto.getAccessTokenDto().getAccessToken());

        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:"+authentication.getName());
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        Long expiration = tokenProvider.getAccessTokenValidityInMilliseconds();
        redisTemplate.opsForValue().set(
                tokenDto.getAccessTokenDto().getAccessToken(),
                "logout",
                expiration,
                TimeUnit.MILLISECONDS);
    }

    public void deletePatientUser(Long userId) {

        // 사용자 status를 "INACTIVE"로 변경
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));
        userEntity.setStatus(Status.INACTIVE);
        userRepository.save(userEntity);
    }

    public PatientDetailDto getPatientUserDetail(Long id) {

        UserEntity user = userRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        return PatientDetailDto.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .fontSize(user.getFontSize())
                .build();
    }

    public TokenDto reGenerateTokens(AccessTokenDto accessTokenDto, RefreshTokenDto refreshTokenDto) {

        String refreshToken = refreshTokenDto.getRefreshToken();

        // 리프레시 토큰이 만료되었는지 확인
        if (tokenProvider.isExpiredRefreshToken(refreshToken)) {
            throw new BaseException(EXPIRED_REFRESH_TOKEN);
        }

        // 리프레시 토큰이 유효한지 확인
        if (tokenProvider.isValidRefreshToken(refreshToken)) {

            // 인증 정보에서 사용자 id를 조회
            Authentication authentication =
                    tokenProvider.getAuthenticationByAccessToken(accessTokenDto.getAccessToken());
            String userId = authentication.getName();

            // 사용자 id를 통해 Redis에서 리프레시 토큰을 조회하여 일치하는지 확인
            if (redisTemplate.opsForValue().get("RT:"+userId) != null) {
                // 두 토큰 모두 재생성 후 Redis 업데이트
                TokenDto newTokens = tokenProvider.reIssueAccessAndRefreshToken(refreshToken);
                setRefreshTokenInRedis(newTokens.getRefreshTokenDto());
                return newTokens;
            }
        }

        throw new BaseException(INVALID_REFRESH_TOKEN);
    }

    public void updateProfilesInfo(Long userId,
                                   ProfilesRequestDto profilesRequestDto,
                                   CommonCodeDetailDto codeDetailDto) throws BaseException {

        // 기존 사용자 프로필 정보 조회
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));


        // 닉네임 중복 체크 (조회된 유저가 자기 자신인 경우 제외)
        userRepository.findByNicknameAndStatus(profilesRequestDto.getNickname(), Status.ACTIVE)
                .ifPresent(userEntity1 -> {
                    if (!userEntity1.getId().equals(userId)) {
                        throw new BaseException(NICKNAME_ALREADY_EXISTS);
                    }
                });

        // 학력 코드 업데이트를 위한 공통 코드 엔티티 생성
        CommonCodeDetailEntity codeDetailEntity = CommonCodeDetailEntity.builder()
                .id(codeDetailDto.getId())
                .codeDetail(codeDetailDto.getCodeDetail())
                .codeDetailName(codeDetailDto.getCodeDetailName())
                .commonCode(codeDetailDto.getCommonCode())
                .build();

        // 새로운 정보로 유저 엔티티 업데이트 및 저장
        userEntity.updateProfiles(
                profilesRequestDto.getNickname(),
                profilesRequestDto.getProfileImgUrl(),
                codeDetailEntity
        );

        userRepository.save(userEntity);
    }

    public void updatePhoneNumber(Long userId, String phoneNumber) {

        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        // 전화번호 중복 체크 (조회된 유저가 자기 자신일 때는 제외)
        userRepository.findByPhoneNumberAndStatus(phoneNumber, Status.ACTIVE)
                .ifPresent(userEntity1 -> {
                    if (!userEntity1.getId().equals(userId)) {
                        throw new BaseException(USER_ALREADY_EXISTS);
                    }
                });

        userEntity.setPhoneNumber(phoneNumber);
        userRepository.save(userEntity);
    }

    public void updateFontSize(Long userId, Integer fontSize) {

        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        userEntity.setFontSize(fontSize);
        userRepository.save(userEntity);
    }

    private void setRefreshTokenInRedis(RefreshTokenDto refreshToken) {

        String userId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

        redisTemplate.opsForValue().set(
                "RT:"+userId,
                refreshToken.getRefreshToken(),
                refreshToken.getRefreshTokenExpiresTime(),
                TimeUnit.MILLISECONDS
        );
    }
}
