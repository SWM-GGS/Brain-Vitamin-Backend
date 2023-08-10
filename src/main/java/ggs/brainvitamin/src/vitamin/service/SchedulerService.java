package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final UserRepository userRepository;
    private final CommonCodeDetailRepository commonCodeDetailRepository;

    // 매일 정오 오전 12시에 실행
    @Scheduled(cron = "0 * * * * *")
    public void calculateVitaminConsecutiveDays() {
        CommonCodeDetailEntity commonCodeDetailEntity = commonCodeDetailRepository.findCommonCodeDetailEntityByCodeDetailName("환자");

        List<UserEntity> userEntities = userRepository.findAllByUserTypeCodeAndStatus(commonCodeDetailEntity, Status.ACTIVE);

        // 모든 환자타입의 유저들 두뇌 비타민 연속 수행일 계산
        for (UserEntity userEntity : userEntities) {
            if (userEntity.getTodayVitaminCheck() == 0) {
                userEntity.setConsecutiveDays(0);
            }
            userEntity.setTodayVitaminCheck(0);
        }

    }
}
