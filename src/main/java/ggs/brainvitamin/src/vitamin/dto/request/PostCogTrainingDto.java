package ggs.brainvitamin.src.vitamin.dto.request;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "두뇌 비타민 완수 여부를 입력해주세요")
    private Boolean finish;

    private List<CogTrainingDto> cogTrainingDtos;
}
