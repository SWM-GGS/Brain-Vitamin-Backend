package ggs.brainvitamin.src.user.guardian.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.guardian.dto.*;
import ggs.brainvitamin.src.user.repository.FamilyMemberRepository;
import ggs.brainvitamin.src.user.repository.FamilyRepository;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class GuardianFamilyService {

    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    /**
     *
     * @param userId
     * @return
     * 보호자앱 접속 시 가장 먼저 보여질 가입된 가족 그룹 리스트 조회
     */
    public List<FamilyGroupPreviewDto> getFamilyGroupList(Long userId) {

        List<FamilyGroupPreviewDto> familyGroupPreviewDtoList = new ArrayList<>();

        // family_member 테이블에서 현재 유저를 조회하여 가입된 가족 그룹을 조회하는 방식
        List<FamilyMemberEntity> familyMemberEntityList = familyMemberRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
        for (FamilyMemberEntity familyMemberEntity : familyMemberEntityList) {

            // 각각의 가족 그룹 Dto 구성
            FamilyEntity familyEntity = familyMemberEntity.getFamily();
            familyGroupPreviewDtoList.add(
                    FamilyGroupPreviewDto.builder()
                            .id(familyEntity.getId())
                            .familyName(familyEntity.getFamilyName())
                            .profileImgUrl(familyMemberEntity.getFamilyGroupProfileImg())
                            .build()
            );
        }

        // 가족 그룹 메인 Dto에 저장하여 반환
        return familyGroupPreviewDtoList;
    }

    /**
     * 가족 그룹 검색 시 결과 조회
     * @param familyKey
     * @return
     */
    public FamilyGroupDetailDto findFamilyGroupWithFamilyKey(String familyKey) {
        FamilyEntity familyEntity = familyRepository.findByFamilyKeyAndStatus(familyKey, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(INVALID_FAMILY_KEY));

        List<FamilyMemberEntity> familyMemberEntityList = familyMemberRepository.findTop2ByFamilyId(familyEntity.getId());
        FamilyMemberEntity firstFamilyMember;

        if (familyMemberEntityList.size() > 1) {
            firstFamilyMember = familyMemberEntityList.get(1);
        } else {
            firstFamilyMember = familyMemberEntityList.get(0);
        }

        return FamilyGroupDetailDto.builder()
                .id(familyEntity.getId())
                .familyName(familyEntity.getFamilyName())
                .memberCount(familyEntity.getMemberCount())
                .profileImgUrl(firstFamilyMember.getFamilyGroupProfileImg())
                .firstUserName(firstFamilyMember.getUser().getName())
                .build();
    }

    /**
     * 새로운 유저의 가족 그룹 가입 처리 함수
     * @param familyGroupJoinDto
     * @param userId
     */
    public void joinFamilyGroup(FamilyGroupJoinDto familyGroupJoinDto, Long userId) {

        // 가족 그룹 정보 조회
        FamilyEntity familyEntity = familyRepository.findById(familyGroupJoinDto.getFamilyId())
                .orElseThrow(() -> new BaseException(FAMILY_NOT_EXISTS));

        // 이미 가입된 사용자에 대한 예외 처리
        List<FamilyMemberEntity> joinedFamilyGroupList = familyMemberRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
        for (FamilyMemberEntity familyMemberEntity : joinedFamilyGroupList) {
            if (familyMemberEntity.getFamily().getId() == familyGroupJoinDto.getFamilyId()) {
                throw new BaseException(ALREADY_JOINED_FAMILY);
            }
        }

        // 가입하고자 하는 유저 조회
        UserEntity userEntity = userRepository.findById(userId)
                        .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        // 실질적인 사용자 가입 처리
        familyEntity.increaseMemberCount();
        familyRepository.save(familyEntity);

        familyMemberRepository.save(
            FamilyMemberEntity.builder()
                    .family(familyEntity)
                    .user(userEntity)
                    .relationship(familyGroupJoinDto.getRelationship())
                    .build()
        );
    }

    /**
     * 가족 그룹 탈퇴 함수
     * @param familyGroupQuitDto
     * @param userId
     */
    public void quitFamilyGroup(FamilyGroupQuitDto familyGroupQuitDto, Long userId) {

        // 가족 그룹 정보 조회
        FamilyEntity familyEntity = familyRepository.findById(familyGroupQuitDto.getFamilyId())
                .orElseThrow(() -> new BaseException(FAMILY_NOT_EXISTS));

        // 가입하고자 하는 유저 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        // 실질적인 사용자 가족 그룹 탈퇴 처리
        FamilyMemberEntity quitMember =
                familyMemberRepository.findByUserIdAndFamilyIdAndStatus(userId, familyGroupQuitDto.getFamilyId(), Status.ACTIVE)
                        .orElseThrow(() -> new BaseException(MEMBER_NOT_EXISTS));

        familyEntity.decreaseMemberCount();
        familyRepository.save(familyEntity);

        quitMember.setStatus(Status.INACTIVE);
        familyMemberRepository.save(quitMember);
    }

    /**
     * 가족 그룹 프로필 이미지 수정 함수
     * @param familyGroupProfileDto
     * @param userId
     */
    public void updateFamilyGroupProfileImg(FamilyGroupProfileDto familyGroupProfileDto, Long userId) {

        // 가족 그룹 정보 조회
        FamilyEntity familyEntity = familyRepository.findById(familyGroupProfileDto.getFamilyId())
                .orElseThrow(() -> new BaseException(FAMILY_NOT_EXISTS));

        // 현재 유저 조회
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        // 해당 사용자에 대하여 가족 그룹 프로필 이미지 변경
        FamilyMemberEntity familyMemberEntity =
                familyMemberRepository.findByUserIdAndFamilyIdAndStatus(userId, familyGroupProfileDto.getFamilyId(), Status.ACTIVE)
                        .orElseThrow(() -> new BaseException(MEMBER_NOT_EXISTS));

        familyMemberEntity.setFamilyGroupProfileImg(
                familyGroupProfileDto.getFamilyGroupProfileImg()
        );

        familyMemberRepository.save(familyMemberEntity);
    }
}
