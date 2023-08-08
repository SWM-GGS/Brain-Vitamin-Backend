package ggs.brainvitamin.src.user.patient.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    private AccessTokenDto accessTokenDto;
    private RefreshTokenDto refreshTokenDto;

    @Data
    @Builder
    public static class AccessTokenDto {

        private String accessToken;
        private Long accessTokenExpiresTime;
    }

    @Data
    @Builder
    public static class RefreshTokenDto {

        private String refreshToken;
        private Long refreshTokenExpiresTime;
    }
}