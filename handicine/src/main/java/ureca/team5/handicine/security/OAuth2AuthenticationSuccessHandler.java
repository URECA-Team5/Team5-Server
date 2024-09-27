package ureca.team5.handicine.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getName();
        String roleName = "ROLE_USER";  // 기본 역할 설정
        Long userId = getUserIdFromOAuth2User(oAuth2User);  // 사용자 ID 추출

        // JWT 생성
        String token = jwtTokenProvider.createToken(username, roleName, userId);

        // JWT를 응답 헤더로 전달
        response.setHeader("Authorization", "Bearer " + token);
        getRedirectStrategy().sendRedirect(request, response, "/");
    }

    private Long getUserIdFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return Long.parseLong(attributes.get("id").toString());
    }
}