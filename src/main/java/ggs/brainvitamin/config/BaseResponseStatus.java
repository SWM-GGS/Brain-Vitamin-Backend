package ggs.brainvitamin.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */

@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, 200, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),


    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    USER_ALREADY_EXISTS(false, 2011, "이미 가입된 전화번호입니다."),
    PHONE_NUMBER_NOT_EXISTS(false, 2012, "가입되지 않은 전화번호입니다."),
    NICKNAME_ALREADY_EXISTS(false, 2013, "이미 존재하는 닉네임입니다."),
    INVALID_LOGIN_INFO(false, 2015, "유효하지 않은 로그인입니다"),


    // [POST] /users
    POST_USERS_EMPTY_USERNAME(false,2020,"아이디를 입력해주세요."),
    POST_USERS_INVALID_USERNAME(false,2021,"잘못된 아이디 형식입니다."),

    POST_USERS_EMPTY_PASSWORD(false,2030,"비밀 번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false,2031,"비밀 번호는 특수문자 포함 8자 이상 20자리 이하입니다."),

    POST_USERS_EMPTY_NICKNAME(false,2040,"닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false,2042,"닉네임은 한글 최소 2자, 최대 8자까지 사용 가능합니다."),

    POST_USERS_EMPTY_AGE(false,2050,"나이를 입력해주세요."),
    POST_USERS_INVALID_AGE(false,2051,"올바른 나이를 입력해주세요."),

    POST_USERS_EMPTY_GENDER(false,2060,"성별을 입력해주세요."),
    POST_USERS_INVALID_GENDER(false,2061,"올바른 성별을 입력해주세요."),

    POST_USERS_EMPTY_PHONENUMBER(false,2070,"휴대폰 번호를 입력해주세요."),
    POST_USERS_INVALID_PHONENUMBER(false,2071,"잘못된 휴대폰 번호입니다."),

    POST_USERS_EMPTY_PRIVACY(false,2080,"개인정보 약관 동의가 필요합니다."),
    POST_USERS_INVALID_PRIVACY(false,2081,"잘못된 개인정보 약관 동의입니다."),

    // [POST] /users/login
    POST_USERS_EMPTY_LOGIN_ID(false, 2090, "아이디를 입력해주세요."),
    POST_USERS_OVER_LENGTH_LOGIN_ID(false, 2091, "아이디는 3자리 이상 20자리 이하입니다."),

    // Token 관련 오류
    INVALID_ACCESS_TOKEN(false, 2100, "유효하지 않은 액세스 토큰입니다"),
    INVALID_REFRESH_TOKEN(false, 2101, "유효하지 않은 리프레시 토큰입니다"),
    EMPTY_ACCESS_TOKEN(false, 2102, "액세스 토큰이 비어있습니다."),
    EMPTY_REFRESH_TOKEN(false, 2103, "리프레시 토큰이 비어있습니다."),
    EXPIRED_ACCESS_TOKEN(false, 2104, "만료된 액세스 토큰 입니다."),
    EXPIRED_REFRESH_TOKEN(false, 2105, "만료된 리프레시 토큰 입니다."),

    // 선별 검사 관련 오류
    HISTORY_NOT_EXISTS(false, 2110, "선별검사 결과가 존재하지 않습니다."),
    INVALID_CLOVA_SPEECH(false, 2115, "음성 인식에 실패하였습니다."),

    // 신고 관련 요청 오류
    INVALID_REPORT_TYPE(false,2190,"잘못된 신고 형식입니다."),

    // 페이징 관련 요청 오류
    EMPTY_PAGE_INDEX(false,2200,"페이지 인덱스 값이 필요합니다."),
    INVALID_PAGE_INDEX(false,2201,"잘못된 페이지 인덱스입니다."),

    //게시글 관련 오류
    POST_EMPTY_TOPIC(false,2500,"제목을 입력해주세요."),
    POST_OVER_LENGTH_TOPIC(false,2505,"제목은 최대 45자까지 입력해주세요."),
    POST_EMPTY_CONTENTS(false,2510,"내용을 입력해주세요."),
    POST_OVER_LENGTH_CONTENTS(false,2515,"내용은 최대 255자까지 입력해주세요."),
    POST_NOT_EXISTS(false, 2520, "존재하지 않는 게시글입니다."),

    //댓글 관련 오류
    COMMENT_EMPTY_CONTENTS(false, 2600, "내용을 입력해주세요."),
    COMMENT_OVER_LENGTH_CONTENTS(false, 2605, "내용은 최대 100자까지 입력해주세요."),

    // 감정표현 관련 오류
    EMOTION_NOT_EXISTS(false, 2700, "존재하지 않는 감정표현입니다."),

    // 공통코드 관련 오류
    CODE_NOT_EXISTS(false, 2800, "존재하지 않는 감정표현입니다."),

    INVALID_ACCESS_KAKAO(false, 2900, "카카오 로그인에 실패하였습니다."),
//    INVALID_ACCESS_GOOGLE(false, 2701, "구글 로그인에 실패하였습니다."),
//    INVALID_ACCESS_NAVER(false, 2702, "네이버 로그인에 실패하였습니다."),
//    INVALID_ACCESS_APPLE(false, 2703, "애플 로그인에 실패하였습니다."),

    // 가족 그룹 관련 오류
    INVALID_FAMILY_KEY(false, 2950, "유효한 가족고유 번호가 아닙니다"),
    FAMILY_NOT_EXISTS(false, 2951, "존재하지 않는 가족 그룹입니다."),
    ALREADY_JOINED_FAMILY(false, 2952, "이미 가입한 가족 그룹입니다."),
    MEMBER_NOT_EXISTS(false, 2953, "가족 그룹에 멤버가 존재하지 않습니다."),

    FAILED_TO_CONVERT_JSON(false, 2990, "json data 변환에 실패하였습니다."),
    FAILED_TO_SAVE_AUDIO_FILE(false, 2990, "s3 파일 저장에 실패하였습니다."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    FAILED_TO_LOGIN(false,3010,"없는 아이디이거나 비밀번호가 틀렸습니다."),

    NOT_ACTIVATED_USER(false,3020,"유효한 사용자가 아닙니다."),
    INVALID_USERTYPE(false,3025,"잘못된 사용자 타입입니다."),
    NOT_ACTIVATED_PROBLEM(false,3030,"유효한 문제가 아닙니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    FAIL_SAVE_AUDIO_FILE(false, 4500, "오디오 파일 저장에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
