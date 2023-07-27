package ggs.brainvitamin.src.post.patient.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class PostMainDto {

    private Long id;
    private String thumbnailUrl;
}
