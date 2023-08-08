package ggs.brainvitamin.src.user.notification.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ggs.brainvitamin.src.user.notification.sms.dto.MessageDto;
import ggs.brainvitamin.src.user.notification.sms.dto.SmsRequestDto;
import ggs.brainvitamin.src.user.notification.sms.dto.SmsResponseDto;
import lombok.RequiredArgsConstructor;
//import org.apache.commons.codec.binary.Base64;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${naver-cloud-sms.accessKey}")
    private String accessKey;

    @Value("${naver-cloud-sms.secretKey}")
    private String secretKey;

    @Value("${naver-cloud-sms.serviceId}")
    private String serviceId;

    @Value("${naver-cloud-sms.senderPhone}")
    private String phone;

    private String makeSignature(Long time) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + serviceId + "/messages";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(time)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

        return encodeBase64String;
    }

    public SmsResponseDto sendSms(MessageDto messageDto) throws
            JsonProcessingException,
            RestClientException,
            URISyntaxException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException {

        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        // 인증번호 생성 후 messageDto의 contents에 저장
        String authNum = generateAuthNumber();
        String message = "\n[두뇌비타민] 인증번호: ";
        messageDto.setContent(message + authNum);

        List<MessageDto> messageDtoList = new ArrayList<>();
        messageDtoList.add(messageDto);

        SmsRequestDto requestDto = SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(phone)
                .content(messageDto.getContent())
                .messages(messageDtoList)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(requestDto);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponseDto responseDto = restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ this.serviceId +"/messages"), httpBody, SmsResponseDto.class
        );
        responseDto.setAuthNum(authNum);

        return responseDto;
    }

    private String generateAuthNumber() {

        StringBuilder authNumber = new StringBuilder();
        Random random = new Random();
        int length = 6;

        for (int i = 0; i < length; i++) {
            authNumber.append(random.nextInt(10));
        }

        return authNumber.toString();
    }

}
