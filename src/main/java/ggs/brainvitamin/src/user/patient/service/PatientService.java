package ggs.brainvitamin.src.user.patient.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.src.user.patient.dto.ActivitiesDto;
import ggs.brainvitamin.src.post.repository.PostRepository;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ActivitiesDto getActivities(Long id) throws BaseException {

        // 최근 일주일 두뇌 비타민 참여 현황 데이터

        // 최근 일주일 간 영역별 인지 능력 데이터 (두뇌 비타민 결과)

        // 그 지난 일주일 간 영역별 인지 능력 데이터 (두뇌 비타민 결과)

        // 가장 최근 인지선별검사 결과 및 해석

        return new ActivitiesDto();
    }

}
