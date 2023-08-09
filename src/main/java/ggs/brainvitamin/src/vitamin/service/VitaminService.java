package ggs.brainvitamin.src.vitamin.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Result;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.dto.request.CogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostScreeningTestDto;
import ggs.brainvitamin.src.vitamin.dto.request.PostUserDetailDto;
import ggs.brainvitamin.src.vitamin.dto.response.CogTrainingPoolDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetCogTrainingDto;
import ggs.brainvitamin.src.vitamin.dto.response.GetPatientHomeDto;
import ggs.brainvitamin.src.vitamin.entity.*;
import ggs.brainvitamin.src.vitamin.repository.*;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ggs.brainvitamin.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class VitaminService {

    private final UserRepository userRepository;
    private final ScreeningTestRepository screeningTestRepository;
    private final ScreeningTestHistoryRepository screeningTestHistoryRepository;
    private final CommonCodeDetailRepository commonCodeDetailRepository;
    private final VitaminAnalyticsRepository vitaminAnalyticsRepository;
    private final BrainVitaminHistoryRepository brainVitaminHistoryRepository;
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

        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
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

            Optional<ScreeningTestHistoryEntity> screeningTestHistoryEntity = screeningTestHistoryRepository.findTop1ByUserOrderByCreatedAtDesc(userEntity);

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

    public List<Map<String, Object>> getCogTraining(Long userId) {
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
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
        }

        List<ProblemEntity> problemEntities = problemRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        for (ProblemEntity problemEntity : problemEntities) {

            Map<String, Object> candidate = new HashMap<>();

            Random random = new Random();
            Integer randomDifficulty = random.nextInt(1,4);

            if (problemEntity.getTrainingName().equals("오늘의 날짜 찾기")) {
                randomDifficulty = 1;
            }

            candidate.put("problemId", problemEntity.getId());
            candidate.put("trainingName", problemEntity.getTrainingName());
            candidate.put("explanation", problemEntity.getExplanation());
            candidate.put("difficulty", randomDifficulty);
            candidate.put("cogArea", problemEntity.getProblemCategory().getAreaCode().getCodeDetailName());
            candidate.put("timeLimit", problemEntity.getTimeLimit());
            candidate.put("pathUri", problemEntity.getPathUri());

            // 난이도 3일때는 할인 적용
            if (problemEntity.getTrainingName().equals("시장에서 쇼핑하기")) {
                if (randomDifficulty == 3) {
                    // 할인율을 [5, 10, 15, ..., 50]에서 랜덤 추출
                    candidate.put("discountPercent", random.nextInt(1, 11) * 5);
                }
                else {
                    candidate.put("discountPercent", 0);
                }

            }

            ProblemDetailEntity problemDetailEntity = problemDetailRepository.findProblemDetailEntityByProblemAndAndDifficulty(problemEntity, randomDifficulty);

            candidate.put("problemPool", getPool(problemEntity, problemDetailEntity));

            result.add(candidate);
        }

        return result;
    }

    public List<Map<String, Object>> getPool(ProblemEntity problemEntity, ProblemDetailEntity problemDetailEntity) {
        Random random = new Random();

        List<Map<String, Object>> result = new ArrayList<>();

        switch (problemEntity.getTrainingName()) {

            case "카드 뒤집기":
                // 일단 랜덤으로 6개를 뽑고, 난이도에 따라 다시 랜덤으로 뽑기
                List<PoolCardEntity> poolCardEntities = poolCardRepository.findRandom6ByProblem(problemEntity.getId());

                // 리스트 섞기
                Collections.shuffle(poolCardEntities);

                // 난이도 별로 3, 4, 6개 뽑기
                for (int i = 0; i < problemDetailEntity.getElementSize(); i++) {
                    Map<String, Object> candidate = new HashMap<>();

                    candidate.put("imgUrl", poolCardEntities.get(i).getImgUrl());
                    result.add(candidate);
                }

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
                    Map<String, Object> candidate = new HashMap<>();

                    candidate.put("contents", poolMcEntities.get(i).getContents());

                    if (i < problemDetailEntity.getElementSize()) {
                        candidate.put("imgUrl", poolMcEntities.get(i).getImgUrl());
                        candidate.put("answer", Boolean.TRUE);
                    } else {
                        candidate.put("answer", Boolean.FALSE);
                    }
                    result.add(candidate);
                }

                break;

            case "시장에서 쇼핑하기":
                List<PoolSfEntity> poolSfEntities = poolSfRepository.findRandom3ByProblem(problemEntity.getId());
                for (PoolSfEntity poolSfEntity : poolSfEntities) {
                    Map<String, Object> candidate = new HashMap<>();

                    // 가격 100원 단위로 랜덤 추출
                    Integer randomPrice = random.nextInt(poolSfEntity.getMinRange()/100, poolSfEntity.getMaxRange()/100 + 1) * 100;
                    Integer randomCount = 1;

                    // 난이도가 1이 아닐때는 물품 수량이 1 이상 5이하 값 중 랜덤 추출
                    if (!problemDetailEntity.getDifficulty().equals(1)) {
                        randomCount = random.nextInt(1, 6);
                    }

                    candidate.put("contents", poolSfEntity.getElementName());
                    candidate.put("imgUrl", poolSfEntity.getImgUrl());
                    candidate.put("price", randomPrice);
                    candidate.put("count", randomCount);
                    result.add(candidate);
                }

                break;

            case "미로 길찾기":
                PoolMazeEntity poolMazeEntity = poolMazeRepository.findRandom1ByProblemAndDifficulty(problemEntity.getId(), problemDetailEntity.getDifficulty());

                List<PoolMazeDetailEntity> poolMazeDetailEntities = poolMazeEntity.getPoolMazeDetails();

                for (PoolMazeDetailEntity poolMazeDetailEntity : poolMazeDetailEntities) {
                    Map<String, Object> candidate = new HashMap<>();

                    candidate.put("imgUrl", poolMazeEntity.getImgUrl());
                    candidate.put("x", poolMazeDetailEntity.getX());
                    candidate.put("y", poolMazeDetailEntity.getY());

                    if (poolMazeDetailEntity.getAnswer().equals("T")) {
                        candidate.put("answer", Boolean.TRUE);
                    }
                    else {
                        candidate.put("answer", Boolean.FALSE);
                    }
                    result.add(candidate);
                }

                break;
            case "오늘의 날짜 찾기":
                // 난이도만 넘겨주면 됨

                break;

            default:

                break;
        }

        return result;
    }

    public String determinateCogTraining(Long userId, PostCogTrainingDto postCogTrainingDto) {

        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
        }

        // 두뇌 비타민을 끝까지 완수한 경우
        if (postCogTrainingDto.getFinish()) {
            userEntity.plusConsecutiveDays();
        }

        // 영역별 점수
        Integer memoryScore = 0;
        Integer attentionScore = 0;
        Integer orientationScore = 0;
        Integer visualScore = 0;
        Integer languageScore = 0;
        Integer calculationScore = 0;
        Integer executiveScore = 0;
        Integer soundScore = 0;

        for (CogTrainingDto cogTrainingDto : postCogTrainingDto.getCogTrainingDtos()) {
            ProblemEntity problemEntity = problemRepository.findById(cogTrainingDto.getProblemId())
                    .orElseThrow(() -> new BaseException(NOT_ACTIVATED_PROBLEM));

            String cogArea = problemEntity.getProblemCategory().getAreaCode().getCodeDetailName();

            if (cogTrainingDto.getResult().equals(Result.SUCCESS)) {
                switch (cogArea) {
                    case "기억력" -> memoryScore += cogTrainingDto.getScore();
                    case "주의집중력" -> attentionScore += cogTrainingDto.getScore();
                    case "시공간/지남력" -> orientationScore += cogTrainingDto.getScore();
                    case "시지각능력" -> visualScore += cogTrainingDto.getScore();
                    case "언어능력" -> languageScore += cogTrainingDto.getScore();
                    case "계산능력" -> calculationScore += cogTrainingDto.getScore();
                    case "집행능력" -> executiveScore += cogTrainingDto.getScore();
                    default -> soundScore += cogTrainingDto.getScore();
                }
            }


            BrainVitaminHistoryEntity brainVitaminHistoryEntity = BrainVitaminHistoryEntity.builder()
                    .user(userEntity)
                    .problem(problemEntity)
                    .score(cogTrainingDto.getScore())
                    .duration(cogTrainingDto.getDuration())
                    .result(cogTrainingDto.getResult())
                    .build();

            brainVitaminHistoryRepository.save(brainVitaminHistoryEntity);
        }

        VitaminAnalyticsEntity vitaminAnalyticsEntity = VitaminAnalyticsEntity.builder()
                .user(userEntity)
                .memoryScore(memoryScore)
                .attentionScore(attentionScore)
                .orientationScore(orientationScore)
                .visualScore(visualScore)
                .languageScore(languageScore)
                .calculationScore(calculationScore)
                .executiveScore(executiveScore)
                .soundScore(soundScore)
                .build();

        vitaminAnalyticsRepository.save(vitaminAnalyticsEntity);

        Integer totalScore = memoryScore + attentionScore + orientationScore + visualScore +
                languageScore + calculationScore + executiveScore + soundScore;

        String result = "상위 " + (101 - (totalScore * 100 / 70)) + "%";
        return result;
    }

    public List<Map<String, Object>> getScreeningTest(Long userId) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        List<ScreeningTestEntity> screeningTestEntities = screeningTestRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();

        for (ScreeningTestEntity screeningTestEntity : screeningTestEntities) {
            Map<String, Object> candidate = new HashMap<>();

            candidate.put("description", screeningTestEntity.getDescription());
            result.add(candidate);
        }

        return result;
    }

    public Map<String, Object> submitScreeningTest(Long userId, PostScreeningTestDto postScreeningTestDto) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        ScreeningTestHistoryEntity screeningTestHistoryEntity = new ScreeningTestHistoryEntity(userEntity, postScreeningTestDto.getScore());

        screeningTestHistoryRepository.save(screeningTestHistoryEntity);

        HashMap<String, Object> result = new HashMap<>();

        if (postScreeningTestDto.getScore() >= 8) {
            result.put("cogLevel", "의심");
        }
        else {
            result.put("cogLevel", "정상");
        }

        return result;
    }
}
