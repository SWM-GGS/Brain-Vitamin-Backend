package ggs.brainvitamin.src.post.patient.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CommentDto {

    // 댓글 정보
    private Long id;
    private String contents;
    private Long postId;
    private Long parentsId;
    private List<CommentDto> childCommentList;
    private String createdAt;

    // 작성자 정보
    private Long userId;
    private String userName;
    private String profileImgUrl;
}
