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

@Service
@RequiredArgsConstructor
@Transactional
public class PatientUserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Long createPatientUser(signUpDto signUpDto, CommonCodeDetailDto codeDetailDto) throws BaseException {

        userRepository.findByPhoneNumberAndStatus(signUpDto.getPhoneNumber(), Status.ACTIVE)
                .ifPresent(none -> {
                    throw new BaseException(USER_ALREADY_EXISTS);
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

    public loginResponseDto login(String phoneNumber) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(phoneNumber, "");

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto.AccessTokenDto accessToken = tokenProvider.createAccessToken(authentication);
        TokenDto.RefreshTokenDto refreshToken = tokenProvider.createRefreshToken(authentication);

        // redis에 refresh 토큰 정보 저장 (만료 시각 아닌 '유효 시간'으로)
            redisTemplate.opsForValue().set(
                    "RT:"+SecurityUtil.getCurrentUserId().get(),
                    refreshToken.getRefreshToken(),
                    refreshToken.getRefreshTokenExpiresTime(),
                    TimeUnit.MILLISECONDS
            );

        UserEntity userEntity = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        PatientDetailDto patientDetailDto = PatientDetailDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .nickname(userEntity.getNickname())
                .fontSize(userEntity.getFontSize())
                .build();

        return loginResponseDto.builder()
                .patientDetailDto(patientDetailDto)
                .tokenDto(new TokenDto(accessToken, refreshToken))
                .build();
    }

    public Optional<UserEntity> getUserWithAuthorities(String phoneNumber) {
        return userRepository.findOneWithAuthoritiesByPhoneNumberAndStatus(phoneNumber, Status.ACTIVE);
    }

    public void logout(TokenDto tokenDto) {

        if (!tokenProvider.isValidAccessToken(tokenDto.getAccessTokenDto().getAccessToken())) {
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        Authentication authentication =
                tokenProvider.getAuthenticationByAccessToken(tokenDto.getAccessTokenDto().getAccessToken());

        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:"+authentication.getName());
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        Long expiration = tokenProvider.getAccessTokenValidityInMilliseconds();
        redisTemplate.opsForValue().set(tokenDto.getAccessTokenDto().getAccessToken(),"logout", expiration, TimeUnit.MILLISECONDS);
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

    public ActivitiesDto getActivities(Long id) throws BaseException {

        // 최근 일주일 두뇌 비타민 참여 현황 데이터

        // 최근 일주일 간 영역별 인지 능력 데이터 (두뇌 비타민 결과)

        // 그 지난 일주일 간 영역별 인지 능력 데이터 (두뇌 비타민 결과)

        // 가장 최근 인지선별검사 결과 및 해석

        return new ActivitiesDto();
    }

}
