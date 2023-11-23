package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.vitamin.entity.ScreeningTestHistoryEntity;
import ggs.brainvitamin.src.vitamin.repository.ScreeningTestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;

import static ggs.brainvitamin.src.user.patient.dto.ActivitiesDto.*;

@Service
@RequiredArgsConstructor
public class ScreeningTestHistoryService {

    private final ScreeningTestHistoryRepository screeningTestHistoryRepository;

    /**
     * 가장 최근 선별검사 기록 조회 함수
     * @param userId
     * @return
     */
    public GetScreeningTestHistoryDto getScreeningTestHistory(Long userId) {

        // 선별 검사 기록 조회
        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .build();

        Optional<ScreeningTestHistoryEntity> historyEntityOptional =
                screeningTestHistoryRepository.findTop1ByUserAndStatusOrderByCreatedAtDesc(userEntity, Status.ACTIVE);

        if (historyEntityOptional.isEmpty()) {
            return null;
        }

        LocalDate now = LocalDate.now();

        // 만 나이
        // 일단 현재 연도와 태어난 연도 빼기
        int age = now.minusYears(userEntity.getBirthDate().getYear()).getYear();

        // 생일이 지났는지 여부를 판단하기 위해 위의 연도 차이를 생년월일의 연도에 더한다.
        // 연도가 같아짐으로 생년월일만 판단할 수 있음
        if (userEntity.getBirthDate().plusYears(age).isAfter(now)) {
            age = age -1;
        }

        String education = userEntity.getEducationCode().getCodeDetailName();// 무학, 초졸, 중졸, 고졸, 대졸

        // 교육 수준과 만 나이에 대한 점수 규준
        int[][] standard = {
                {18, 22, 24, 26, 27},
                {16, 21, 23, 25, 26},
                {14, 19, 22, 22, 25},
                {11, 16, 18, 20, 22}};

        int row = 0;
        if (age >= 50 & age < 90) {
            row = (age - 50) / 10;
        }
        else if (age >= 90) {
            row = 3;
        }

        int col = 0;
        if (education.equals("초졸")) {
            col = 1;
        }
        else if (education.equals("중졸")) {
            col = 2;
        }
        else if (education.equals("고졸")) {
            col = 3;
        }
        else if (education.equals("대졸")) {
            col = 4;
        }

        // 인지선별검사 결과 historyDto 구성
        String state, description;

        if (historyEntityOptional.get().getTotalScore() < standard[row][col]) {
            state = "의심";
            description = "경도인지장애가 의심되는 상태입니다.";
        }
        else {
            state = "양호";
            description = "인지능력에 문제가 없는 양호한 상태입니다.";
        }

        GetScreeningTestHistoryDto historyDto = GetScreeningTestHistoryDto.builder()
                .totalScore(historyEntityOptional.get().getTotalScore())
                .state(state)
                .description(description)
                .testDate(historyEntityOptional.get().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
                .build();

        return historyDto;
    }
}
