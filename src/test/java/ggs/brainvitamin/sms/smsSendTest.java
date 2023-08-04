package ggs.brainvitamin.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import ggs.brainvitamin.src.user.notification.sms.SmsService;
import ggs.brainvitamin.src.user.notification.sms.dto.MessageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class smsSendTest {

    @Autowired
    private SmsService smsService;

    @Test
    @DisplayName("네이버 SENS를 이용한 SMS 전송 테스트")
    void sendSmsTest() throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        MessageDto messageDto = MessageDto.builder()
                .to("01039412077")
                .build();

        smsService.sendSms(messageDto);
    }
}
