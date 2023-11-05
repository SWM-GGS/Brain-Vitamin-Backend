package ggs.brainvitamin.src.post.guardian.dto;

import ggs.brainvitamin.src.post.patient.dto.*;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FamilyPostMainDto {

    private List<EmotionInfoDto> emotionInfoDtoList;
    private List<FamilyPostDto> familyPostDtoList;
}
