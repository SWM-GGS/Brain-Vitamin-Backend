package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.vitamin.entity.ScreeningTestHistoryEntity;
import ggs.brainvitamin.src.vitamin.repository.ScreeningTestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        // 인지선별검사 결과 historyDto 구성
        ScreeningTestHistoryEntity historyEntity = historyEntityOptional.get();
        Integer totalScore = historyEntity.getTotalScore();
        String state, description;

        if (totalScore >= 8) {
            state = "의심";
            description = "경도인지장애가 의심되는 상태입니다. 가까운 병원이나 보건소를 방문하여 정밀 검사를 받는 것을 권고드립니다.";
        } else {
            state = "양호";
            description = "인지능력에 문제가 없는 양호한 상태입니다. 꾸준한 두뇌비타민 활동을 통해 인지 건강을 지켜나가면 좋습니다.";
        }
        GetScreeningTestHistoryDto historyDto = GetScreeningTestHistoryDto.builder()
                .totalScore(historyEntity.getTotalScore())
                .state(state)
                .description(description)
                .build();

        return historyDto;
    }
}
