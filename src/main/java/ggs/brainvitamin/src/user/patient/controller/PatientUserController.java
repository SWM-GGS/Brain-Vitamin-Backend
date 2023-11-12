package ggs.brainvitamin.src.user.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.service.CommonCodeService;
import ggs.brainvitamin.src.user.patient.dto.*;
import ggs.brainvitamin.src.user.patient.service.PatientUserService;
import ggs.brainvitamin.src.vitamin.service.ScreeningTestHistoryService;
import ggs.brainvitamin.src.vitamin.service.VitaminAnalyticsService;
import ggs.brainvitamin.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static ggs.brainvitamin.config.BaseResponseStatus.*;
import static ggs.brainvitamin.src.user.patient.dto.ActivitiesDto.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient API")
public class PatientUserController {

    private final PatientUserService patientUserService;
    private final CommonCodeService commonCodeService;
    private final ScreeningTestHistoryService screeningTestHistoryService;
    private final VitaminAnalyticsService vitaminAnalyticsService;

    @GetMapping("/activities")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 내 활동 보기 데이터 조회")
    public BaseResponse<ActivitiesResponseDto> getActivities() {
        try {
            String currentUserId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));
            Long userId = Long.parseLong(currentUserId);

            LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

            // 이번주 간 비타민 참여 여부 조회
            GetWeeklyVitaminDto weeklyVitaminDto =
                    vitaminAnalyticsService.getWeeklyVitaminAttendance(userId, today);

            // 지난주 대비 인지능력 변화 조회
            GetChangesFromLastWeekDto changesFromLastWeekDto =
                    vitaminAnalyticsService.getChangesFromLastWeek(userId, today);

            // 가장 최근 선별검사 결과 조회
            GetScreeningTestHistoryDto screeningTestHistoryDto =
                    screeningTestHistoryService.getScreeningTestHistory(userId);

            // ResponseDto 구성
            ActivitiesResponseDto activitiesResponseDtO = ActivitiesResponseDto.builder()
                    .weeklyVitaminDto(weeklyVitaminDto)
                    .changesFromLastWeekDto(changesFromLastWeekDto)
                    .screeningTestHistoryDto(screeningTestHistoryDto)
                    .build();

            return new BaseResponse<>(activitiesResponseDtO);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/profiles")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 프로필 정보 수정", description = "")
    public BaseResponse<String> setProfilesInfo(
            @Valid @RequestBody ProfilesRequestDto profilesRequestDto) {

        try {
            Long userId = Long.parseLong(SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO)));

            CommonCodeDetailDto codeDetailDto =
                    commonCodeService.getCodeWithCodeDetailName(profilesRequestDto.getEducation());
            patientUserService.updateProfilesInfo(userId, profilesRequestDto, codeDetailDto);

            return new BaseResponse<>("프로필 정보를 성공적으로 저장했습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/phone-number")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 전화번호 수정", description = "")
    public BaseResponse<String> setPhoneNumber(@Valid @RequestBody PatientUserDto.PhoneNumberDto phoneNumberDto) {

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

    @PostMapping("/family-stories/pictures")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "환자 가족 사진 추가", description = "")
    public BaseResponse<String> createFamilyPicture(@Valid @RequestBody FamilyPictureDto familyPictureDto) {

        try {
            // 현재 로그인한 유저의 id값 조회
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

            patientUserService.createFamilyPicture(Long.parseLong(userId), familyPictureDto);

            return new BaseResponse<>("환자 가족 사진이 성공적으로 저장되었습니다.");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/family-stories/pictures")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "환자 가족 사진 조회", description = "")
    public BaseResponse<List<Map<String, Object>>> getFamilyPicture() {

        try {
            // 현재 로그인한 유저의 id값 조회
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

            List<Map<String, Object>> responseMap = patientUserService.getFamilyPicture(Long.parseLong(userId));

            return new BaseResponse<>(responseMap);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/family-stories/problems")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @Operation(summary = "가족 사진 기반 문제 조회", description = "")
    public BaseResponse<List<FamilyPictureProblemDto>> getFamilyPictureProblems() {

        try {
            // 현재 로그인한 유저의 id값 조회
            String userId = SecurityUtil.getCurrentUserId()
                    .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

            List<FamilyPictureProblemDto> responseMap = patientUserService.getFamilyPictureProblems(Long.parseLong(userId));

            return new BaseResponse<>(responseMap);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/font-size")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 글자크기 수정", description = "")
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
