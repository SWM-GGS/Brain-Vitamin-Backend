package ggs.brainvitamin.jwt;

import ggs.brainvitamin.src.user.patient.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String accessTokenSecret;
    private final String refreshTokenSecret;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    private Key accessTokenKey;
    private Key refreshTokenKey;

    public TokenProvider(@Value("${jwt.access-token-secret}") String accessTokenSecret,
                         @Value("${jwt.refresh-token-secret}") String refreshTokenSecret,
                         @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
                         @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        this.accessTokenSecret = accessTokenSecret;
        this.refreshTokenSecret = refreshTokenSecret;
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000 * 7;
    }

    /**
     * 빈이 생성되고 의존성을 주입받은 후에, base64로 디코딩한 secret 값을 주입하기 위함.
     */
    @Override
    public void afterPropertiesSet() {
        byte[] accessTokenKeyBytes = Decoders.BASE64.decode(accessTokenSecret);
        byte[] refreshTokenKeyBytes = Decoders.BASE64.decode(refreshTokenSecret);

        this.accessTokenKey = Keys.hmacShaKeyFor(accessTokenKeyBytes);
        this.refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenKeyBytes);
    }

    /**
     * Authentication에 있는 권한 정보를 이용해서 엑세스 토큰을 생성한다.
     * 정보를 바탕으로 권한을 가져오고, 유효시간과 암호화를 통해 토큰을 생성한다.
     */
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessTokenExpiredTime = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(accessTokenKey, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenExpiredTime)
                .compact();
    }

    /**
     * Authentication에 있는 권한 정보를 이용해서 리프레쉬 토큰을 생성한다.
     * 정보를 바탕으로 권한을 가져오고, 유효시간과 암호화를 통해 토큰을 생성한다.
     */
    public String createRefreshToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date refreshTokenExpiredTime = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder().setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(refreshTokenKey, SignatureAlgorithm.HS512)
                .setExpiration(refreshTokenExpiredTime).compact();
    }

    /**
     * 토큰을 이용해서 권한 정보를 리턴하는 메서드
     * 토큰을 이용해서 클레임을 만들고, 클레임에서 권한 정보를 가져와서 유저 객체를 만든다.
     *
     * claim : JWT의 속성 정보
     */
    public Authentication getAuthenticationByAccessToken(String token) {

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(accessTokenKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Authentication getAuthenticationByRefreshToken(String token) {

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(refreshTokenKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities  = Arrays.stream(
                        claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰의 유효성을 검증하기 위함 -> 파싱 후 검증하고 리턴한다.
     */
    public boolean isValidAccessToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            logger.info("잘못된 JWT 서명입니다.");
        }catch (ExpiredJwtException e){
            logger.info("만료된 Access Token 입니다.");
        }catch (UnsupportedJwtException e){
            logger.info("지원하지 않는 JWT Token 입니다.");
        }catch (MissingClaimException e){
            logger.info("알 수 없는 에러가 발생했습니다.");
        }
        return false;
    }

    public boolean isExpiredAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            logger.info("만료된 Access Token 입니다.");
            return true;
        }
    }

    public boolean isValidRefreshToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(refreshTokenKey).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            logger.info("잘못된 JWT 서명입니다.");
        }catch (ExpiredJwtException e){
            logger.info("만료된 Refresh Token 입니다.");
        }catch (UnsupportedJwtException e){
            logger.info("지원하지 않는 JWT Token 입니다.");
        }catch (MissingClaimException e){
            logger.info("알 수 없는 에러가 발생했습니다.");
        }
        return false;
    }

    public boolean isExpiredRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshTokenKey).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            logger.info("만료된 Refresh Token 입니다.");
            return true;
        }
    }

    public TokenDto reIssueAccessAndRefreshToken(String refreshToken) {
        Authentication authentication = this.getAuthenticationByRefreshToken(refreshToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new TokenDto(this.createAccessToken(authentication), this.createRefreshToken(authentication));
    }

}
