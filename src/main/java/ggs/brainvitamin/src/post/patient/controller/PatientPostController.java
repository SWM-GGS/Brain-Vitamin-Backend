package ggs.brainvitamin.src.post.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.common.dto.CommonCodeDetailDto;
import ggs.brainvitamin.src.common.service.CommonCodeService;
import ggs.brainvitamin.src.post.patient.dto.*;
import ggs.brainvitamin.src.post.patient.service.PatientEmotionService;
import ggs.brainvitamin.src.post.patient.service.PatientPostService;
import ggs.brainvitamin.src.user.patient.dto.FamilyMemberDto;
import ggs.brainvitamin.src.user.patient.service.PatientFamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient/family-stories")
@RequiredArgsConstructor
@Tag(name = "Patient", description = "Patient API")
public class PatientPostController {

    private final PatientPostService patientPostService;
    private final CommonCodeService commonCodeService;
    private final PatientEmotionService patientEmotionService;
    private final PatientFamilyService patientFamilyService;

    /**
     * @param familyId
     * 환자용앱 우리 가족이야기 메인 페이지 조회
     */
    @GetMapping("/{familyId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 우리가족 이야기 메인 화면 조회", description = "")
    public BaseResponse<PostMainDto> getFamilyStoriesMain(@PathVariable("familyId") Long familyId) {
        try {
            // 현재 가족 그룹의 모든 게시글 조회
            List<PostPreviewDto> familyStoriesPosts = patientPostService.getFamilyStoriesAllPosts(familyId);
            // 감정표현 공통코드 정보 전체 조회
            List<CommonCodeDetailDto> emotionInfos = commonCodeService.getCodeInfosWithCode("EMOT");
            // 환자 가족 그룹 내 모든 멤버 정보 조회
            List<FamilyMemberDto> familyMembers = patientFamilyService.getAllFamilyMembers(familyId);

            PostMainDto postMainDto = PostMainDto.builder()
                    .postPreviewDtoList(familyStoriesPosts)
                    .emotionInfoDtoList(emotionInfos)
                    .familyMemberDtoList(familyMembers)
                    .build();

            return new BaseResponse<>(postMainDto);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * @param postId
     * 환자용앱 우리가족 이야기 특정 게시글 조회
     */
    @GetMapping("/{familyId}/{postId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 우리가족 이야기 특정 게시글 조회", description = "")
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
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 우리 가족이야기 게시글 감정표현 추가", description = "")
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

    /**
     *
     * @param postId
     * @param emotionIdDto
     * 환자용앱 특정 게시글 감정표현 취소
     */
    @DeleteMapping("{familyId}/{postId}/emotion")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_PATIENT')")
    @Operation(summary = "환자 우리가족 이야기 게시글 감정표현 취소", description = "")
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

