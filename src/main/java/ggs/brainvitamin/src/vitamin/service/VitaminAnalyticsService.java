package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.vitamin.entity.VitaminAnalyticsEntity;
import ggs.brainvitamin.src.vitamin.repository.VitaminAnalyticsRepository;
import ggs.brainvitamin.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static ggs.brainvitamin.src.user.patient.dto.ActivitiesDto.*;

@Service
@RequiredArgsConstructor
public class VitaminAnalyticsService {

    private final VitaminAnalyticsRepository vitaminAnalyticsRepository;

    public GetWeeklyVitaminDto getWeeklyVitaminAttendance(Long userId, LocalDateTime today) {

        UserEntity userEntity = UserEntity.builder().id(userId).build();

        LocalDateTime endDate = today;
        LocalDateTime startDate = DateUtil.getDateOfThisMonday(today);

        List<VitaminAnalyticsEntity> vitaminHistory =
                vitaminAnalyticsRepository
                        .findByUserAndFinishAndStatusAndCreatedAtBetweenOrderByCreatedAt(
                                userEntity,
                                "T",
                                Status.ACTIVE,
                                startDate,
                                endDate
                );

        List<Boolean> weeklyVitaminAttendanceList = new ArrayList<>(Collections.nCopies(7, false));

        for (VitaminAnalyticsEntity vitaminAnalyticsEntity : vitaminHistory) {
            // 비타민 기록에서 요일 값 가져오기
            int index = vitaminAnalyticsEntity.getCreatedAt().getDayOfWeek().getValue() - 1;

            // 기록이 있는 요일의 값이 아직 false일 때만 true로 바꾸는 작업
            if (!weeklyVitaminAttendanceList.get(index)) {
                weeklyVitaminAttendanceList.set(index, true);
            }
        }

        GetWeeklyVitaminDto getWeeklyVitaminDto = GetWeeklyVitaminDto.builder()
                .weeklyVitaminAttendance(weeklyVitaminAttendanceList)
                .build();

        return getWeeklyVitaminDto;
    }

    public GetChangesFromLastWeekDto getChangesFromLastWeek(Long userId, LocalDateTime today) {

        UserEntity userEntity = UserEntity.builder().id(userId).build();

        // 이번주 월요일부터 오늘까지의 기록 조회
        LocalDateTime endDate = today;
        LocalDateTime startDate = DateUtil.getDateOfThisMonday(today);
        List<VitaminAnalyticsEntity> thisWeekHistory =
                vitaminAnalyticsRepository
                        .findByUserAndStatusAndCreatedAtBetweenOrderByCreatedAt(
                                userEntity,
                                Status.ACTIVE,
                                startDate,
                                endDate
                        );

        // 지난주 월요일부터 저번주 일요일까지의 기록 조회
        endDate = startDate.minusDays(1);
        startDate = startDate.minusDays(8);
        List<VitaminAnalyticsEntity> lastWeekHistory =
                vitaminAnalyticsRepository
                        .findByUserAndStatusAndCreatedAtBetweenOrderByCreatedAt(
                                userEntity,
                                Status.ACTIVE,
                                startDate,
                                endDate
                        );

        // 지난 주 혹은 이번 주 기록이 없으면 return null
        if (thisWeekHistory.isEmpty() || lastWeekHistory.isEmpty())
            return null;

        double[] thisWeekAverage = calcAveragesOfEachArea(thisWeekHistory);
        double[] lastWeekAverage = calcAveragesOfEachArea(lastWeekHistory);

        HashMap<String, Double> changesFromLastWeek = new HashMap<>();
        List<Double> diffAveragePercents = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            if (lastWeekAverage[i] != 0) {
                diffAveragePercents.add((thisWeekAverage[i]-lastWeekAverage[i]) * 100 / lastWeekAverage[i]);
            }
            else {
                diffAveragePercents.add(0.0);
            }
        }
        changesFromLastWeek.put("attention", diffAveragePercents.get(0));
        changesFromLastWeek.put("calculation", diffAveragePercents.get(1));
        changesFromLastWeek.put("executive", diffAveragePercents.get(2));
        changesFromLastWeek.put("language", diffAveragePercents.get(3));
        changesFromLastWeek.put("memory", diffAveragePercents.get(4));
        changesFromLastWeek.put("orientation", diffAveragePercents.get(5));
        changesFromLastWeek.put("sound", diffAveragePercents.get(6));
        changesFromLastWeek.put("visual", diffAveragePercents.get(7));

        return new GetChangesFromLastWeekDto(changesFromLastWeek);
    }

    private double[] calcAveragesOfEachArea(List<VitaminAnalyticsEntity> history) {

        double[] averages = new double[8];
        int[] counts = new int[8];

        // 각 기록에 대하여 영역별로 점수 합산
        for (VitaminAnalyticsEntity vitaminAnalyticsEntity : history) {
            if (vitaminAnalyticsEntity.getAttentionScore() >= 0) {
                averages[0] += vitaminAnalyticsEntity.getAttentionScore();
                counts[0] += 1;
            }
            if (vitaminAnalyticsEntity.getCalculationScore() >= 0) {
                averages[1] += vitaminAnalyticsEntity.getCalculationScore();
                counts[1] += 1;
            }
            if (vitaminAnalyticsEntity.getExecutiveScore() >= 0) {
                averages[2] += vitaminAnalyticsEntity.getExecutiveScore();
                counts[2] += 1;
            }
            if (vitaminAnalyticsEntity.getLanguageScore() >= 0) {
                averages[3] += vitaminAnalyticsEntity.getLanguageScore();
                counts[3] += 1;
            }
            if (vitaminAnalyticsEntity.getMemoryScore() >= 0) {
                averages[4] += vitaminAnalyticsEntity.getMemoryScore();
                counts[4] += 1;
            }
            if (vitaminAnalyticsEntity.getOrientationScore() >= 0) {
                averages[5] += vitaminAnalyticsEntity.getOrientationScore();
                counts[5] += 1;
            }
            if (vitaminAnalyticsEntity.getSoundScore() >= 0) {
                averages[6] += vitaminAnalyticsEntity.getSoundScore();
                counts[6] += 1;
            }
            if (vitaminAnalyticsEntity.getVisualScore() >= 0) {
                averages[7] += vitaminAnalyticsEntity.getVisualScore();
                counts[7] += 1;
            }

        }

        // 각 영역별로 평균 계산
        for (int i=0; i < 8; i++) {
            if (counts[i] > 0) {
                averages[i] /= counts[i];
            }
            else {
                averages[i] = 0;
            }
        }

        return averages;
    }
}
