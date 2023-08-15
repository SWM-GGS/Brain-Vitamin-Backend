package ggs.brainvitamin.src.vitamin.dto.request;

import ggs.brainvitamin.config.Result;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CogTrainingDto {
    @NotNull(message = "problem id 값을 입력해주세요")
    @Positive(message = "올바른 id 값을 입력해주세요")
    private Long problemId;

    @NotNull(message = "duration 값을 입력해주세요")
    @Positive(message = "올바른 duration 값을 입력해주세요")
    private Float duration;

    @NotBlank(message = "result 값을 입력해주세요")
    private Result result;

    @NotNull(message = "score 값을 입력해주세요")
    @Min(value = -1, message = "올바른 score 값을 입력해주세요")
    @Max(value = 10, message = "올바른 score 값을 입력해주세요")
    private Integer score;
}
