package ggs.brainvitamin.src.vitamin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.dto.request.SpeechDto;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
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

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private Gson gson = new Gson();

    public Map<String, Object> getSpeechToText(Long userId, String fileUrl) throws BaseException {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
        }

        try {
            SpeechDto.NestRequestEntity nestRequest = new SpeechDto.NestRequestEntity();

            String result = url(fileUrl, nestRequest);

            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {};

            return mapper.readValue(result, typeReference);
        } catch (Exception e) {
            throw new BaseException(INVALID_CLOVA_SPEECH);
        }

    }

    /**
     * recognize media using URL
     * @param url required, the media URL
     * @param nestRequest optional
     * @return string
     */
    public String url(String url, SpeechDto.NestRequestEntity nestRequest) {
         Header[] HEADERS = new Header[] {
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("X-CLOVASPEECH-API-KEY", secretKey),
        };

        HttpPost httpPost = new HttpPost(invokeUrl + "/recognizer/url");
        httpPost.setHeaders(HEADERS);
        Map<String, Object> body = new HashMap<>();
        body.put("url", url);
        body.put("language", nestRequest.getLanguage());
        body.put("completion", nestRequest.getCompletion());
        body.put("wordAlignment", nestRequest.getWordAlignment());
        body.put("fullText", nestRequest.getFullText());
//        body.put("callback", nestRequest.getCallback());
//        body.put("userdata", nestRequest.getCallback());
//        body.put("forbiddens", nestRequest.getForbiddens());
//        body.put("boostings", nestRequest.getBoostings());
//        body.put("diarization", nestRequest.getDiarization());
        HttpEntity httpEntity = new StringEntity(gson.toJson(body), ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        return execute(httpPost);
    }

    private String execute(HttpPost httpPost) {
        try (final CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            final HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }








}
