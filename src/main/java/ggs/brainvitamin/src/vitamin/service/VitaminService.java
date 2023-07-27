package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.dto.GetPatientHomeDto;
import ggs.brainvitamin.src.vitamin.entity.ScreeningTestHistoryEntity;
import ggs.brainvitamin.src.vitamin.repository.ScreeningTestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static ggs.brainvitamin.config.BaseResponseStatus.NOT_ACTIVATED_USER;

@Service
@RequiredArgsConstructor
public class VitaminService {

    private final UserRepository userRepository;
    private final ScreeningTestHistoryRepository screeningTestHistoryRepository;


    public GetPatientHomeDto getPatientHome(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        if (userEntity.getStatusCode().getCodeDetail().equals("ACTI02")) {
            throw new BaseException(NOT_ACTIVATED_USER);
        }

        GetPatientHomeDto getPatientHomeDto = new GetPatientHomeDto();

        // 회원 가입 후 첫 두뇌 비타민 실행
        if (userEntity.getGender() == null) {
            getPatientHomeDto.setFirst(true);
            getPatientHomeDto.setNextToDo("screeningTest");
            getPatientHomeDto.setConsecutiveDays(0);
        }
        // 회원 가입 후 한번 이상 두뇌 비타민 실행
        else {
            getPatientHomeDto.setFirst(false);

            Optional<ScreeningTestHistoryEntity> screeningTestHistoryEntity = screeningTestHistoryRepository.findScreeningTestHistoryEntityByUserOrderByCreatedAtDesc(userEntity);

            // 인지 선별 검사가 처음인 경우
            if (screeningTestHistoryEntity.isEmpty()) {
                getPatientHomeDto.setNextToDo("screeningTest");
                getPatientHomeDto.setConsecutiveDays(0);
            }
            // 인지 선별 검사 기록이 있는 경우
            else {
                // 마지막 인지 선별 검사가 한달이 지난 경우
                if (ChronoUnit.MONTHS.between(screeningTestHistoryEntity.get().getCreatedAt(), LocalDateTime.now()) >= 1) {
                    getPatientHomeDto.setNextToDo("screeningTest");
                }
                // 마지막 인지 선별 검사한지 한달 이내 -> 두뇌 비타민 게임으로 넘어가야 하는 경우
                else {
                    getPatientHomeDto.setNextToDo("cogTraining");
                }
                getPatientHomeDto.setConsecutiveDays(userEntity.getConsecutiveDays());
            }
        }

        return getPatientHomeDto;
    }

}
