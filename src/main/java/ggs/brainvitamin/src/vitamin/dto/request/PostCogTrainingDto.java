package ggs.brainvitamin.src.vitamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCogTrainingDto {
    private Boolean finish;
    private List<CogTrainingDto> cogTrainingDtos;
}
