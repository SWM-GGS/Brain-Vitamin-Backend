package ggs.brainvitamin.src.vitamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetCogTrainingDto {

    private String pathUri;

    private String cogArea;

    private String trainingName;

    private String explanation;

    private Integer difficulty;

    private Integer timeLimit;

    private List<CogTrainingPoolDto> cogTrainingPoolDtos;

    private Integer discountPercent;

    private String imgUrl;

}
