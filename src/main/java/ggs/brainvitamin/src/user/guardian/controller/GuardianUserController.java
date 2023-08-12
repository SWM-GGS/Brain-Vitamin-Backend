package ggs.brainvitamin.src.user.guardian.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.post.patient.dto.EmotionIdDto;
import ggs.brainvitamin.src.user.guardian.dto.*;
import ggs.brainvitamin.src.user.guardian.service.GuardianFamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.representer.BaseRepresenter;

import java.util.List;

@RestController
@RequestMapping("/guardian")
@RequiredArgsConstructor
@Tag(name = "Guardian", description = "Guardian API")
public class GuardianUserController {

    private final GuardianFamilyService guardianFamilyService;

    /**
     * @return
     * 가족 그룹 리스트 조회 함수
     */
    @GetMapping("/family-group")
    @Operation(summary = "환자 가입된 가족 그룹 리스트 조회", description = "")
    public BaseResponse<List<FamilyGroupPreviewDto>> getFamilyGroupList() {
        try {
            Long userId = Long.parseLong("2");  // 인증 기능 추가 시 구현 예정
            List<FamilyGroupPreviewDto> familyGroupPreviewDtoList = guardianFamilyService.getFamilyGroupList(userId);
            return new BaseResponse<>(familyGroupPreviewDtoList);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/family-group/{familyKey}")
    @Operation(summary = "보호자 가족 그룹 상세정보 조회", description = "")
    public BaseResponse<FamilyGroupDetailDto> getFamilyGroupDetail(@PathVariable("familyKey") String familyKey) {
        try {
            return new BaseResponse<>(guardianFamilyService.findFamilyGroupWithFamilyKey(familyKey));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 새로운 가족 그룹 가입 함수
     * @param familyGroupJoinDto
     * @return
     */
    @PostMapping("/family-group")
    @Operation(summary = "보호자 가족 그룹 가입", description = "")
    public BaseResponse<String> postJoinFamilyGroup(@Valid @RequestBody FamilyGroupJoinDto familyGroupJoinDto) {
        try {
            Long userId = Long.parseLong("2"); // 인증 기능 구현 후 추가 예정
            guardianFamilyService.joinFamilyGroup(familyGroupJoinDto, userId);

            return new BaseResponse<>("가족 그룹에 성공적으로 가입하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 가입된 가족 그룹 탈퇴 함수
     * @param familyGroupQuitDto
     * @return
     */
    @DeleteMapping("/family-group")
    @Operation(summary = "보호자 가족 그룹 탈퇴", description = "")
    public BaseResponse<String> deleteJoinedFamilyGroup(@Valid @RequestBody FamilyGroupQuitDto familyGroupQuitDto) {
        try {
            Long userId = Long.parseLong("2"); // 인증 기능 구현 후 추가 예정
            guardianFamilyService.quitFamilyGroup(familyGroupQuitDto, userId);

            return new BaseResponse<>("가족 그룹을 성공적으로 탈퇴하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping("/family-group/profile")
    @Operation(summary = "보호자 가족 그룹 프로필 이미지 수정", description = "")
    public BaseResponse<String> postFamilyGroupProfile(@Valid @RequestBody FamilyGroupProfileDto familyGroupProfileDto) {
        try {
            Long userId = Long.parseLong("2"); // 인증 기능 구현 후 추가 예정
            guardianFamilyService.updateFamilyGroupProfileImg(familyGroupProfileDto, userId);
            return new BaseResponse<>("프로필 이미지가 성공적으로 변경되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
