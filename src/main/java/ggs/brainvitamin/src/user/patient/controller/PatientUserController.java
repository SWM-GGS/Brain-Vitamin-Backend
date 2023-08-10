package ggs.brainvitamin.src.user.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.service.CommonCodeService;
import ggs.brainvitamin.src.user.patient.dto.ActivitiesDto;
import ggs.brainvitamin.src.user.patient.dto.FontSizeDto;
import ggs.brainvitamin.src.user.patient.dto.PatientUserDto;
import ggs.brainvitamin.src.user.patient.dto.ProfilesRequestDto;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import ggs.brainvitamin.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ggs.brainvitamin.config.BaseResponseStatus.*;
import static ggs.brainvitamin.src.user.patient.dto.PatientUserDto.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientUserController {

    private final PatientUserService patientUserService;
    private final CommonCodeService commonCodeService;

//    @GetMapping("/")
//    public BaseResponse<ActivitiesDto> getMain() {
//        try {
//
//        }
//    }

    @GetMapping("/activities")
    public BaseResponse<ActivitiesDto> getActivities() {
        try {
            Long userId = Long.valueOf(1);  // 현재 접속 중인 User의 Id 받아오기
            return new BaseResponse<>(patientUserService.getActivities(userId));

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/profiles")
    public BaseResponse<String> setProfilesInfo(
            @Valid @RequestBody ProfilesRequestDto profilesRequestDto) {

        try {
            Long userId = Long.parseLong(SecurityUtil.getCurrentUserId().get());
            CommonCodeDetailDto codeDetailDto =
                    commonCodeService.getCodeWithCodeDetailName(profilesRequestDto.getEducation());
            patientUserService.updateProfilesInfo(userId, profilesRequestDto, codeDetailDto);

            return new BaseResponse<>("프로필 정보를 성공적으로 저장했습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/phone-number")
    public BaseResponse<String> setPhoneNumber(@Valid @RequestBody phoneNumberDto phoneNumberDto) {

        try {
            // 현재 로그인한 유저의 id값 조회
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));
            // 전화번호 업데이트
            patientUserService.updatePhoneNumber(Long.parseLong(userId), phoneNumberDto.getPhoneNumber());
            return new BaseResponse<>("전화번호가 성공적으로 저장되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/font-size")
    public BaseResponse<String> setFontSize(@Valid @RequestBody FontSizeDto fontSizeDto) {

        try {
            // 현재 로그인한 유저의 id값 조회
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

            patientUserService.updateFontSize(Long.parseLong(userId), fontSizeDto.getFontSize());
            return new BaseResponse<>("글자 크기가 성공적으로 저장되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
