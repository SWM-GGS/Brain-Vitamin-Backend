package ggs.brainvitamin.src.user.notification.sms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

    String to;
    String content;
}
