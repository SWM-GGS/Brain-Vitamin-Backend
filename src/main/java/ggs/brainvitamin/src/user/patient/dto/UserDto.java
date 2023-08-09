package ggs.brainvitamin.src.user.patient.dto;

import ggs.brainvitamin.config.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDto {

    @Data
    @Builder
    public static class signUpDto {

        private String name;
        private String nickname;
        private String phoneNumber;
        private Integer fontSize;
    }

    @Data
    public static class loginDto {

        private String phoneNumber;

    }

    @Data
    @Builder
    public static class startVitaminDto {

        private String birthDate;
        private String education;
    }
}
