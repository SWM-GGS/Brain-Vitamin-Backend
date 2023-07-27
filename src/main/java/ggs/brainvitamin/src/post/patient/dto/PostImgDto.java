package ggs.brainvitamin.src.post.patient.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class PostImgDto {

    private Long id;
    private String imgUrl;
    private String description;
}
