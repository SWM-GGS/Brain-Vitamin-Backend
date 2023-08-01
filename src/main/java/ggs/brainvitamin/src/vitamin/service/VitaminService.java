package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.dto.request.PostUserDetailDto;
import ggs.brainvitamin.src.vitamin.dto.response.CogTrainingPoolDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetPatientHomeDto;
import ggs.brainvitamin.src.vitamin.entity.*;
import ggs.brainvitamin.src.vitamin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ggs.brainvitamin.config.BaseResponseStatus.NOT_ACTIVATED_USER;

@Service
@RequiredArgsConstructor
public class VitaminService {

    private final UserRepository userRepository;
    private final ScreeningTestHistoryRepository screeningTestHistoryRepository;
    private final CommonCodeDetailRepository commonCodeDetailRepository;
    private final VitaminAnalyticsRepository vitaminAnalyticsRepository;
    private final ProblemRepository problemRepository;
    private final ProblemDetailRepository problemDetailRepository;
    private final PoolSfRepository poolSfRepository;
    private final PoolMcRepository poolMcRepository;
    private final PoolCardRepository poolCardRepository;
    private final PoolMazeRepository poolMazeRepository;
    private final PoolMazeDetailRepository poolMazeDetailRepository;



    public GetPatientHomeDto getPatientHome(Long userId) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

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

    public void setUserDetails(Long userId, PostUserDetailDto postUserDetailDto) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        String birthDate = postUserDetailDto.getBirthDate().substring(0, 4) + "-" +
                postUserDetailDto.getBirthDate().substring(4, 6) + "-" + postUserDetailDto.getBirthDate().substring(6, 8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(birthDate, formatter);

        CommonCodeDetailEntity commonCodeDetailEntity = commonCodeDetailRepository.findCommonCodeDetailEntityByCodeDetailName(postUserDetailDto.getEducation());

        userEntity.addPatientDetails(date, postUserDetailDto.getGender(), commonCodeDetailEntity);
        userRepository.save(userEntity);
    }

    public List<GetCogTrainingDto> getCogTraining(Long userId) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

//        List<VitaminAnalyticsEntity> vitaminHistoryEntities = vitaminAnalyticsRepository.findTop5ByUserOrderByCreatedAtDesc(userEntity);
//
//        // 두뇌 비타민 기록이 없을때 -> 아예 랜덤으로 문제 가져오기
//        if (vitaminHistoryEntities.isEmpty()) {
//            Random random = new Random();
//
//
//
//        }
//        else {
//
//        }

        // 일단 MVP에는 영역당 1문제씩 밖에 없어서 그냥 다 긁어오기
        List<ProblemEntity> problemEntities = problemRepository.findAll();

        List<GetCogTrainingDto> getCogTrainingDtos = new ArrayList<>();

        for (ProblemEntity problemEntity : problemEntities) {

            String cogArea = problemEntity.getProblemCategory().getAreaCode().getCodeDetailName();

            Random random = new Random();
            Integer randomDifficulty = random.nextInt(1,4);

            if (problemEntity.getTrainingName().equals("오늘의 날짜 찾기")) {
                randomDifficulty = 1;
            }

            ProblemDetailEntity problemDetailEntity = problemDetailRepository.findProblemDetailEntityByProblemAndAndDifficulty(problemEntity, randomDifficulty);

            getCogTrainingDtos.add(generateCogTrainingDto(problemEntity, cogArea, problemDetailEntity));
        }

        return getCogTrainingDtos;
    }

    private GetCogTrainingDto generateCogTrainingDto(ProblemEntity problemEntity, String cogArea, ProblemDetailEntity problemDetailEntity) {
        Random random = new Random();

        GetCogTrainingDto resultGetCogTrainingDto = new GetCogTrainingDto();

        resultGetCogTrainingDto.setCogArea(cogArea);
        resultGetCogTrainingDto.setDifficulty(problemDetailEntity.getDifficulty());
        resultGetCogTrainingDto.setExplanation(problemEntity.getExplanation());
        resultGetCogTrainingDto.setTrainingName(problemEntity.getTrainingName());
        resultGetCogTrainingDto.setPathUri(problemEntity.getPathUri());
        resultGetCogTrainingDto.setTimeLimit(problemEntity.getTimeLimit());

        List<CogTrainingPoolDto> resultCogTrainingPoolDto = new ArrayList<>();

        switch (problemEntity.getTrainingName()) {

            case "카드 뒤집기":
                // 일단 랜덤으로 6개를 뽑고, 난이도에 따라 다시 랜덤으로 뽑기
                List<PoolCardEntity> poolCardEntities = poolCardRepository.findRandom6ByProblem(problemEntity.getId());

                // 리스트 섞기
                Collections.shuffle(poolCardEntities);

                // 난이도 별로 3, 4, 6개 뽑기
                for (int i = 0; i < problemDetailEntity.getElementSize(); i++) {
                    CogTrainingPoolDto cogTrainingPoolDto = new CogTrainingPoolDto();
                    cogTrainingPoolDto.setImgUrl(poolCardEntities.get(i).getImgUrl());
                    resultCogTrainingPoolDto.add(cogTrainingPoolDto);
                }

                resultGetCogTrainingDto.setCogTrainingPoolDtos(resultCogTrainingPoolDto);
                break;

            case "팔레트 따라 색칠하기":
                // 난이도만 넘겨주면 됨
                break;

            case "합쳐진 숫자 찾기":
                // 난이도만 넘겨주면 됨
                break;

            case "글자 조합해서 단어 만들기":
                List<PoolMcEntity> poolMcEntities = poolMcRepository.findRandom8ByProblem(problemEntity.getId());

                Collections.shuffle(poolMcEntities);

                for (int i = 0; i < 8; i++) {
                    CogTrainingPoolDto cogTrainingPoolDto = new CogTrainingPoolDto();

                    cogTrainingPoolDto.setContents(poolMcEntities.get(i).getContents());

                    if (i < problemDetailEntity.getElementSize()) {
                        cogTrainingPoolDto.setImgUrl(poolMcEntities.get(i).getImgUrl());
                        cogTrainingPoolDto.setAnswer(true);
                    }
                    else {
                        cogTrainingPoolDto.setAnswer(false);
                    }

                    resultCogTrainingPoolDto.add(cogTrainingPoolDto);
                }

                resultGetCogTrainingDto.setCogTrainingPoolDtos(resultCogTrainingPoolDto);
                break;

            case "시장에서 쇼핑하기":
                // 난이도 3일때는 할인 적용
                if (problemDetailEntity.getDifficulty().equals(3)) {
                    // 할인율을 [5, 10, 15, ..., 50]에서 랜덤 추출
                    resultGetCogTrainingDto.setDiscountPercent(random.nextInt(1, 11) * 5);
                }

                List<PoolSfEntity> poolSfEntities = poolSfRepository.findRandom3ByProblem(problemEntity.getId());
                for (PoolSfEntity poolSfEntity : poolSfEntities) {
                    // 가격 100원 단위로 랜덤 추출
                    Integer randomPrice = random.nextInt(poolSfEntity.getMinRange()/100, poolSfEntity.getMaxRange()/100 + 1) * 100;
                    Integer randomCount = 1;

                    // 난이도가 1이 아닐때는 물품 수량이 1 이상 5이하 값 중 랜덤 추출
                    if (!problemDetailEntity.getDifficulty().equals(1)) {
                        randomCount = random.nextInt(1, 6);
                    }

                    resultCogTrainingPoolDto.add(new CogTrainingPoolDto(poolSfEntity.getImgUrl(), poolSfEntity.getElementName(), randomPrice, randomCount));
                }

                resultGetCogTrainingDto.setCogTrainingPoolDtos(resultCogTrainingPoolDto);
                break;

            case "미로 길찾기":
                PoolMazeEntity poolMazeEntity = poolMazeRepository.findRandom1ByProblemAndDifficulty(problemEntity.getId(), problemDetailEntity.getDifficulty());

                List<PoolMazeDetailEntity> poolMazeDetailEntities = poolMazeEntity.getPoolMazeDetails();

                resultGetCogTrainingDto.setImgUrl(poolMazeEntity.getImgUrl());

                for (PoolMazeDetailEntity poolMazeDetailEntity : poolMazeDetailEntities) {
                    CogTrainingPoolDto cogTrainingPoolDto = new CogTrainingPoolDto();
                    cogTrainingPoolDto.setX(poolMazeDetailEntity.getX());
                    cogTrainingPoolDto.setY(poolMazeDetailEntity.getY());

                    if (poolMazeDetailEntity.getAnswer().equals("T")) {
                        cogTrainingPoolDto.setAnswer(true);
                    }
                    else {
                        cogTrainingPoolDto.setAnswer(false);
                    }

                    resultCogTrainingPoolDto.add(cogTrainingPoolDto);
                }

                resultGetCogTrainingDto.setCogTrainingPoolDtos(resultCogTrainingPoolDto);
                break;
            case "오늘의 날짜 찾기":
                // 난이도만 넘겨주면 됨

                break;

            default:

                break;
        }

        return resultGetCogTrainingDto;
    }
}
