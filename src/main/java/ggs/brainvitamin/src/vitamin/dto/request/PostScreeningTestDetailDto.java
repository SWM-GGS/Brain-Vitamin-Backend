package ggs.brainvitamin.src.vitamin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostScreeningTestDetailDto {
    @NotNull(message = "인지 선별 검사 Id를 입력해주세요")
    @Min(value = 0, message = "잘못된 Id 입니다")
    @Max(value = 60, message = "잘못된 Id 입니다")
    private Long screeningTestId;

    @NotNull(message = "중복 요청 횟수를 입력해주세요")
    @Min(value = 0, message = "잘못된 횟수 입니다")
    @Max(value = 3, message = "잘못된 횟수 입니다")
    private Integer count;

    private String audioContent;

    private List<Integer> firstVertex;

    private List<Integer> secondVertex;
}
