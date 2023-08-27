package ggs.brainvitamin.src.user.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfilesRequestDto {

    @Pattern(regexp = "((http[s]?|ftp):\\/\\/)?(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=가-힣]{1,256}[:|\\.][a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+,.~#?&\\/=가-힣]*)", message = "잘못된 URL 링크입니다.")
    private String profileImgUrl;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣~^()_]{2,10}$", message = "잘못된 닉네임 형식입니다.")
    private String nickname;

    @NotBlank(message = "학력을 입력해주세요.")
    @Pattern(regexp = "무학|초졸|중졸|고졸|대졸", message = "잘못된 학력 형식입니다.")
    private String education;
}
