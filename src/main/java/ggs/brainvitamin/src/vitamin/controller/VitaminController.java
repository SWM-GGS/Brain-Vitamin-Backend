package ggs.brainvitamin.src.vitamin.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
import ggs.brainvitamin.src.vitamin.dto.request.PostCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostUserDetailDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetPatientHomeDto;
import ggs.brainvitamin.src.vitamin.dto.MarketDto;
import ggs.brainvitamin.src.vitamin.dto.MazeDto;
import ggs.brainvitamin.src.vitamin.service.VitaminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class VitaminController {

    private final VitaminService vitaminService;

    /**
     * 환자 홈 화면 조회
     * @return GetPatientHomeDto
     */
    @GetMapping("")
    public BaseResponse<GetPatientHomeDto> getPatientHome() {
        try {
            Long userId = 1L;
            GetPatientHomeDto getPatientHomeDto = vitaminService.getPatientHome(userId);

            return new BaseResponse<>(getPatientHomeDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 인지 선별 검사를 위한 회원 정보 받기
     * @repuest postUserDetailDto
     */
    @PostMapping("/vitamins/user-details")
    public BaseResponse<String> setUserDetails(@RequestBody PostUserDetailDto postUserDetailDto) {
        try {
            Long userId = 1L;
            vitaminService.setUserDetails(userId, postUserDetailDto);

            return new BaseResponse<>("회원 정보 받기 완료!");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 두뇌 비타민 인지 향상 게임 데이터 조회
     * @return List<GetCogTrainingDto>
     */
    @GetMapping("/vitamins/cog-training")
    public BaseResponse<List<Map<String, Object>>> getCogTraining() {
        try {
            Long userId = 1L;
            List<Map<String, Object>> responseMap = vitaminService.getCogTraining(userId);

            return new BaseResponse<>(responseMap);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 두뇌 비타민 중단 및 종료
     * @request List<PostCogTrainingDto>
     * @return String
     */
    @PostMapping("/vitamins/cog-training")
    public BaseResponse<String> determinateCogTraining(@RequestBody List<PostCogTrainingDto> postCogTrainingDtos) {
        try {
            Long userId = 1L;
            String result = vitaminService.determinateCogTraining(userId, postCogTrainingDtos);

            return new BaseResponse<>(result);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


}
