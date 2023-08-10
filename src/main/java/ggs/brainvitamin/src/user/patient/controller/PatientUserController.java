package ggs.brainvitamin.src.user.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.service.CommonCodeService;
import ggs.brainvitamin.src.user.patient.dto.ActivitiesDto;
import ggs.brainvitamin.src.user.patient.dto.ProfilesRequestDto;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import ggs.brainvitamin.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}
