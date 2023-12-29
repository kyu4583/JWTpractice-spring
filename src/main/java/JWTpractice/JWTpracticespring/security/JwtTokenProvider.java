package JWTpractice.JWTpracticespring.security;

import JWTpractice.JWTpracticespring.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final int AT_EXP = 10000;   // Access Token 만료 시간
    private final int RT_EXP = 60000;   // Refresh Token 만료 시간
    private final Key key;

    // application.yml에서 secret 값 가져와서 key에 저장
    public JwtTokenProvider(@Value("${jwt.secret}"/*yml 파일 내 설정값*/) String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);    //16진수 시크릿키를 바이트 배열로 변환
        this.key = Keys.hmacShaKeyFor(keyBytes);    //바이트 배열로 HMAC SHA 키 생성
    }

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities()    // 권한 목록 가져오기
                .stream()
                .map(GrantedAuthority::getAuthority)  // 권한을 각각 문자열로 변환
                                                    // GrantedAuthority(인터페이스) 객체를 받아 getAuthority 메서드 실행 결과를 사용
                .collect(Collectors.joining(","));  // 콤마를 껴서 문자열들을 하나의 문자열로 변환
                //사용자의 모든 권한이 쉼표로 구분된 하나의 문자열로 변환됨

        long now = (new Date()).getTime();  // 현재시간, 토큰 만료 시간 결정용

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + AT_EXP);
        String accessToken = Jwts.builder()   // Jwt 토큰 생성
                .setSubject(authentication.getName())   // 토큰 제목 설정(사용자 이름)
                .claim("auth", authorities)     // 사용자 권한 목록을 포함하는 커스텀 클레임 추가
                .setExpiration(accessTokenExpiresIn)    // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)    // 토큰 암호화 알고리즘, 키 설정
                .compact(); // 토큰 생성(문자열 형태)

        // Refresh Token 생성
        String refreshToken = Jwts.builder()    // Jwt 토큰 생성
                .setExpiration(new Date(now + RT_EXP))
                .signWith(key, SignatureAlgorithm.HS256)    // 토큰 암호화 알고리즘, 키 설정
                .compact(); // 토큰 생성(문자열 형태)

        return JwtToken.builder()   // Jwt"Token" 객체 생성
                .grantType("Bearer")    // 'Baerer' : '소지자', 이 토큰을 소지하면 권한을 가진 것과 같다.
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)   // SimpleGrantedAuthority 클래스의 생성자를 참조해서 문자열을 객체로 변환
                .collect(Collectors.toList());  // 권한 목록을 리스트로 변환

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject()/*식별자(ex.이름)*/, "", authorities);   // 유저 식별자, 권한목록을 담은 유저 정보 객체
        return new UsernamePasswordAuthenticationToken(principal, "", authorities); // 유저 정보, 권한목록을 담은 객체 리턴
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 검증에 쓸 키 설정
                    .build()
                    .parseClaimsJws(token); // 파스 및 검증
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    // accessToken 파싱
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody(); // claims 객체 반환, claims: 토큰에 담긴 정보 조각들
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}