package ggs.brainvitamin.src.post.patient.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientPostService {

    private final PostRepository postRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public List<PostMainDto> listFamilyStoriesMain(Long familyId) {

        // familyId로 해당 가족 그룹의 게시글을 조회
        List<PostEntity> familyPostResults = postRepository.findByFamilyIdOrderByCreatedAtDesc(familyId);
        List<PostMainDto> familyPostMainList = new ArrayList<>();

        // 각 postEntity에서 게시글 id와 첫번째 이미지를 가져와 postMainDto에 저장
        for (PostEntity post : familyPostResults) {
            familyPostMainList.add(
                    PostMainDto.builder()
                            .id(post.getId())
                            .thumbnailUrl(post.getPostImgEntityList().get(0).getImgUrl())
                            .build()
            );
        }

        return familyPostMainList;
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
            List<CommentDto> commentDtoList = makeCommentDtoList(postValues.getCommentEntityList());

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
                    .commentDtoList(commentDtoList)
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
    private List<CommentDto> makeCommentDtoList(List<CommentEntity> commentEntityList) {

        List<CommentDto> commentDtoList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntityList) {
            commentDtoList.add(
                    CommentDto.builder()
                            .id(commentEntity.getId())
                            .contents(commentEntity.getContents())
                            .postId(commentEntity.getPost().getId())
                            .parentsId(commentEntity.getParentsId())
                            .childCommentList(makeChildCommentDtoList(commentDtoList, commentEntity.getId()))
                            .createdAt(commentEntity.getCreatedAt().toString())
                            .userId(commentEntity.getUser().getId())
                            .userName(commentEntity.getUser().getName())
                            .profileImgUrl(commentEntity.getUser().getProfileImgUrl())
                            .build()
            );
        }
        return commentDtoList;
    }

    // 게시글 답글 구조 구성 함수
    private List<CommentDto> makeChildCommentDtoList(List<CommentDto> commentDtoList, Long parentsId) {

        List<CommentDto> childCommentList = new ArrayList<>();
        for (CommentDto commentDto : commentDtoList) {
            if (commentDto.getParentsId().equals(parentsId)) {
                childCommentList.add(commentDto);
            }
        }
        return childCommentList;
    }
}
