package ggs.brainvitamin.src.post.guardian.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.post.guardian.dto.FamilyPostMainDto;
import ggs.brainvitamin.src.post.guardian.service.GuardianFamilyPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guardian")
@RequiredArgsConstructor
@Tag(name = "Guardian", description = "Guardian API")
public class GuardianPostController {

    private final GuardianFamilyPostService guardianFamilyPostService;

    // family-stories 우리 가족 이야기 관련 컨트롤러 함수
    @GetMapping("/family-stories/{familyId}")
    @Operation(summary = "보호자 우리가족 이야기 메인 화면 조회", description = "")
    public BaseResponse<FamilyPostMainDto> getFamilyStoriesMain(@PathVariable("familyId") Long familyId) {
        try {
            Long userId = Long.parseLong("2");
            FamilyPostMainDto familyPostMainDto = guardianFamilyPostService.getFamilyStoriesMain(familyId, userId);
            return new BaseResponse<>(familyPostMainDto);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    // neighbor-stories 우리 가족 이야기 관련 컨트롤러 함수
}
