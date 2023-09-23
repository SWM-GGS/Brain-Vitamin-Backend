package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.dto.request.SpeechDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ClovaSpeechService {
    private final UserRepository userRepository;

    @Value("${clova-speech.invokeUrl}")
    private String invokeUrl;
    @Value("${clova-speech.secretKey}")
    private String secretKey;

    public Map getSpeechToText(Long userId, String fileUrl) throws BaseException {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
        }

        SpeechDto.NestRequestEntity nestRequest = new SpeechDto.NestRequestEntity();

        // 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("X-CLOVASPEECH-API-KEY", secretKey);

        // requestBody 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("url", fileUrl);
        requestBody.put("language", nestRequest.getLanguage());
        requestBody.put("completion", nestRequest.getCompletion());
        requestBody.put("wordAlignment", nestRequest.getWordAlignment());
        requestBody.put("fullText", nestRequest.getFullText());

        // 헤더와 바디 묶어서 엔티티로 만들기
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate으로 post 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(invokeUrl + "/recognizer/url", requestEntity, Map.class);

        return response.getBody();
    }


}
