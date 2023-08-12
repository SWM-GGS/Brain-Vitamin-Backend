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
                        .findByUserAndFinishAndStatusAndCreatedAtBetweenOrderByCreatedAt(
                                userEntity,
                                "T",
                                Status.ACTIVE,
                                startDate,
                                endDate
                        );

        // 지난주 월요일부터 저번주 일요일까지의 기록 조회
        endDate = startDate.minusDays(1);
        startDate = startDate.minusDays(8);
        List<VitaminAnalyticsEntity> lastWeekHistory =
                vitaminAnalyticsRepository
                        .findByUserAndFinishAndStatusAndCreatedAtBetweenOrderByCreatedAt(
                                userEntity,
                                "T",
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
        changesFromLastWeek.put("attention", thisWeekAverage[0]-lastWeekAverage[0]);
        changesFromLastWeek.put("calculation", thisWeekAverage[1]-lastWeekAverage[1]);
        changesFromLastWeek.put("executive", thisWeekAverage[2]-lastWeekAverage[2]);
        changesFromLastWeek.put("language", thisWeekAverage[3]-lastWeekAverage[3]);
        changesFromLastWeek.put("memory", thisWeekAverage[4]-lastWeekAverage[4]);
        changesFromLastWeek.put("orientation", thisWeekAverage[5]-lastWeekAverage[5]);
        changesFromLastWeek.put("sound", thisWeekAverage[6]-lastWeekAverage[6]);
        changesFromLastWeek.put("visual", thisWeekAverage[7]-lastWeekAverage[7]);

        return new GetChangesFromLastWeekDto(changesFromLastWeek);
    }

    private double[] calcAveragesOfEachArea(List<VitaminAnalyticsEntity> history) {

        double[] averages = new double[8];
        int count = history.size();

        // 각 기록에 대하여 영역별로 점수 합산
        for (VitaminAnalyticsEntity vitaminAnalyticsEntity : history) {
            averages[0] += vitaminAnalyticsEntity.getAttentionScore();
            averages[1] += vitaminAnalyticsEntity.getCalculationScore();
            averages[2] += vitaminAnalyticsEntity.getExecutiveScore();
            averages[3] += vitaminAnalyticsEntity.getLanguageScore();
            averages[4] += vitaminAnalyticsEntity.getMemoryScore();
            averages[5] += vitaminAnalyticsEntity.getOrientationScore();
            averages[6] += vitaminAnalyticsEntity.getSoundScore();
            averages[7] += vitaminAnalyticsEntity.getVisualScore();
        }

        // 각 영역별로 평균 계산
        for (int i=0; i < 8; i++) {
            averages[i] /= count;
        }

        return averages;
    }
}
