package ggs.brainvitamin.src.user.patient.dto;

import ggs.brainvitamin.config.Status;
import lombok.Builder;
import lombok.Data;
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
    @Builder
    public static class loginDto {

        private String phoneNumber;
        private Status status;

        public Collection<? extends GrantedAuthority> getAuthorities() {  //계정이 갖고있는 권한목록을 리턴한다.
            List<GrantedAuthority> auth = new ArrayList<>();
            auth.add(new SimpleGrantedAuthority("ROLE_USER"));
            return auth;
        }
    }

    @Data
    @Builder
    public static class startVitaminDto {

        private String birthDate;
        private String education;
    }
}
