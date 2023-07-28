package ggs.brainvitamin.src.post.patient.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostPreviewDto {

    private Long id;
    private String thumbnailUrl;
}
