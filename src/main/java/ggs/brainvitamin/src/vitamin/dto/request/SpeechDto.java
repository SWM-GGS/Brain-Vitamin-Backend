package ggs.brainvitamin.src.vitamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class SpeechDto {

    @Setter
    @Getter
    public static class Diarization {
        private Boolean enable = Boolean.FALSE;
        private Integer speakerCountMin;
        private Integer speakerCountMax;
    }

    @Setter
    @Getter
    public static class Boosting {
        private String words;

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
    @Setter
    @Getter
    public static class NestRequestEntity {
        private String language = "ko-KR";

        //completion optional, sync/async
        private String completion = "sync";

        //optional, used to receive the analyzed results
//        private String callback;

        //optional, any data
//    private Map<String, Object> userdata;

        private Boolean wordAlignment = Boolean.TRUE;

        private Boolean fullText = Boolean.TRUE;

        //boosting object array
//    private List<Boosting> boostings;

        //comma separated words
//    private String forbiddens;

//    private Diarization diarization;
    }



}
