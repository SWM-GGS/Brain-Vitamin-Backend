package ggs.brainvitamin.src.post.patient.dto;

import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PostMainDto {

    private List<EmotionInfoDto> emotionInfoDtoList = new ArrayList<>();
    private List<PostPreviewDto> postPreviewDtoList = new ArrayList<>();
}
