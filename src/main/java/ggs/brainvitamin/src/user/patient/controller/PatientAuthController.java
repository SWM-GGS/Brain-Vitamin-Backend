package ggs.brainvitamin.src.user.patient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.common.Service.CommonCodeService;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.user.notification.sms.SmsService;
import ggs.brainvitamin.src.user.notification.sms.dto.MessageDto;
import ggs.brainvitamin.src.user.notification.sms.dto.SmsResponseDto;
import ggs.brainvitamin.src.user.patient.dto.FamilyDto;
import ggs.brainvitamin.src.user.patient.dto.TokenDto;
import ggs.brainvitamin.src.user.patient.service.PatientFamilyService;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static ggs.brainvitamin.src.user.patient.dto.PatientUserDto.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientAuthController {

    private final PatientUserService patientUserService;
    private final SmsService smsService;
    private final CommonCodeService commonCodeService;
    private final PatientFamilyService patientFamilyService;

    @PostMapping("/sms")
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
    public BaseResponse<loginResponseDto> signUp(@Valid @RequestBody signUpDto signUpDto) {
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
    public BaseResponse<loginResponseDto> login(@RequestBody loginRequestDto loginDto) {
        try {
            // 로그인 후 사용자 정보 조회
            loginResponseDto loginResponseDto = patientUserService.login(loginDto.getPhoneNumber());
            // 사용자 정보 바탕으로 가족 코드 조회
            FamilyDto familyInfo = patientFamilyService.getFamilyInfo(loginResponseDto.getPatientDetailDto().getId());
            loginResponseDto.getPatientDetailDto().setFamilyKey(familyInfo.getFamilyKey());

            return new BaseResponse<>(loginResponseDto);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/logout")
    public BaseResponse<String> logout(@Valid @RequestBody TokenDto tokenDto) {

        try {
            patientUserService.logout(tokenDto);
            return new BaseResponse<>("로그아웃이 완료되었습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
