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

    INVALID_ACCESS_KAKAO(false, 2700, "카카오 로그인에 실패하였습니다."),
    INVALID_ACCESS_GOOGLE(false, 2701, "구글 로그인에 실패하였습니다."),
    INVALID_ACCESS_NAVER(false, 2702, "네이버 로그인에 실패하였습니다."),
    INVALID_ACCESS_APPLE(false, 2703, "애플 로그인에 실패하였습니다."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    FAILED_TO_LOGIN(false,3010,"없는 아이디이거나 비밀번호가 틀렸습니다."),

    NOT_ACTIVATED_USER(false,3020,"유효한 사용자가 아닙니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

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
