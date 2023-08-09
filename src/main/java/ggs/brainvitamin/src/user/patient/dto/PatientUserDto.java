package ggs.brainvitamin.src.user.patient.dto;

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
    public static class signUpDto {

        private String name;
        private String nickname;
        private String phoneNumber;
        private Integer fontSize;
    }

    @Data
    public static class loginRequestDto {

        private String phoneNumber;
    }

    @Data
    @Builder
    public static class loginResponseDto {

        private PatientDetailDto patientDetailDto;
        private TokenDto tokenDto;
    }

    @Data
    @Builder
    public static class startVitaminDto {

        private String birthDate;
        private String education;
    }
}
