package ggs.brainvitamin.src.user.notification.sms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SmsRequestDto {

    String type;
    String contentType;
    String countryCode;
    String from;
    String content;
    List<MessageDto> messages;
}
