package ggs.brainvitamin.src.user.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivitiesDto {

    @Data
    @Builder
    public static class ActivitiesResponseDto {

        private GetWeeklyVitaminDto weeklyVitaminDto;
        private GetChangesFromLastWeekDto changesFromLastWeekDto;
        private GetScreeningTestHistoryDto screeningTestHistoryDto;
    }

    @Data
    @AllArgsConstructor
    public static class GetChangesFromLastWeekDto {

        private HashMap<String, Double> changedFromLastWeek = new HashMap<>();
    }

    @Data
    @Builder
    public static class GetWeeklyVitaminDto {

        private List<Boolean> weeklyVitaminAttendance;
    }

    @Data
    @Builder
    public static class GetScreeningTestHistoryDto {

        private Integer totalScore;
        private String state;
        private String description = "";
        private String testDate;

//        private Integer attentionScore;     // 집중력
//        private Integer executiveScore;     // 집행능력
//        private Integer languageScore;      // 언어능력
//        private Integer memoryScore;        // 기억력
//        private Integer orientationScore;   // 지남력
//        private Integer spaceTimeScore;     // 시공간능력
    }
}


