package ggs.brainvitamin.src.user.notification.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponseDto {

    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
}
