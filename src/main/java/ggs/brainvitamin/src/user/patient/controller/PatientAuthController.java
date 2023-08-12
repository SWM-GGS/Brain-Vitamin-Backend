package ggs.brainvitamin.src.user.patient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.common.service.CommonCodeService;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.user.notification.sms.SmsService;
import ggs.brainvitamin.src.user.notification.sms.dto.MessageDto;
import ggs.brainvitamin.src.user.notification.sms.dto.SmsResponseDto;
import ggs.brainvitamin.src.user.patient.dto.FamilyDto;
import ggs.brainvitamin.src.user.patient.dto.PatientUserDto;
import ggs.brainvitamin.src.user.patient.dto.TokenDto;
import ggs.brainvitamin.src.user.patient.service.PatientFamilyService;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import ggs.brainvitamin.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static ggs.brainvitamin.config.BaseResponseStatus.*;
import static ggs.brainvitamin.src.user.patient.dto.PatientUserDto.*;
import static ggs.brainvitamin.src.user.patient.dto.TokenDto.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient API")
public class PatientAuthController {

    private final PatientUserService patientUserService;
    private final SmsService smsService;
    private final CommonCodeService commonCodeService;
    private final PatientFamilyService patientFamilyService;


    @PostMapping("/sms")
    @Operation(summary = "환자 인증번호 문자 요청", description = "")
    public BaseResponse<SmsResponseDto> sendSms(@RequestBody MessageDto messageDto) throws
            JsonProcessingException,
            RestClientException,
            URISyntaxException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException {
        try {
            SmsResponseDto responseDto = smsService.sendSms(messageDto);
            return new BaseResponse<>(responseDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @PostMapping("/signup")
    @Operation(summary = "환자 회원가입", description = "")
    public BaseResponse<LoginResponseDto> signUp(@Valid @RequestBody PatientUserDto.SignUpDto signUpDto) {
        try {
            // 함께 저장할 공통코드 정보 조회
            CommonCodeDetailDto codeDetailDto = commonCodeService.getCodeWithCodeDetailName("환자");
            // DB에 유저 insert
            Long createdUserId = patientUserService.createPatientUser(signUpDto, codeDetailDto);

            // 추가한 유저 정보 조회하고 가족 생성 및 환자를 가족 멤버로 등록
            PatientDetailDto patientDetailDto = patientUserService.getPatientUserDetail(createdUserId);
            patientFamilyService.createFamily(patientDetailDto);

            // 회원가입 완료 후 로그인 처리
            return new BaseResponse<>(patientUserService.login(signUpDto.getPhoneNumber()));

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "환자 로그인", description = "")
    public BaseResponse<LoginResponseDto> login(@RequestBody PhoneNumberDto phoneNumberDto) {
        try {
            // 로그인 후 사용자 정보 조회
            LoginResponseDto loginResponseDto = patientUserService.login(phoneNumberDto.getPhoneNumber());
            // 사용자 정보 바탕으로 가족 코드 조회
            FamilyDto familyInfo = patientFamilyService.getFamilyInfo(loginResponseDto.getPatientDetailDto().getId());
            loginResponseDto.getPatientDetailDto().setFamilyKey(familyInfo.getFamilyKey());

            return new BaseResponse<>(loginResponseDto);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "환자 로그아웃", description = "")
    public BaseResponse<String> logout(@Valid @RequestBody TokenDto tokenDto) {

        try {
            patientUserService.logout(tokenDto);
            return new BaseResponse<>("로그아웃이 완료되었습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/signout")
    @Operation(summary = "환자 회원탈퇴", description = "")
    public BaseResponse<String> signOut(@Valid @RequestBody TokenDto tokenDto) {

        try {
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

            // 사용자 비활성화
            patientUserService.deletePatientUser(Long.parseLong(userId));
            // 가족 그룹 비활성화 및 그룹 멤버 비활성화
            FamilyDto familyInfo = patientFamilyService.getFamilyInfo(Long.parseLong(userId));
            patientFamilyService.deleteFamily(familyInfo.getId());

            // 환자 가족 내 커뮤니티 전체 비활성화 작업 추가 예정

            // 탈퇴 작업 완료 후 로그아웃 처리
            patientUserService.logout(tokenDto);

            return new BaseResponse<>("회원 탈퇴가 완료되었습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/reissue-tokens")
    @Operation(summary = "환자 토큰 재발급", description = "")
    public BaseResponse<LoginResponseDto> reIssueTokens(@Valid @RequestBody TokenDto tokenDto) {

        try {
            AccessTokenDto accessToken = tokenDto.getAccessTokenDto();
            RefreshTokenDto refreshToken = tokenDto.getRefreshTokenDto();

            // 두 토큰이 모두 존재하는지 확인
            if (!StringUtils.hasText(accessToken.getAccessToken()))
                return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
            if (!StringUtils.hasText(refreshToken.getRefreshToken()))
                return new BaseResponse<>(EMPTY_REFRESH_TOKEN);

            // 토큰 재생성
            TokenDto newTokens = patientUserService.reGenerateTokens(accessToken, refreshToken);

            // 리프레시된 인증 정보를 바탕으로 다시 사용자 정보를 조회해서 반환
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

            PatientDetailDto patientDetailDto = patientUserService.getPatientUserDetail(Long.parseLong(userId));

            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                    .patientDetailDto(patientDetailDto)
                    .tokenDto(newTokens)
                    .build();

            return new BaseResponse<>(loginResponseDto);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
