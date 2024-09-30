package ureca.team5.handicine.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final UserDetailsService userDetailsService; // UserDetailsService 추가

    // Constructor for DI
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // 토큰 생성
    public String createToken(String username, String roleName, Long userId) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role_name", roleName);
        claims.put("user_id", userId);

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1시간 유효한 토큰

        // 시크릿 키를 디코딩하여 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // 토큰에 서명
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256) // Key 객체와 알고리즘 사용
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        try {
            // 'Bearer ' 접두사가 포함되어 있다면 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);  // 'Bearer ' 뒤의 토큰 부분만 사용
            }

            // 시크릿 키를 BASE64 디코딩 후 Key 객체로 변환
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            // 토큰 파싱 및 클레임 추출
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)  // Key 객체 사용
                    .build()
                    .parseClaimsJws(token)  // Bearer 접두사가 제거된 토큰을 파싱
                    .getBody();

            System.out.println("Decoded Claims: " + claims);  // 디코딩된 클레임 출력
            return claims.getSubject();  // subject에서 사용자 이름 추출

        } catch (Exception e) {
            System.out.println("Error while decoding JWT: " + e.getMessage());  // 디코딩 오류 출력
            throw new RuntimeException("Invalid JWT Token", e);
        }
    }

    // 토큰 파싱 메서드 추가
    private Claims parseToken(String token) {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer ", "").trim())  // Bearer 제거
                .getBody();  // Claims 추출
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            System.out.println("Validating Token in validateToken: " + token);  // 검증할 토큰 출력

            // 'Bearer ' 접두사가 포함되어 있다면 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);  // 'Bearer ' 뒤의 토큰 부분만 사용
            }

            // 시크릿 키를 BASE64 디코딩 후 Key 객체로 변환
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            // JwtParserBuilder를 사용하여 토큰 유효성 검사
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)  // Bearer 접두사가 제거된 토큰을 파싱
                    .getBody();

            System.out.println("Validated Claims: " + claims);  // 파싱된 클레임 출력
            return true;  // 유효한 경우 true 반환

        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());  // 예외 발생 시 오류 메시지 출력
            return false;
        }
    }

    // 요청에서 JWT 토큰을 추출하는 메서드 (resolveToken)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        System.out.println("Raw Authorization Header: " + bearerToken);  // Authorization 헤더 로그 출력
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            System.out.println("Extracted Token: " + token);  // 추출된 토큰 출력
            return token;
        }
        System.out.println("No Authorization header present");
        return null;
    }

    // JWT 토큰으로 인증 객체를 생성하는 메서드 (getAuthentication)
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}