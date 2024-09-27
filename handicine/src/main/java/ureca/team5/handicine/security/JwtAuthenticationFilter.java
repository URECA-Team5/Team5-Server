package ureca.team5.handicine.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청에서 JWT 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);

        if (token == null || token.isEmpty()) {
            System.out.println("No Authorization header present, proceeding without token");
            // 토큰이 없는 경우에도 필터를 계속 진행
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("Received Token in JwtAuthenticationFilter: " + token);

        // 토큰이 유효하고 SecurityContext가 비어있는지 확인
        if (token != null && jwtTokenProvider.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 토큰에서 인증 정보 가져오기
            var authentication = jwtTokenProvider.getAuthentication(token);
            System.out.println("Authentication Object: " + authentication);

            if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // SecurityContext에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}