package ggs.brainvitamin.src.user.patient.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.user.patient.dto.ActivitiesDto;
import ggs.brainvitamin.src.post.patient.dto.PostMainDto;
import ggs.brainvitamin.src.user.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class UserPatientController {

    private PatientService patientService;

//    @GetMapping("/")
//    public BaseResponse<ActivitiesDto> getMain() {
//        try {
//
//        }
//    }

    @GetMapping("/activities")
    public BaseResponse<ActivitiesDto> getActivities() {
        try {
            Long userId = Long.valueOf(1);  // 현재 접속 중인 User의 Id 받아오기
            return new BaseResponse<>(patientService.getActivities(userId));

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
