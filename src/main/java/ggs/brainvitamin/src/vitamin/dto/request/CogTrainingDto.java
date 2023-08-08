package ggs.brainvitamin.src.vitamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CogTrainingDto {
    private Long problemId;

    private Float duration;

    private String result;

    private Integer score;
}
