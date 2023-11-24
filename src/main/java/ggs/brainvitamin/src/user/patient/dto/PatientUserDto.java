package ggs.brainvitamin.src.user.patient.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

public class PatientUserDto {

    @Data
    @Builder
    public static class PatientDetailDto {

        private Long id;
        private String name;
        private String nickname;
        private String phoneNumber;
        private Integer fontSize;
        private String profileImgUrl;
        private String education;
        private String familyKey;
        private Long familyId;
    }

    @Data
    @Builder
    public static class SignUpDto {

        @NotBlank(message = "이름을 입력해주세요.")
        @Pattern(regexp = "[가-힣]{2,20}", message = "잘못된 이름 형식입니다.")
        private String name;

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣~^()_]{2,10}$", message = "잘못된 닉네임 형식입니다.")
        private String nickname;

        @NotBlank(message = "휴대폰 번호를 입력해주세요.")
        @Pattern(regexp = "01([0|1])([0-9]{8})", message = "올바른 휴대폰 번호를 입력해주세요.")
        private String phoneNumber;

        @NotNull(message = "폰트 크기를 입력해주세요.")
        @Min(value = 1, message = "잘못된 폰트 크기입니다.")
        @Max(value = 3, message = "잘못된 폰트 크기입니다.")
        private Integer fontSize;
    }

    @Data
    public static class PhoneNumberDto {

        @NotBlank(message = "휴대폰 번호를 입력해주세요")
        @Pattern(regexp = "01([0|1])([0-9]{8})")
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class LoginResponseDto {

        private PatientDetailDto patientDetailDto;
        private TokenDto tokenDto;
    }

    @Data
    @Builder
    public static class StartVitaminDto {

        private String birthDate;
        private String education;
    }
}
