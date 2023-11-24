package ggs.brainvitamin.src.user.patient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.config.Season;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.jwt.TokenProvider;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.user.entity.AuthorityEntity;
import ggs.brainvitamin.src.user.entity.FamilyPictureEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.patient.dto.*;
import ggs.brainvitamin.src.user.repository.FamilyPictureRepository;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.service.ChatService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ggs.brainvitamin.config.BaseResponseStatus.*;
import static ggs.brainvitamin.src.user.patient.dto.PatientUserDto.*;
import static ggs.brainvitamin.src.user.patient.dto.TokenDto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientUserService {

    private final UserRepository userRepository;
    private final FamilyPictureRepository familyPictureRepository;
    private final ChatService chatService;

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
                .profileImgUrl(user.getProfileImgUrl())
                .education(user.getEducationCode().getCodeDetailName())
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

    public void createFamilyPicture(Long userId, FamilyPictureDto familyPictureDto) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        FamilyPictureEntity familyPictureEntity = new FamilyPictureEntity(
                userEntity,
                familyPictureDto.getImgUrl(),
                familyPictureDto.getSeason(),
                familyPictureDto.getYear(),
                familyPictureDto.getPlace(),
                familyPictureDto.getHeadCount());

        if (!familyPictureDto.getFamilyRelations().isEmpty()) {
            List<Integer> familyRelations = familyPictureDto.getFamilyRelations();

            String str = familyRelations.toString().replaceAll("[^0-9 ]","");
            System.out.println(str);

            familyPictureEntity.setFamilyRelations(str);
        }

        familyPictureRepository.save(familyPictureEntity);
    }

    public List<Map<String, Object>> getFamilyPicture(Long userId) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        List<Map<String, Object>> responseList= new ArrayList<>();

        List<FamilyPictureEntity> familyPictures = familyPictureRepository.findAllByUserAndStatus(userEntity, Status.ACTIVE);

        if (!familyPictures.isEmpty()) {

            for (FamilyPictureEntity familyPicture : familyPictures) {
                Map<String, Object> candidate = new HashMap<>();

                candidate.put("pictureId", familyPicture.getId());
                candidate.put("imgUrl", familyPicture.getImgUrl());

                responseList.add(candidate);
            }

        }
        return responseList;
    }

    public List<FamilyPictureProblemDto> getFamilyPictureProblems(Long userId) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        List<FamilyPictureProblemDto> response = new ArrayList<>();

        List<FamilyPictureEntity> familyPictureEntities = familyPictureRepository.findAllByUserAndStatus(userEntity, Status.ACTIVE);

        // 등록한 사진이 있는 경우 문제 생성
        if (!familyPictureEntities.isEmpty()) {
            List<Integer> randomPictureNumbers = new ArrayList<>();

            // 등록된 사진이 4개 이상인 경우 랜덤한 사진 3개 고르기
            if (familyPictureEntities.size() > 3) {
                // 중복 없이 랜덤 번호 저장하기 위한 Set
                Set<Integer> randomPictureNumberCandidate = new HashSet<>();

                // 중복 없이 3개까지 저장
                while (randomPictureNumberCandidate.size() < 3) {
                    randomPictureNumberCandidate.add((int) (Math.random() * familyPictureEntities.size()));
                }

                // 랜덤한 사진 총 3개 저장
                randomPictureNumbers = new ArrayList<>(randomPictureNumberCandidate);
            }
            // 등록된 사진 개수만큼 사진 번호 넣기
            else {
                if (familyPictureEntities.size() == 1) {
                    randomPictureNumbers.add(0);
                }
                else if (familyPictureEntities.size() == 2) {
                    randomPictureNumbers.add(0);
                    randomPictureNumbers.add(1);
                }
                else {
                    randomPictureNumbers.add(0);
                    randomPictureNumbers.add(1);
                    randomPictureNumbers.add(2);
                }
            }

            // 등록된 사진 하나당 문제 2개 생성
            for (int pictureNumber : randomPictureNumbers) {
                FamilyPictureEntity familyPictureEntity = familyPictureEntities.get(pictureNumber);

                // 첫번째 문제 번호
                int firstProblemNumber = (int) (Math.random() * 4); // 0 ~ 3 사이의 랜덤 숫자
                // 두번째 문제 번호
                int secondProblemNumber = (firstProblemNumber + 1) % 4;

                response.add(generatePictureProblem(familyPictureEntity, firstProblemNumber));
                response.add(generatePictureProblem(familyPictureEntity, secondProblemNumber));
            }
        }

        // 문제 셔플
        Collections.shuffle(response);

        return response;
    }

    private FamilyPictureProblemDto generatePictureProblem(FamilyPictureEntity familyPictureEntity, int problemNumber) {
        FamilyPictureProblemDto familyPictureProblemDto = new FamilyPictureProblemDto();

        List<String> problemScripts = Arrays.asList("어느 계절에 찍은 사진일까요?", "몇 년도에 찍은 사진일까요?", "어디서 찍은 사진일까요?", "사진에 있는 사람을 고르세요");

        // 문제 지문 저장
        familyPictureProblemDto.setScript(problemScripts.get(problemNumber));
        // 문제 이미지 링크 저장
        familyPictureProblemDto.setImgUrl(familyPictureEntity.getImgUrl());

        List<Object> choices = new ArrayList<>();

        // 계절 문제
        if (problemNumber == 0) {
            // 문제 타입 저장
            familyPictureProblemDto.setProblemType("SEASON");

            // 사진 계절
            Season season = familyPictureEntity.getSeason();

            // 보기 중 정답 인덱스 추출
            int answerIndex = 0;

            if (season.equals(Season.SUMMER)) {
                answerIndex = 1;
            }
            else if (season.equals(Season.FALL)) {
                answerIndex = 2;
            }
            else {
                answerIndex = 3;
            }

            familyPictureProblemDto.setAnswerIndex(answerIndex);

            choices.add("봄");
            choices.add("여름");
            choices.add("가을");
            choices.add("겨울");
        }

        // 년도 문제
        else if (problemNumber == 1) {
            // 문제 타입 저장
            familyPictureProblemDto.setProblemType("YEAR");

            // 사진 년도
            Integer year = familyPictureEntity.getYear();

            // 보기 중 정답 인덱스 랜덤 추출
            int answerIndex = (int) (Math.random() * 3);
            familyPictureProblemDto.setAnswerIndex(answerIndex);

            if (answerIndex == 0) {
                choices.add(year);
                choices.add(year + 5);
                choices.add(year + 10);
            }
            else if (answerIndex == 1) {
                choices.add(year - 5);
                choices.add(year);
                choices.add(year + 5);
            }
            else {
                choices.add(year- 10);
                choices.add(year - 5);
                choices.add(year);
            }
        }

        // 장소 문제
        else if (problemNumber == 2) {
            // 문제 타입 저장
            familyPictureProblemDto.setProblemType("PLACE");

            // 보기 중 정답 인덱스 랜덤 추출
            int answerIndex = (int) (Math.random() * 3);
            familyPictureProblemDto.setAnswerIndex(answerIndex);

            // 보기 저장
            String beforeStr1 = familyPictureEntity.getPlace() + "와 비슷한 대한민국 장소 하나를 알려줘. 답변은 다음과 같이 부가 설명 없이 장소 명만 알려줘. 답변 예시 : 경복궁";
            float temperature = 0.6f;

            Pattern p = Pattern.compile("([가-힣|\s]+)");	// 검색할 문자열 패턴 : 한글, 공백 문자

            // GPT 응답
            String afterStr1 = getGptResponseText(chatService.getChatResponse(beforeStr1, temperature, 500)).trim();

            Matcher m = p.matcher(afterStr1);
            if (m.find()) {
                afterStr1 = m.group();
            }

            String beforeStr2 = familyPictureEntity.getPlace() + "와 비슷한 대한민국 장소 중 " + afterStr1 + "을 제외하고 하나만 알려줘. 답변은 다음과 같이 부가 설명 없이 장소 명만 알려줘. 답변 예시 : 경복궁";

            // GPT 응답
            String afterStr2 = getGptResponseText(chatService.getChatResponse(beforeStr2, temperature, 500)).trim();

            while (afterStr2.contains(afterStr1) | afterStr2.contains(familyPictureEntity.getPlace())) {
                afterStr2 = getGptResponseText(chatService.getChatResponse(beforeStr2, temperature, 500)).trim();
            }

            m = p.matcher(afterStr2);
            if (m.find()) {
                afterStr2 = m.group();
            }

            String[] candidate = new String[3];
            candidate[answerIndex] = familyPictureEntity.getPlace();

            int count = 0;

            for (int i = 0; i < 3; i++) {
                if (i != answerIndex) {
                    if (count == 0) {
                        candidate[i] = afterStr1;
                        count++;
                    }
                    else {
                        candidate[i] = afterStr2;
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
                choices.add(candidate[i]);
            }

        }

        // 사진 속 인물 문제
        else {
            // 문제 타입 저장
            familyPictureProblemDto.setProblemType("PERSON");

            List<String> familyRelationName = Arrays.asList("", "배우자", "아들", "딸", "아버지", "어머니", "며느리", "사위", "할아버지", "할머니",
                    "손자", "손녀", "형제", "자매", "사촌", "고모", "이모", "삼촌", "외삼촌", "조카", "조카딸", "외할머니", "외할아버지");

            // 보기 중 정답 인덱스 랜덤 추출
            int answerIndex = (int) (Math.random() * 3);
            familyPictureProblemDto.setAnswerIndex(answerIndex);

            // 보기 저장
            String[] familyRelations = familyPictureEntity.getFamilyRelations().split(" ");
            List<Integer> familyRelationsNumbers = new ArrayList<>();

            for (String familyRelation : familyRelations) {
                familyRelationsNumbers.add(Integer.parseInt(familyRelation));
            }

            List<Integer> exceptNumbers = new ArrayList<>(familyRelationsNumbers);

            String[] candidate = new String[3];

            for (int i = 0; i < 3; i++) {
                if (i == answerIndex) {
                    int randomNumber = (int) (Math.random() * familyRelationsNumbers.size());

                    candidate[answerIndex] = familyRelationName.get(familyRelationsNumbers.get(randomNumber));

                    exceptNumbers.add(randomNumber);
                }
                else {
                    boolean check = true;

                    while (check) {
                        int randomNumber = (int) (Math.random() * 22) + 1; // 1 ~ 22 랜덤 수

                        if (!exceptNumbers.contains(randomNumber)) {
                            candidate[i] = familyRelationName.get(randomNumber);
                            exceptNumbers.add(randomNumber);
                            check = false;
                        }
                    }

                }
            }

            for (int i = 0; i < 3; i++) {
                choices.add(candidate[i]);
            }
        }

        // 객관식 보기 저장
        familyPictureProblemDto.setChoices(choices);

        return familyPictureProblemDto;
    }

    // GPT 응답 추출 메서드
    public String getGptResponseText(Map response) {
        Object choices = response.get("choices");
        List<?> objects = convertObjectToList(choices);
        Object textObject = objects.get(0);

        ObjectMapper objectMapper = new ObjectMapper();

        // convert object to map
        Map<String, Object> map = objectMapper.convertValue(textObject, Map.class);

        return String.valueOf(map.get("text"));
    }

    // Object 객체를 List로 변환
    public List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }
}
