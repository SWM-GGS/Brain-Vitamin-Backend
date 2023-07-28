package ggs.brainvitamin.src.post.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.post.patient.dto.*;
import ggs.brainvitamin.src.post.patient.service.PatientEmotionService;
import ggs.brainvitamin.src.post.patient.service.PatientPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient/family-stories")
@RequiredArgsConstructor
public class PatientPostController {

    private final PatientPostService patientPostService;
    private final PatientEmotionService patientEmotionService;

    /**
     * @param familyId
     * 환자용앱 우리 가족이야기 메인 페이지 조회
     */
    @GetMapping("/{familyId}")
    public BaseResponse<PostMainDto> getFamilyStoriesMain(@PathVariable("familyId") Long familyId) {
        try {
            return new BaseResponse<>(patientPostService.getFamilyStoriesMain(familyId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * @param postId
     * 환자용앱 우리가족 이야기 특정 게시글 조회
     */
    @GetMapping("/{familyId}/{postId}")
    public BaseResponse<PostDetailDto> getFamilyStoriesPost(@PathVariable("postId") Long postId) {
        try {
            return new BaseResponse<>(patientPostService.getFamilyStoriesPost(postId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     *
     * @param postId
     * @param emotionIdDto
     * 환자용앱 특정 게시글에 감정표현 하나 추가
     */
    @PostMapping("/{familyId}/{postId}/emotion")
    public BaseResponse<String> postEmotion(@PathVariable("postId") Long postId,
                                            @Valid @RequestBody EmotionIdDto emotionIdDto) {
        try {
            Long userId = Long.valueOf(2); // 테스트용 아이디, 로그인 기능 구현 이후에 추가 예정
            patientEmotionService.addEmotion(postId, userId, emotionIdDto.getId());

            return new BaseResponse<>("감정표현이 성공적으로 추가되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @DeleteMapping("{familyId}/{postId}/emotion")
    public BaseResponse<String> deleteEmotion(@PathVariable("postId") Long postId,
                                              @Valid @RequestBody EmotionIdDto emotionIdDto) {
        try {
            patientEmotionService.removeEmotion(postId, emotionIdDto.getId());
            return new BaseResponse<>("감정표현을 성공적으로 취소했습니다");

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}

