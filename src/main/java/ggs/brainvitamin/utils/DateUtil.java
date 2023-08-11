package ggs.brainvitamin.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {

    private DateUtil() { }

    public static LocalDateTime getDateOfThisMonday(LocalDateTime today) {

        LocalDateTime endDate= LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // 요일 가져오기 (월 ~ 일 -> 1 ~ 7)
        Integer dayValue = endDate.getDayOfWeek().getValue();
        // 최소 날짜 및 시간 범위 설정 (현재 주의 월요일까지만)
        LocalDateTime thisMonday = endDate.minusDays(dayValue-1).withHour(0).withMinute(0).withSecond(0);

        return thisMonday;
    }
}
