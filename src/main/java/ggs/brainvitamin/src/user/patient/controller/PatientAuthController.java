package ggs.brainvitamin.src.user.patient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
//import ggs.brainvitamin.jwt.TokenProvider;
import ggs.brainvitamin.src.user.notification.sms.SmsService;
import ggs.brainvitamin.src.user.notification.sms.dto.MessageDto;
import ggs.brainvitamin.src.user.notification.sms.dto.SmsResponseDto;
import ggs.brainvitamin.src.user.patient.dto.UserDto;
import ggs.brainvitamin.src.user.patient.service.PatientAuthService;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientAuthController {

//    private final TokenProvider tokenProvider;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PatientAuthService patientAuthService;
    private final PatientUserService patientUserService;
    private final SmsService smsService;

    @PostMapping("/sms")
    public BaseResponse<String> sendSms(MessageDto messageDto) throws
            JsonProcessingException,
            RestClientException,
            URISyntaxException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException {
        try {
            SmsResponseDto responseDto = smsService.sendSms(messageDto);
            return new BaseResponse<>("인증번호를 발송하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @PostMapping("/signup")
    public BaseResponse<String> signUp(@Valid @RequestBody UserDto.signUpDto signUpDto)
    {
        try {
            patientUserService.signUp(signUpDto);
            return new BaseResponse<>("회원가입이 성공적으로 이루어졌습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
