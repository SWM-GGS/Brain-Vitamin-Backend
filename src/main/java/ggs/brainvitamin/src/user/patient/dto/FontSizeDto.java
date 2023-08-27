package ggs.brainvitamin.src.user.patient.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FontSizeDto {

    @NotNull(message = "폰트 크기를 입력해주세요.")
    @Min(value = 1, message = "잘못된 폰트 크기입니다.")
    @Max(value = 3, message = "잘못된 폰트 크기입니다.")
    private Integer fontSize;
}