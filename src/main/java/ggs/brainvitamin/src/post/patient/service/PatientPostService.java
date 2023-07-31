package ggs.brainvitamin.src.post.patient.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.entity.CommonCodeEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.common.repository.CommonCodeRepository;
import ggs.brainvitamin.src.post.entity.CommentEntity;
import ggs.brainvitamin.src.post.entity.EmotionEntity;
import ggs.brainvitamin.src.post.entity.PostEntity;
import ggs.brainvitamin.src.post.entity.PostImgEntity;
import ggs.brainvitamin.src.post.patient.dto.*;
import ggs.brainvitamin.src.post.repository.PostRepository;
import ggs.brainvitamin.src.user.entity.FamilyMemberEntity;
import ggs.brainvitamin.src.user.repository.FamilyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientPostService {

    private final PostRepository postRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonCodeDetailRepository commonCodeDetailRepository;

    public PostMainDto getFamilyStoriesMain(Long familyId) {

        // familyId로 해당 가족 그룹의 게시글을 조회
        List<PostEntity> familyPostResults = postRepository.findByFamilyIdOrderByCreatedAtDesc(familyId);
        List<PostPreviewDto> familyPostPreviewList = new ArrayList<>();

        // 각 postEntity에서 게시글 id와 첫번째 이미지를 가져와 postMainDto 구성
        for (PostEntity post : familyPostResults) {
            familyPostPreviewList.add(
                    PostPreviewDto.builder()
                            .id(post.getId())
                            .thumbnailUrl(post.getPostImgEntityList().get(0).getImgUrl())
                            .build()
            );
        }

        // 모든 감정표현의 종류를 공통 코드 테이블을 통해 조회하여 리스트
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

        // return 할 PostMainDto 빌드
        return PostMainDto.builder()
                .postPreviewDtoList(familyPostPreviewList)
                .emotionInfoDtoList(emotionInfoDtoList)
                .build();
    }

    public PostDetailDto getFamilyStoriesPost(Long postId) {

        //post 테이블에서 해당 게시글 조회
        Optional<PostEntity> postResult = postRepository.findById(postId);

        if (postResult.isPresent()) {

            PostEntity postValues = postResult.get();

            // 게시글 작성자 환자와의 관계 구성
            Optional<FamilyMemberEntity> familyMemberInfo = familyMemberRepository.findById(postValues.getUser().getId());
            String relationship = familyMemberInfo.get().getRelationship();

            // 게시글 이미지 리스트 구성
            List<PostImgDto> postImgDtoList = makeImgDtoList(postValues.getPostImgEntityList());

            // 게시글 감정표현 리스트 구성
            List<EmotionDto> emotionDtoList = makeEmotionDtoList(postValues.getEmotionEntityList());

            // 게시글 댓글 리스트 구성
            LinkedHashMap<Long, CommentDto> commentDtoMap = makeCommentDtoMap(postValues.getCommentEntityList());

            return PostDetailDto.builder()
                    .id(postValues.getId())
                    .contents(postValues.getContents())
                    .postImgDtoList(postImgDtoList)
                    .createdAt(postValues.getCreatedAt().toString())
                    .userId(postValues.getUser().getId())
                    .userName(postValues.getUser().getName())
                    .relationship(relationship)
                    .profileImgUrl(postValues.getUser().getProfileImgUrl())
                    .viewersCount(postValues.getViewersCount())
                    .emotionsCount(postValues.getEmotionsCount())
                    .emotionDtoList(emotionDtoList)
                    .commentsCount(postValues.getCommentsCount())
                    .commentDtoMap(commentDtoMap)
                    .build();
        } else {
            return null;
        }
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

    // 게시글 감정표현 리스트 구성 함수
    private List<EmotionDto> makeEmotionDtoList(List<EmotionEntity> emotionEntityList) {

        List<EmotionDto> emotionDtoList = new ArrayList<>();
        for (EmotionEntity emotionEntity : emotionEntityList) {
            emotionDtoList.add(
                    EmotionDto.builder()
                            .id(emotionEntity.getId())
                            .userName(emotionEntity.getUser().getName())
                            .profileImgUrl(emotionEntity.getUser().getProfileImgUrl())
                            .emotionType(emotionEntity.getEmotionTypeCode().getCodeDetailName())
                            .build()
            );
        }
        return emotionDtoList;
    }

    // 게시글 댓글, 답글 리스트 구성 함수
    private LinkedHashMap<Long, CommentDto> makeCommentDtoMap(List<CommentEntity> commentEntityList) {

        // 먼저 등록된 순으로 정렬하여
        // parentsId가 없으면 commentId로 Map에 삽입 (부모 댓글)
        // parentsId가 있으면 parentId로 Map에 삽입 (자식 댓글)
        LinkedHashMap<Long, CommentDto> commentDtoLinkedHashMap = new LinkedHashMap<>();
        for (CommentEntity commentEntity : commentEntityList) {
            CommentDto commentDto = CommentDto.builder()
                    .id(commentEntity.getId())
                    .contents(commentEntity.getContents())
                    .postId(commentEntity.getPost().getId())
                    .parentsId(commentEntity.getParentsId())
                    .childCommentList(new ArrayList<>())
                    .createdAt(commentEntity.getCreatedAt().toString())
                    .userId(commentEntity.getUser().getId())
                    .userName(commentEntity.getUser().getName())
                    .profileImgUrl(commentEntity.getUser().getProfileImgUrl())
                    .build();

            if (commentEntity.getParentsId() == 0) {
                commentDtoLinkedHashMap.put(commentEntity.getId(), commentDto);
            } else {
                commentDtoLinkedHashMap.get(commentEntity.getParentsId()).getChildCommentList().add(0, commentDto);
            }
        }
        return commentDtoLinkedHashMap;
    }
}
