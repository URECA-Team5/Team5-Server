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
    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

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
        // 시크릿 키를 BASE64 디코딩 후 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        // JwtParserBuilder를 사용해 파서 생성 및 서명 키 설정
        return Jwts.parserBuilder()
                .setSigningKey(key) // Key 객체 사용
                .build() // 파서 빌드
                .parseClaimsJws(token) // 토큰 파싱
                .getBody() // 클레임 추출
                .getSubject(); // subject에서 사용자 이름 추출
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 시크릿 키를 BASE64 디코딩 후 Key 객체로 변환
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            // JwtParserBuilder를 사용하여 토큰 유효성 검사
            Jwts.parserBuilder()
                    .setSigningKey(key) // Key 객체 사용
                    .build() // 파서 빌드
                    .parseClaimsJws(token); // 토큰 파싱 및 유효성 검증

            return true; // 유효한 경우 true 반환
        } catch (Exception e) {
            return false; // 예외 발생 시 false 반환
        }
    }

    // 요청에서 JWT 토큰을 추출하는 메서드 (resolveToken)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 부분을 제거하고 토큰 반환
        }
        return null;
    }

    // JWT 토큰으로 인증 객체를 생성하는 메서드 (getAuthentication)
    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}