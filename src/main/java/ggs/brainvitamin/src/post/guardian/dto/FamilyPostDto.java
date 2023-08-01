package ggs.brainvitamin.src.post.guardian.dto;

import ggs.brainvitamin.src.post.patient.dto.CommentDto;
import ggs.brainvitamin.src.post.patient.dto.EmotionDto;
import ggs.brainvitamin.src.post.patient.dto.PostImgDto;
import ggs.brainvitamin.src.post.patient.dto.ViewerDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FamilyPostDto {

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
    // 감정표현 정보
    private Long emotionsCount;
    // 댓글 정보
    private Long commentsCount;
}
