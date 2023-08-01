package ggs.brainvitamin.src.post.guardian.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.common.repository.CommonCodeRepository;
import ggs.brainvitamin.src.post.entity.PostEntity;
import ggs.brainvitamin.src.post.entity.PostImgEntity;
import ggs.brainvitamin.src.post.guardian.dto.FamilyPostDto;
import ggs.brainvitamin.src.post.guardian.dto.FamilyPostMainDto;
import ggs.brainvitamin.src.post.patient.dto.EmotionInfoDto;
import ggs.brainvitamin.src.post.patient.dto.PostImgDto;
import ggs.brainvitamin.src.post.repository.PostRepository;
import ggs.brainvitamin.src.user.entity.FamilyEntity;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.FamilyMemberRepository;
import ggs.brainvitamin.src.user.repository.FamilyRepository;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class GuardianFamilyPostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonCodeDetailRepository commonCodeDetailRepository;

    public FamilyPostMainDto getFamilyStoriesMain(Long familyId, Long userId) {

        // 사용자 확인
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USERS_EMPTY_USER_ID));

        // 가족 그룹 확인
        FamilyEntity familyEntity = familyRepository.findById(familyId)
                .orElseThrow(() -> new BaseException(FAMILY_NOT_EXISTS));

        // 현재 사용자가 가족 그룹 멤버인지 확인
        FamilyMemberEntity familyMemberEntity =
                familyMemberRepository.findByUserIdAndFamilyIdAndStatus(userId, familyId, userEntity.getStatus())
                        .orElseThrow(() -> new BaseException(MEMBER_NOT_EXISTS));

        // 가족 그룹 아이디로 우리가족 이야기 게시글 조회
        List<PostEntity> familyPostEntityList = postRepository.findByFamilyIdOrderByCreatedAtDesc(familyId);

        List<FamilyPostDto> familyPostDtoList = new ArrayList<>();
        for (PostEntity postEntity : familyPostEntityList) {

            // 게시글 내 이미지 리스트 조회
            List<PostImgDto> postImgDtoList = makeImgDtoList(postEntity.getPostImgEntityList());

            familyPostDtoList.add(
                    FamilyPostDto.builder()
                            .id(postEntity.getId())
                            .contents(postEntity.getContents())
                            .postImgDtoList(postImgDtoList)
                            .createdAt(postEntity.getCreatedAt().toString())
                            .userId(postEntity.getUser().getId())
                            .userName(postEntity.getUser().getName())
                            .relationship(familyMemberEntity.getRelationship())
                            .profileImgUrl(postEntity.getUser().getProfileImgUrl())
                            .viewersCount(postEntity.getViewersCount())
                            .emotionsCount(postEntity.getEmotionsCount())
                            .commentsCount(postEntity.getCommentsCount())
                            .build()
            );
        }

        // FamilyPostMainDto 구성 및 반환
        return FamilyPostMainDto.builder()
                .familyPostDtoList(familyPostDtoList)
                .emotionInfoDtoList(makeEmotionInfoDtoList())
                .build();
    }

    // 게시글 이미지 리스트 구성 함수
    private List<PostImgDto> makeImgDtoList(List<PostImgEntity> postImgEntityList) {

        List<PostImgDto> postImgDtoList = new ArrayList<>();
        for (PostImgEntity imgEntity : postImgEntityList) {
            postImgDtoList.add(
                    PostImgDto.builder()
                            .id(imgEntity.getId())
                            .imgUrl(imgEntity.getImgUrl())
                            .description(imgEntity.getDescription())
                            .build()
            );
        }
        return postImgDtoList;
    }

    private List<EmotionInfoDto> makeEmotionInfoDtoList() {

        CommonCodeEntity commonCodeEntity = commonCodeRepository.findByCode("EMOT")
                .orElseThrow(() -> new BaseException(CODE_NOT_EXISTS));

        List<CommonCodeDetailEntity> commonCodeDetailEntityList =
                commonCodeDetailRepository.findByCommonCode(commonCodeEntity);

        List<EmotionInfoDto> emotionInfoDtoList = new ArrayList<>();
        for (CommonCodeDetailEntity commonCodeDetailEntity : commonCodeDetailEntityList) {
            emotionInfoDtoList.add(
                    EmotionInfoDto.builder()
                            .id(commonCodeDetailEntity.getId())
                            .CodeDetailName(commonCodeDetailEntity.getCodeDetailName())
                            .build()
            );
        }

        return emotionInfoDtoList;
    }
}
