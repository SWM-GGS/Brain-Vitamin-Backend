package ggs.brainvitamin.src.post.patient.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmotionDto {

    private Long id;
    private String userName;
    private String profileImgUrl;
    private String emotionType;
}
