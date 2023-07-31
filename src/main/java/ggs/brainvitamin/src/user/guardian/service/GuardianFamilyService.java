package ggs.brainvitamin.src.user.guardian.service;

import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import ggs.brainvitamin.src.user.guardian.dto.FamilyGroupMainDto;
import ggs.brainvitamin.src.user.guardian.dto.FamilyGroupPreviewDto;
import ggs.brainvitamin.src.user.repository.FamilyMemberRepository;
import ggs.brainvitamin.src.user.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GuardianFamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;

    /**
     *
     * @param userId
     * @return
     * 보호자앱 접속 시 가장 먼저 보여질 가입된 가족 그룹 리스트 조회
     */
    public FamilyGroupMainDto getFamilyGroupList(Long userId) {

        List<FamilyGroupPreviewDto> familyGroupPreviewDtoList = new ArrayList<>();

        // family_member 테이블에서 현재 유저를 조회하여 가입된 가족 그룹을 조회하는 방식
        List<FamilyMemberEntity> familyMemberEntityList = familyMemberRepository.findByUserId(userId);
        for (FamilyMemberEntity familyMemberEntity : familyMemberEntityList) {

            // 각각의 가족 그룹 Dto 구성
            FamilyEntity familyEntity = familyMemberEntity.getFamily();
            familyGroupPreviewDtoList.add(
                    FamilyGroupPreviewDto.builder()
                            .id(familyEntity.getId())
                            .familyName(familyEntity.getFamilyName())
                            .profileImgUrl(familyEntity.getProfileImgUrl())
                            .build()
            );
        }

        // 가족 그룹 메인 Dto에 저장하여 반환
        return FamilyGroupMainDto.builder()
                .familyGroupPreviewDtoList(familyGroupPreviewDtoList)
                .build();
    }
}
