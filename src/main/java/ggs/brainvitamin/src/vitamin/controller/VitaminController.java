package ggs.brainvitamin.src.vitamin.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.vitamin.dto.request.PostCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostScreeningTestDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostUserDetailDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetPatientHomeDto;
import ggs.brainvitamin.src.vitamin.service.VitaminService;
import ggs.brainvitamin.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class VitaminController {

    private final VitaminService vitaminService;

    /**
     * 환자 홈 화면 조회
     */
    @GetMapping("")
    public BaseResponse<GetPatientHomeDto> getPatientHome() {
        try {
            Long userId = getUserId();

            GetPatientHomeDto getPatientHomeDto = vitaminService.getPatientHome(userId);

            return new BaseResponse<>(getPatientHomeDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 인지 선별 검사를 위한 회원 정보 받기
     */
    @PostMapping("/vitamins/user-details")
    public BaseResponse<String> setUserDetails(@RequestBody PostUserDetailDto postUserDetailDto) {
        try {
            Long userId = getUserId();

            vitaminService.setUserDetails(userId, postUserDetailDto);

            return new BaseResponse<>("회원 정보 받기 완료!");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 두뇌 비타민 인지 향상 게임 데이터 조회
     * @return List<GetCogTrainingDto>
     */
    @GetMapping("/vitamins/cog-training")
    public BaseResponse<List<Map<String, Object>>> getCogTraining() {
        try {
            Long userId = getUserId();

            List<Map<String, Object>> responseMap = vitaminService.getCogTraining(userId);

            return new BaseResponse<>(responseMap);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 두뇌 비타민 중단 및 종료
     */
    @PostMapping("/vitamins/cog-training")
    public BaseResponse<String> determinateCogTraining(@RequestBody PostCogTrainingDto postCogTrainingDto) {
        try {
            Long userId = getUserId();

            String result = vitaminService.determinateCogTraining(userId, postCogTrainingDto);

            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 인지 선별검사 질문 조회
     */
    @GetMapping("/vitamins/screening-test")
    public BaseResponse<List<Map<String, Object>>> getScreeningTest() {
        try {
            Long userId = getUserId();

            List<Map<String, Object>> responseMap = vitaminService.getScreeningTest(userId);

            return new BaseResponse<>(responseMap);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 인지 선별검사 제출
     */
    @PostMapping("/vitamins/screening-test")
    public BaseResponse<Map<String, Object>> submitScreeningTest(@RequestBody PostScreeningTestDto postScreeningTestDto) {
        try {
            Long userId = getUserId();

            Map<String, Object> responseMap = vitaminService.submitScreeningTest(userId, postScreeningTestDto);

            return new BaseResponse<>(responseMap);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    private static Long getUserId() {
        String currentUserId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));
        Long userId = Long.parseLong(currentUserId);
        return userId;
    }

}
