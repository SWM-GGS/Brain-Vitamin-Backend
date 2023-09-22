package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static ggs.brainvitamin.config.BaseResponseStatus.INVALID_USERTYPE;
import static ggs.brainvitamin.config.BaseResponseStatus.NOT_ACTIVATED_USER;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;

    @Value("${chatgpt.api-key}")
    private String API_KEY;
    private static final String ENDPOINT = "https://api.openai.com/v1/completions";

    public Map getChatResponse(Long userId, String prompt, float temperature, int maxTokens) {
        // 유저 체크
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        // 환자 타입의 유저가 아닌 경우, 예외 처리
        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
        }

        // 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        // requestBody 설정
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model","gpt-3.5-turbo-instruct");
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);

        // 헤더와 바디 묶어서 엔티티로 만들기
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // RestTemplate으로 post 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, requestEntity, Map.class);

        return response.getBody();
    }

}
