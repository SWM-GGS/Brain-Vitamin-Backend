package ggs.brainvitamin.src.vitamin.controller;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponse;
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

    @GetMapping("/maze")
    public BaseResponse<MazeDto> getMaze() {
        try {
            MazeDto mazeDto = new MazeDto("https://us.123rf.com/450wm/bonumopus/bonumopus1703/bonumopus170300778/75021750-%EB%AF%B8%EB%A1%9C-%EA%B2%8C%EC%9E%84-%EB%B0%B0%EA%B2%BD%EC%9E%85%EB%8B%88%EB%8B%A4-%EC%9E%85%EA%B5%AC%EC%99%80-%EC%B6%9C%EA%B5%AC%EC%99%80-%EB%AF%B8%EB%A1%9C%EC%9E%85%EB%8B%88%EB%8B%A4.jpg");

            return new BaseResponse<>(mazeDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/market")
    public BaseResponse<List<MarketDto>> getMarket() {
        try {
            MarketDto marketDto1 = new MarketDto("채소", "양파", 1000, 2, "https://img.lovepik.com/free-png/20210918/lovepik-onion-png-image_400259430_wh1200.png");
            MarketDto marketDto2 = new MarketDto("채소", "당근", 2000, 3, "https://w7.pngwing.com/pngs/983/723/png-transparent-carrot-illustration-carrot-food-painted-hand.png");

            ArrayList<MarketDto> marketDtos = new ArrayList<>();
            marketDtos.add(marketDto1);
            marketDtos.add(marketDto2);

            return new BaseResponse<>(marketDtos);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

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
     * @param postUserDetailDto
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


}
