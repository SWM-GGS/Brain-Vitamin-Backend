package ggs.brainvitamin.src.user.guardian.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.post.patient.dto.EmotionIdDto;
import ggs.brainvitamin.src.user.guardian.dto.FamilyGroupDetailDto;
import ggs.brainvitamin.src.user.guardian.dto.FamilyGroupJoinDto;
import ggs.brainvitamin.src.user.guardian.dto.FamilyGroupPreviewDto;
import ggs.brainvitamin.src.user.guardian.service.GuardianFamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.representer.BaseRepresenter;

import java.util.List;

@RestController
@RequestMapping("/guardian")
@RequiredArgsConstructor
public class GuardianUserController {

    private final GuardianFamilyService guardianFamilyService;

    /**
     * @return
     * 가족 그룹 리스트 조회 함수
     */
    @GetMapping("/family-group")
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
    public BaseResponse<FamilyGroupDetailDto> getFamilyGroupDetail(@PathVariable("familyKey") String familyKey) {
        try {
            return new BaseResponse<>(guardianFamilyService.findFamilyGroupWithFamilyKey(familyKey));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("/family-group")
    public BaseResponse<String> postJoinFamilyGroup(@Valid @RequestBody FamilyGroupJoinDto familyGroupJoinDto) {
        try {

            Long userId = Long.parseLong("2"); // 인증 기능 구현 후 추가 예정
            guardianFamilyService.joinFamilyGroup(familyGroupJoinDto, userId);

            return new BaseResponse<>("가족 그룹에 성공적으로 가입하였습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
