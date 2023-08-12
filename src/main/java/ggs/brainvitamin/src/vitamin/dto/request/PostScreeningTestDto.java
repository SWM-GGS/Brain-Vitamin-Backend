package ggs.brainvitamin.src.vitamin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostScreeningTestDto {
    @NotNull(message = "점수를 입력해주세요")
    @Min(value = 0, message = "잘못된 점수입니다")
    @Max(value = 30, message = "잘못된 점수입니다")
    private Integer score;
}