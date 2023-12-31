package ggs.brainvitamin.src.vitamin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.commons.lang.Pair;
import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.Result;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.common.entity.CommonCodeDetailEntity;
import ggs.brainvitamin.src.common.repository.CommonCodeDetailRepository;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import ggs.brainvitamin.src.vitamin.dto.request.*;
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
import java.util.regex.Pattern;

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

    private final ClovaSpeechService clovaSpeechService;
    private final ChatService chatService;



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

            Optional<ScreeningTestHistoryEntity> screeningTestHistoryEntity = screeningTestHistoryRepository.findTop1ByUserAndStatusOrderByCreatedAtDesc(userEntity, Status.ACTIVE);

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

        List<ProblemEntity> problemEntities = new ArrayList<>();

        problemEntities.add(problemRepository.findMemoryProblemsRandom());
        problemEntities.add(problemRepository.findCalculateProblemsRandom());
        problemEntities.add(problemRepository.findOrientationProblemsRandom());
        problemEntities.add(problemRepository.findExecutiveProblemsRandom());
        problemEntities.add(problemRepository.findVisualProblemsRandom());
        problemEntities.add(problemRepository.findLanguageProblemsRandom());
        List<ProblemEntity> attentionProblemsRandom = problemRepository.findAttentionProblemsRandom();
        problemEntities.addAll(attentionProblemsRandom);

        Collections.shuffle(problemEntities);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ProblemEntity problemEntity : problemEntities) {

            Map<String, Object> candidate = new HashMap<>();

            Random random = new Random();
            Integer randomDifficulty = random.nextInt(1,3);

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
                    candidate.put("discountPercent", random.nextInt(0, 2) * 50);
                }
            }

            // 합쳐진 숫자 찾기 최대 난이도 2로 제한
            if (problemEntity.getTrainingName().equals("합쳐진 숫자 찾기") ||
                    problemEntity.getTrainingName().equals("사칙연산 계산하기")) {
                if (randomDifficulty == 3) {
                    candidate.put("difficulty", 2);
                }
            }

            // 기억력 문제 - 몇 단계 이후에 다시 맞추게 할지 추가
            if (problemEntity.getTrainingName().equals("단어 기억하기") ||
                    problemEntity.getTrainingName().equals("국기 기억하기") ||
                    problemEntity.getTrainingName().equals("국기-나라 매칭 기억하기")) {
                candidate.put("showNext", random.nextInt(0, 4));
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
        List<PoolMcEntity> poolMcEntities;
        List<PoolSfEntity> poolSfEntities;

        switch (problemEntity.getTrainingName()) {

            case "단어 기억하기":
            case "국기 기억하기":
                // 일단 랜덤으로 10개를 뽑고, 난이도에 따라 갯수에 맞게 고르기
                poolMcEntities = poolMcRepository.findRandom10ByProblem(problemEntity.getId());

                // 난이도 별로 6, 8, 10개 고르기
                Collections.shuffle(poolMcEntities);
                List<PoolMcEntity> selectedSubjects = poolMcEntities.subList(0, problemDetailEntity.getElementSize()*2);

                // 절반은 정답, 절반은 오답 처리해서 Map 형태로 반환
                int count = 0;
                int limit = problemDetailEntity.getElementSize();

                for (PoolMcEntity selectedSubject : selectedSubjects) {
                    Map<String, Object> candidate = new HashMap<>();

                    // 단어 기억하기 문제일 때는 contents key 추가
                    if (problemEntity.getTrainingName().equals("단어 기억하기")) {
                        candidate.put("contents", selectedSubject.getContents());
                    }

                    // 국기 기억하기 문제일 때는 imgUrl key 추가
                    else if (problemEntity.getTrainingName().equals("국기 기억하기")) {
                        candidate.put("imgUrl", selectedSubject.getImgUrl());
                    }

                    if (count < limit) {
                        candidate.put("answer", Boolean.TRUE);
                        count++;
                    } else {
                        candidate.put("answer", Boolean.FALSE);
                    }

                    result.add(candidate);
                }

                break;

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

            case "국기-나라 매칭 기억하기":
                poolMcEntities = poolMcRepository.findRandomNByProblem(problemEntity.getId(), problemDetailEntity.getElementSize());

                for (PoolMcEntity poolMcEntity : poolMcEntities) {
                    Map<String, Object> candidate = new HashMap<>();

                    candidate.put("contents", poolMcEntity.getContents());
                    candidate.put("imgUrl", poolMcEntity.getImgUrl());

                    result.add(candidate);
                }

            case "팔레트 따라 색칠하기":
                // 난이도만 넘겨주면 됨
                break;

            case "숫자 차례대로 터치하기":
                // 난이도만 넘겨주면 됨
                break;

            case "글자의 색 선택하기":
                // 넘겨줄 데이터 없음
                break;

            case "글자가 뜻하는 색 선택하기":
                // 넘겨줄 데이터 없음
                break;

            case "글자 색과 뜻하는 색이 같은 것 선택하기":
                // 넘겨줄 데이터 없음
                break;

            case "합쳐진 숫자 찾기":
                // 난이도만 넘겨주면 됨
                break;

            case "글자 조합해서 단어 만들기":
                List<PoolMcEntity> poolMcEntitiesForWord = poolMcRepository.findRandom8ByProblem(problemEntity.getId());

                Collections.shuffle(poolMcEntitiesForWord);

                for (int i = 0; i < 8; i++) {
                    Map<String, Object> candidate = new HashMap<>();

                    candidate.put("contents", poolMcEntitiesForWord.get(i).getContents());

                    if (i < problemDetailEntity.getElementSize()) {
                        candidate.put("imgUrl", poolMcEntitiesForWord.get(i).getImgUrl());
                        candidate.put("answer", Boolean.TRUE);
                    } else {
                        candidate.put("answer", Boolean.FALSE);
                    }
                    result.add(candidate);
                }

                break;

            case "거스름돈 계산하기":
                poolSfEntities = poolSfRepository.findRandomN(problemDetailEntity.getElementSize());

                for (PoolSfEntity poolSfEntity : poolSfEntities) {
                    Map<String, Object> candidate = new HashMap<>();

                    // 100원 단위로 가격 설정
                    Integer randomPrice = random.nextInt(poolSfEntity.getMinRange()/100, poolSfEntity.getMaxRange()/100 + 1) * 100;

                    candidate.put("contents", poolSfEntity.getElementName());
                    candidate.put("imgUrl", poolSfEntity.getImgUrl());
                    candidate.put("price", randomPrice);

                    result.add(candidate);
                }

            case "시장에서 쇼핑하기":
                poolSfEntities = poolSfRepository.findRandom3ByProblem(problemEntity.getId());
                for (PoolSfEntity poolSfEntity : poolSfEntities) {
                    Map<String, Object> candidate = new HashMap<>();

                    // 가격 1000원 단위로 랜덤 추출
                    Integer randomPrice = random.nextInt(poolSfEntity.getMinRange()/1000, poolSfEntity.getMaxRange()/1000 + 1) * 1000;
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
                // 미로 문제 난이도 1로 고정
                PoolMazeEntity poolMazeEntity = poolMazeRepository.findRandom1ByProblemAndDifficulty(problemEntity.getId(), 1);

                List<PoolMazeDetailEntity> poolMazeDetailEntities = poolMazeEntity.getPoolMazeDetails();

                for (PoolMazeDetailEntity poolMazeDetailEntity : poolMazeDetailEntities) {
                    Map<String, Object> candidate = new HashMap<>();

                    candidate.put("imgUrl", poolMazeEntity.getImgUrl());
                    candidate.put("answerImgUrl", poolMazeEntity.getAnswerImgUrl());
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

            case "가까운 시간 찾기":
                // 넘겨줄 데이터 없음
                break;

            case "사칙연산 계산하기":
                // 난이도만 넘겨주면 됨
                break;

            case "규칙에 맞는 숫자 찾기":
                // 난이도만 넘겨주면 됨
                break;

            case "올바른 요일 찾기":
                // 난이도만 넘겨주면 됨
                break;

            case "나침반 방향 찾기":
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

        // 두뇌 비타민을 끝까지 완수한 경우, 비타민 연속 수행일 계산
        if (postCogTrainingDto.getFinish()) {
            // 두뇌 비타민 연속 수행일이 0인 경우 +1
            if (userEntity.getConsecutiveDays() == 0) {
                userEntity.plusConsecutiveDays();
            }
            // 0이 아닌 경우, 가장 최신 두뇌 비타민 기록 확인
            else {
                Optional<VitaminAnalyticsEntity> vitaminAnalyticsHistory = vitaminAnalyticsRepository.findTop1ByUserAndFinishOrderByCreatedAtDesc(userEntity, "T");

                if (vitaminAnalyticsHistory.isPresent()) {
                    if (ChronoUnit.DAYS.between(vitaminAnalyticsHistory.get().getCreatedAt().toLocalDate(), LocalDate.now()) >= 1) {
                        userEntity.plusConsecutiveDays();
                    }
                }
            }
            userEntity.setTodayVitaminCheck(1);
        }

        // 영역별 <점수, 하나라도 풀었는지 여부>
        Pair<Integer, Boolean> memory = Pair.of(0, false);
        Pair<Integer, Boolean> attention = Pair.of(0, false);
        Pair<Integer, Boolean> orientation = Pair.of(0, false);
        Pair<Integer, Boolean> visual = Pair.of(0, false);
        Pair<Integer, Boolean> language = Pair.of(0, false);
        Pair<Integer, Boolean> calculation = Pair.of(0, false);
        Pair<Integer, Boolean> executive = Pair.of(0, false);
        Pair<Integer, Boolean> sound = Pair.of(0, false);

        for (CogTrainingDto cogTrainingDto : postCogTrainingDto.getCogTrainingDtos()) {
            ProblemEntity problemEntity = problemRepository.findById(cogTrainingDto.getProblemId())
                    .orElseThrow(() -> new BaseException(NOT_ACTIVATED_PROBLEM));

            String cogArea = problemEntity.getProblemCategory().getAreaCode().getCodeDetailName();

            if (!cogTrainingDto.getResult().equals(Result.DONOT)) {
                switch (cogArea) {
                    case "기억력" -> memory = Pair.of(memory.getFirst() + cogTrainingDto.getScore(), true);
                    case "주의집중력" -> attention = Pair.of(attention.getFirst() + cogTrainingDto.getScore(), true);
                    case "시공간/지남력" -> orientation = Pair.of(orientation.getFirst() + cogTrainingDto.getScore(), true);
                    case "시지각능력" -> visual = Pair.of(visual.getFirst() + cogTrainingDto.getScore(), true);
                    case "언어능력" -> language = Pair.of(language.getFirst() + cogTrainingDto.getScore(), true);
                    case "계산능력" -> calculation = Pair.of(calculation.getFirst() + cogTrainingDto.getScore(), true);
                    case "집행능력" -> executive = Pair.of(executive.getFirst() + cogTrainingDto.getScore(), true);
                    default -> sound = Pair.of(sound.getFirst() + cogTrainingDto.getScore(), true);
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

        // 기억력 문제를 풀지 않은 경우
        if (!memory.getSecond()) {
            memory = Pair.of(-1, false);
        }
        // 주의집중력 문제를 풀지 않은 경우
        if (!attention.getSecond()) {
            attention = Pair.of(-1, false);
        }
        // 시공간/지남력 문제를 풀지 않은 경우
        if (!orientation.getSecond()) {
            orientation = Pair.of(-1, false);
        }
        // 시지각능력 문제를 풀지 않은 경우
        if (!visual.getSecond()) {
            visual = Pair.of(-1, false);
        }
        // 언어능력 문제를 풀지 않은 경우
        if (!language.getSecond()) {
            language = Pair.of(-1, false);
        }
        // 계산능력 문제를 풀지 않은 경우
        if (!calculation.getSecond()) {
            calculation = Pair.of(-1, false);
        }
        // 집행능력 문제를 풀지 않은 경우
        if (!executive.getSecond()) {
            executive = Pair.of(-1, false);
        }
        // 소리인지력 문제를 풀지 않은 경우
        if (!sound.getSecond()) {
            sound = Pair.of(-1, false);
        }

        VitaminAnalyticsEntity vitaminAnalyticsEntity = VitaminAnalyticsEntity.builder()
                .user(userEntity)
                .memoryScore(memory.getFirst())
                .attentionScore(attention.getFirst())
                .orientationScore(orientation.getFirst())
                .visualScore(visual.getFirst())
                .languageScore(language.getFirst())
                .calculationScore(calculation.getFirst())
                .executiveScore(executive.getFirst())
                .soundScore(sound.getFirst())
                .build();

        if (!postCogTrainingDto.getFinish()) {
            vitaminAnalyticsEntity.setFinish("F");
        }

        vitaminAnalyticsRepository.save(vitaminAnalyticsEntity);

        Integer totalScore = memory.getFirst() + attention.getFirst() + orientation.getFirst() + visual.getFirst() +
                language.getFirst() + calculation.getFirst() + executive.getFirst() + sound.getFirst();

        String result = "상위 " + (101 - (totalScore * 100 / 80)) + "%";
        if (totalScore <= 0) {
            result = "상위 99.99%";
        }
        return result;
    }

    public List<Map<String, Object>> getScreeningTest(Long userId) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        List<ScreeningTestEntity> screeningTestEntities = screeningTestRepository.findAllByStatus(Status.ACTIVE);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ScreeningTestEntity screeningTestEntity : screeningTestEntities) {
            Map<String, Object> candidate = new HashMap<>();

            candidate.put("screeningTestId", screeningTestEntity.getId());
            candidate.put("audioUrl", screeningTestEntity.getAudioUrl());
            candidate.put("description", screeningTestEntity.getDescription());
            candidate.put("step", screeningTestEntity.getStep());
            candidate.put("mikeOn", screeningTestEntity.getMikeOn());
            candidate.put("hide", screeningTestEntity.getHide());


             if (screeningTestEntity.getImgUrl() != null) {
                candidate.put("imgUrl", screeningTestEntity.getImgUrl());
             }

             if (screeningTestEntity.getId() == 57) {
                 candidate.put("timeLimit", screeningTestEntity.getTimeLimit());
             }

             if (screeningTestEntity.getTrial() > 0) {
                 candidate.put("trial", screeningTestEntity.getTrial());
             }

            result.add(candidate);
        }

        return result;
    }

    public Map<String, Object> submitScreeningTest(Long userId, PostScreeningTestDto postScreeningTestDto) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        ScreeningTestHistoryEntity screeningTestHistoryEntity = new ScreeningTestHistoryEntity(userEntity, postScreeningTestDto.getScore());

        screeningTestHistoryRepository.save(screeningTestHistoryEntity);

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

        HashMap<String, Object> result = new HashMap<>();

        if (postScreeningTestDto.getScore() < standard[row][col]) {
            result.put("cogLevel", "의심");
        }
        else {
            result.put("cogLevel", "양호");
        }

        return result;
    }

    public Map<String, Object> checkScreeningTestDetail(Long userId, PostScreeningTestDetailDto postScreeningTestDetailDto) {
        UserEntity userEntity = userRepository.findByIdAndStatus(userId, Status.ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_ACTIVATED_USER));

        // 환자 타입의 유저가 아닌 경우, 예외 처리
        if (!userEntity.getUserTypeCode().getCodeDetailName().equals("환자")) {
            throw new BaseException(INVALID_USERTYPE);
        }

        Map<String, Object> result = new HashMap<>();

        int correct = 0;

        // 오디오 파일이 없는 문제 -> 모눈종이 그림 따라 그리기 문제
        if (postScreeningTestDetailDto.getScreeningTestId() == 42) {
            correct = checkTest9(postScreeningTestDetailDto.getFirstVertex(), postScreeningTestDetailDto.getSecondVertex());
        }
        // 오디오 파일이 있는 문제
        else {
            String audioContent = postScreeningTestDetailDto.getAudioContent();

            // STT 변환에 성공한 경우
            if (!audioContent.isEmpty()) {
                correct = switch (postScreeningTestDetailDto.getScreeningTestId().intValue()) {
                    // 올해는 몇 년도입니까?
                    case 31 -> checkTest1(audioContent);

                    // 지금은 몇 월입니까?
                    case 32 -> checkTest2(audioContent);

                    // 오늘은 며칠입니까?
                    case 33 -> checkTest3(audioContent);

                    // 오늘은 무슨 요일입니까?
                    case 34 -> checkTest4(audioContent);

                    // 현재 검사자께서 살고계시는 나라는 어디입니까?
                    case 35 -> checkTest5(audioContent);

                    // 제가 불러드리는 숫자를 그대로 따라 해 주세요
                    // 1번
                    case 38 -> checkTest6(audioContent);

                    // 제가 불러드리는 숫자를 그대로 따라 해 주세요.
                    // 2번
                    case 39 -> checkTest7(audioContent);

                    // 제가 불러드리는 말을 끝에서부터 거꾸로 따라 해 주세요
                    case 41 -> checkTest8(audioContent);

                    // 제가 조금 전에 외우라고 불러드렸던 문장을 다시 한번 말씀해 주세요
                    case 51 -> {
                        Map<String, Object> checkMap = checkTest10(audioContent);

                        List<?> forgetIndex = convertObjectToList(checkMap.get("forgetIndex"));
                        result.put("forgetIndex", forgetIndex);

                        yield (int) checkMap.get("totalScore");
                    }

                    // 이것을 무엇입니까? (칫솔)
                    case 53 -> checkTest11(audioContent);

                    // 이것을 무엇입니까? (그네)
                    case 54 -> checkTest12(audioContent);

                    // 이것을 무엇입니까? (주사위)
                    case 55 -> checkTest13(audioContent);

                    // 박수를 두번 치고, 조금 쉬었다가 한번 더 쳐주세요
                    case 56 -> checkTest14(audioContent);

                    // 지금부터 1분 동안 과일이나 채소를 최대한 많이 이야기 해 주세요. 준비되셨지요? 자, 과일이나 채소 이름을 말씀해 주세요
                    case 57 -> checkTest15(audioContent);
                    default -> 0;
                };

                result.put("text", audioContent);
            }
        }

        // 문제 풀이에 성공한 경우
        if (correct > 0) {
            result.put("isCorrect", true);
            result.put("score", correct);
            result.put("description", "문제 풀이에 성공하였습니다.");
            result.put("stop", true);
        }
        // 문제 풀이에 실패한 경우
        else {
            result.put("isCorrect", false);
            result.put("score", 0);
            result.put("description", "다시 한번 말씀해주세요.");

            // 문제 풀이 시도 횟수가 2회 또는 모눈종이 그림 따라 그리기 문제인 경우 stop
            if (postScreeningTestDetailDto.getCount() == 2 | postScreeningTestDetailDto.getScreeningTestId() == 42) {
                result.put("stop", true);
            }
            else {
                result.put("stop", false);
            }
        }


        return result;

    }

    // Object 객체를 List로 변환
    public List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }

    // GPT 응답 추출 메서드
    public String getGptResponseText(Map response) {
        Object choices = response.get("choices");
        List<?> objects = convertObjectToList(choices);
        Object textObject = objects.get(0);

        ObjectMapper objectMapper = new ObjectMapper();

        // convert object to map
        Map<String, Object> map = objectMapper.convertValue(textObject, Map.class);

        return String.valueOf(map.get("text"));
    }

    public int checkTest1(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        // 현재 날짜
        LocalDate nowDate = LocalDate.now();

        // 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");

        // 포맷 적용
        String currentYear = nowDate.format(formatter);

        System.out.println("현재 연도 : " + currentYear);
        System.out.println("오디오 답변 : " + afterStr);

        // 답변에 현재 연도을 포함하면 정답
        if (afterStr.contains(currentYear)) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }

    private int checkTest2(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        // 현재 날짜
        LocalDate nowDate = LocalDate.now();
        int monthValue = nowDate.getMonth().getValue();

        // 포맷 적용
        String currentMonth = String.valueOf(monthValue) + "월";

        System.out.println("현재 월 : " + currentMonth);
        System.out.println("오디오 답변 : " + afterStr);

        // 답변에 현재 월을 포함하면 정답
        if (afterStr.contains(currentMonth)) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }
    private int checkTest3(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        // 현재 날짜
        LocalDate nowDate = LocalDate.now();

        // 포맷 적용
        String currentDay = String.valueOf(nowDate.getDayOfMonth());

        System.out.println("현재 날짜 : " + currentDay);
        System.out.println("오디오 답변 : " + afterStr);

        // 답변에 현재 날짜을 포함하면 정답
        if (afterStr.contains(currentDay)) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }
    private int checkTest4(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        // 현재 날짜
        LocalDate nowDate = LocalDate.now();

        int weekValue = nowDate.getDayOfWeek().getValue();

        List<String> weekList = new ArrayList<>(Arrays.asList("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"));

        String currentWeek = weekList.get(weekValue - 1);

        System.out.println("현재 일자 : " + currentWeek);
        System.out.println("오디오 답변 : " + afterStr);

        // 답변에 특정 요일을 포함하면 정답
        if (afterStr.contains(currentWeek)) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }
    private int checkTest5(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        // 답변에 대한민국 또는 한국을 포함하면 정답
        if (afterStr.contains("대한민국") | afterStr.contains("한국")) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }
    private int checkTest6(String text) {
        // 답변에 6973을 포함하면 정답
        if (text.contains("6973")) {
            System.out.println("문제 풀이 성공");
            return 1;
        }

        return 0;
    }
    private int checkTest7(String text) {
        // 답변에 57284를 포함하면 정답
        if (text.contains("57284")) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }
    private int checkTest8(String text) {
        String editedText = text.replaceAll(" ", "");

        // 답변에 산강수금 포함하면 정답
        if (editedText.contains("산강수금")) {
            System.out.println("문제 풀이 성공");
            return 1;
        }
        return 0;
    }
    private int checkTest9(List<Integer> firstVertex, List<Integer> secondVertex) {
        List<Integer> answerFirst = new ArrayList<>(Arrays.asList(2, 2, 6, 8, 8, 9, 12, 12, 14, 14, 17, 18));
        List<Integer> answerSecond = new ArrayList<>(Arrays.asList(6, 8, 12, 13, 9, 14, 17, 13, 18, 19, 18, 19));

        // 맞춘 선분의 개수
        int correctCount = 0;

        // 틀린 선분의 개수
        int inCorrectCount = 0;

        // 그린 그림의 선분을 통해 맞춘 선분의 개수 계산
        for (int i = 0; i < firstVertex.size(); i++) {
            // 현재의 선분이 정답인지 판별할 변수
            boolean correct = false;

            for (int j = 0; j < answerFirst.size(); j++) {
                // 정답인 선분인 경우
                if ((firstVertex.get(i).equals(answerFirst.get(j)) & secondVertex.get(i).equals(answerSecond.get(j))) |
                        (firstVertex.get(i).equals(answerSecond.get(j)) & secondVertex.get(i).equals(answerFirst.get(j)))) {
                    correct = true;
                    answerFirst.set(j, 0);
                    answerSecond.set(j, 0);
                    break;
                }
            }
            // 현재의 선분이 정답인 경우
            if (correct) {
                correctCount++;
            }
            // 현재의 선분이 오답인 경우
            else {
                inCorrectCount++;
            }
        }

        // 오답이 2개 이상인 경우 문제 틀림
        if (inCorrectCount >= 2) {
            return 0;
        }
        // 오답이 1개인 경우
        else if (inCorrectCount == 1) {
            // 정답 수가 12개이면 오류 1개로 1점
            if (correctCount == 12) {
                return 1;
            }
            else {
                return 0;
            }
        }
        // 오답이 0개인 경우
        else {
            // 모든 선분을 다 맞춘 경우 2점
            if (correctCount == 12) {
                return 2;
            }
            // 선분 하나를 생략한 경우 1점
            else if (correctCount == 11) {
                return 1;
            }
            // 선분 두개 이상 생략한 경우 0점
            else {
                return 0;
            }
        }
    }
    // 민수는 / 자전거를 타고 / 공원에 가서 / 11시부터 / 야구를 했다
    private Map<String, Object> checkTest10(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        Map<String, Object> result = new HashMap<>();

        List<Integer> forgetIndex = new ArrayList<>();

        int totalScore = 0;

        if (afterStr.contains("민수")) {
            totalScore = totalScore + 2;
        }
        else {
            forgetIndex.add(1);
        }

        if (afterStr.contains("자전거")) {
            totalScore = totalScore + 2;
        }
        else {
            forgetIndex.add(2);
        }

        if (afterStr.contains("공원")) {
            totalScore = totalScore + 2;
        }
        else {
            forgetIndex.add(3);
        }

        if (afterStr.contains("11시")) {
            totalScore = totalScore + 2;
        }
        else {
            forgetIndex.add(4);
        }

        if (afterStr.contains("야구")) {
            totalScore = totalScore + 2;
        }
        else {
            forgetIndex.add(5);
        }

        result.put("totalScore", totalScore);
        result.put("forgetIndex", forgetIndex);

        return result;
    }
    private int checkTest11(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        if (afterStr.contains("칫솔")) {
            return 1;
        }
        return 0;
    }
    private int checkTest12(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        if (afterStr.contains("그네")) {
            return 1;
        }
        return 0;
    }
    private int checkTest13(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"를 맞춤법이나 어색한 표현이 없게 수정하고 결과값만 알려줘.";
        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        if (afterStr.contains("주사위")) {
            return 1;
        }
        return 0;
    }
    private int checkTest14(String text) {

        return 0;
    }
    private int checkTest15(String text) {
        // 수정 전 문자열
        String beforeStr = "\"" + text + "\"의 문장에서 중복되는 과일 또는 채소를 제외하고 총 몇 개의 과일 또는 채소가 있나요? \n" +
                "\n" +
                "참고로  개수를 셀 때는 아래의 3가지는 제외해주세요.\n" +
                "1. 과일/채소를 가공한 음식 : 무말랭이, 감말랭이, 홍시, 연시, 곶감, 건포도, 시래기 등\n" +
                "2. 곡류, 잡곡류 : 콩, 팥, 쌀, 수수, 조, 보리, 귀리, 율무, 녹두 등\n" +
                "3. 해조류 : 미역, 파래, 곰피, 다시마, 톳 등\n" +
                "\n" +
                "답변은 설명없이 개수만 알려주세요.\n" +
                "예를 들어 답변 예시는 5 입니다. 숫자만 답해주세요.";

        float temperature = 0.5f;
        // GPT로 text 수정
        String afterStr = getGptResponseText(chatService.getChatResponse(beforeStr, temperature, 500)).trim();

        System.out.println("답변: " + afterStr);

        // GPT 응답이 숫자로 잘 오는 경우
        if (Pattern.matches("^[0-9]*$", afterStr)) {
            int count = Integer.parseInt(afterStr);

            if (count >= 15) {
                return 2;
            }
            else if (count >= 9) {
                return 1;
            }
            else {
                return 0;
            }

        }

        return 0;
    }
}
