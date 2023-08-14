package ggs.brainvitamin.src.user.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
        private String familyKey;
    }

    @Data
    @Builder
    public static class SignUpDto {

        private String name;
        private String nickname;
        private String phoneNumber;
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
