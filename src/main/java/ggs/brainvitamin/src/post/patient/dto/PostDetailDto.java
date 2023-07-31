package ggs.brainvitamin.src.post.patient.dto;

import ggs.brainvitamin.src.post.entity.CommentEntity;
import ggs.brainvitamin.src.post.entity.EmotionEntity;
import ggs.brainvitamin.src.post.entity.PostEntity;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Data
@Builder
public class PostDetailDto {
    // 게시글 정보
    private Long id;
    private String contents;
    private List<PostImgDto> postImgDtoList;
    private String createdAt;

    // 작성자 정보
    private Long userId;
    private String userName;
    private String relationship;
    private String profileImgUrl;

    // 조회자 정보
    private Long viewersCount;
    private List<ViewerDto> viewerDtoList = new ArrayList<>();

    // 감정표현 정보
    private Long emotionsCount;
    private List<EmotionDto> emotionDtoList = new ArrayList<>();

    // 댓글 정보
    private Long commentsCount;
    private LinkedHashMap<Long, CommentDto> commentDtoMap = new LinkedHashMap<>();
}
