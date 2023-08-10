package ggs.brainvitamin.src.vitamin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUserDetailDto {

    @NotBlank(message = "생년월일을 입력해주세요")
    @Pattern(regexp = "[12][09][0-9][0-9](0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])", message = "잘못된 날짜 형식입니다.")
    private String birthDate;

    @NotBlank(message = "성별을 입력해주세요")
    @Pattern(regexp = "FEMALE|MALE", message = "잘못된 성별 형식입니다.")
    private String gender;

    @NotBlank(message = "학력을 입력해주세요")
    @Pattern(regexp = "무학|초졸|중졸|고졸|대졸", message = "잘못된 학력 형식입니다.")
    private String education;

}
