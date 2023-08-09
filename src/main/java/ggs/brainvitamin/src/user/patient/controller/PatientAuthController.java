package ggs.brainvitamin.src.user.patient.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.jwt.TokenProvider;
import ggs.brainvitamin.src.common.Service.CommonCodeService;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.user.notification.sms.SmsService;
import ggs.brainvitamin.src.user.notification.sms.dto.MessageDto;
import ggs.brainvitamin.src.user.notification.sms.dto.SmsResponseDto;
import ggs.brainvitamin.src.user.patient.dto.TokenDto;
import ggs.brainvitamin.src.user.patient.dto.UserDto;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import ggs.brainvitamin.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ggs.brainvitamin.src.user.patient.dto.TokenDto.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientAuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PatientUserService patientUserService;
    private final SmsService smsService;
    private final CommonCodeService commonCodeService;
    private final RedisTemplate<String, Object> redisTemplate;

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
    public BaseResponse<TokenDto> signUp(@Valid @RequestBody UserDto.signUpDto signUpDto) {
        try {
            CommonCodeDetailDto codeDetailDto = commonCodeService.getCodeWithCodeDetailName("환자");
            patientUserService.signUp(signUpDto, codeDetailDto);

            // 회원가입 완료 후 바로 로그인
            return new BaseResponse<>(patientUserService.login(signUpDto.getPhoneNumber()));

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/login")
    public BaseResponse<TokenDto> login(@RequestBody UserDto.loginDto loginDto) {
        try {
            return new BaseResponse<>(patientUserService.login(loginDto.getPhoneNumber()));

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    @PostMapping("/logout")
//    public BaseResponse<String> logout(TokenDto tokenDto) {
//
//        try {
//            patientUserService.logout(tokenDto);
//            return new BaseResponse<>("로그아웃이 완료되었습니다.");
//
//        } catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//    }
}
