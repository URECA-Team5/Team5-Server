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

        // OAuth2User에서 사용자 정보 추출
        String username = getUsernameFromOAuth2User(oAuth2User);
        String email = getEmailFromOAuth2User(oAuth2User);
        String roleName = "MEMBER";  // 기본 역할 설정
        Long userId = getUserIdFromOAuth2User(oAuth2User);  // 사용자 ID 추출

        // JWT 생성
        String token = jwtTokenProvider.createToken(username, roleName, userId);

        // JWT를 프론트엔드로 리다이렉트 (쿼리 파라미터로 전달)
        response.sendRedirect("http://localhost:3000/oauth2/callback?token=" + token);
    }

    // OAuth2User에서 username 추출 (카카오의 닉네임을 username으로 사용)
    private String getUsernameFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("nickname"); // 카카오의 닉네임을 username으로 사용
    }

    // OAuth2User에서 email 추출
    private String getEmailFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    // OAuth2User에서 userId 추출 (카카오의 'id' 필드 사용)
    private Long getUserIdFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return Long.parseLong(attributes.get("id").toString());
    }
}