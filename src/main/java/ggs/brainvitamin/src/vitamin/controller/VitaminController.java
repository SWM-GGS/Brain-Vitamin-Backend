package ggs.brainvitamin.src.vitamin.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.vitamin.dto.request.PostCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostScreeningTestDetailDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostScreeningTestDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostUserDetailDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetPatientHomeDto;
import ggs.brainvitamin.src.vitamin.service.S3UploadService;
import ggs.brainvitamin.src.vitamin.service.VitaminService;
import ggs.brainvitamin.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Tag(name = "Patient", description = "Patient API")
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class VitaminController {

    private final VitaminService vitaminService;
    private final S3UploadService s3UploadService;

    /**
     * 환자 홈 화면 조회
     */
    @Operation(summary = "환자 홈 화면 조회", description = "")
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
    @Operation(summary = "인지 선별 검사를 위한 회원 정보 받기", description = "")
    @PostMapping("/vitamins/user-details")
    public BaseResponse<String> setUserDetails(@Valid @RequestBody PostUserDetailDto postUserDetailDto) {
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
     */
    @Operation(summary = "두뇌 비타민 인지 향상 게임 데이터 조회", description = "")
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
    @Operation(summary = "두뇌 비타민 중단 및 종료", description = "")
    @PostMapping("/vitamins/cog-training")
    public BaseResponse<String> determinateCogTraining(@Valid @RequestBody PostCogTrainingDto postCogTrainingDto) {
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
    @Operation(summary = "인지 선별검사 질문 조회", description = "")
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
     * 인지 선별검사 문제별 답안 제출
     */
    @Operation(summary = "인지 선별검사 문제별 답안 제출", description = "")
    @PostMapping("/vitamins/screening-test/detail")
    public BaseResponse<Map<String, Object>> submitScreeningTest(@Valid @RequestBody PostScreeningTestDetailDto postScreeningTestDetailDto) {
        try {
            Long userId = getUserId();

            Map<String, Object> responseMap = vitaminService.checkScreeningTestDetail(userId, postScreeningTestDetailDto);

            return new BaseResponse<>(responseMap);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 인지 선별검사 문제별 답안 제출 테스트
     */
    @Operation(summary = "인지 선별검사 문제별 답안 제출 테스트", description = "")
    @PostMapping("/vitamins/screening-test/detail/test")
    public BaseResponse<Map<String, Object>> submitScreeningTestTemp(
            @RequestPart(name = "audioFile", required = false) MultipartFile multipartFile,
            @RequestParam String jsonData) {
        try {
            Long userId = getUserId();

            PostScreeningTestDetailDto postScreeningTestDetailDto = convertJsonData(jsonData);

            postScreeningTestDetailDto = updatePostScreeningTestDetailDto(postScreeningTestDetailDto, multipartFile);

            Map<String, Object> responseMap = vitaminService.checkScreeningTestDetail(
                    userId,
                    postScreeningTestDetailDto);

            return new BaseResponse<>(responseMap);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    private PostScreeningTestDetailDto convertJsonData(String jsonData) throws BaseException {
        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new SimpleModule());
            PostScreeningTestDetailDto postScreeningTestDetailDto = objectMapper.readValue(jsonData, new TypeReference<>() {});

            return postScreeningTestDetailDto;
        }
        catch (Exception exception) {
            throw new BaseException(FAILED_TO_CONVERT_JSON);
        }
    }

    private PostScreeningTestDetailDto updatePostScreeningTestDetailDto(PostScreeningTestDetailDto postScreeningTestDetailDto, MultipartFile multipartFile) throws BaseException {
        try {
            if (postScreeningTestDetailDto.getScreeningTestId() != 42) {
                String fileUrl = s3UploadService.saveFile(multipartFile);

                postScreeningTestDetailDto.setAudioFileUrl(fileUrl);
            }

            return postScreeningTestDetailDto;
        }
        catch (Exception exception) {
            throw new BaseException(FAILED_TO_SAVE_AUDIO_FILE);
        }
    }

    /**
     * 인지 선별검사 제출
     */
    @Operation(summary = "인지 선별검사 제출", description = "")
    @PostMapping("/vitamins/screening-test")
    public BaseResponse<Map<String, Object>> submitScreeningTest(@Valid @RequestBody PostScreeningTestDto postScreeningTestDto) {
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
                .orElseThrow(() -> new BaseException(INVALID_LOGIN_INFO));

        return Long.parseLong(currentUserId);
    }

}
