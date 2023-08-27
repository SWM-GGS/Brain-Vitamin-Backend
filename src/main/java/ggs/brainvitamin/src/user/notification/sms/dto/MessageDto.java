package ggs.brainvitamin.src.user.notification.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

    @NotBlank(message = "휴대폰 번호를 입력해주세요.")
    @Pattern(regexp = "01([0|1])([0-9]{8})")
    private String to;

    private String content;
}
