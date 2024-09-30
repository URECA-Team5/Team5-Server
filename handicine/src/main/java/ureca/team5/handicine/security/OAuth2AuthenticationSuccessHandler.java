package ureca.team5.handicine.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import ureca.team5.handicine.entity.User;
import ureca.team5.handicine.repository.UserRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;  // 사용자 저장소

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();  // 인증된 사용자 정보
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();  // 카카오, 구글 구분

        // 로그인 제공자에 따라 사용자 정보 처리
        String username;
        String email;

        if ("kakao".equals(registrationId)) {
            username = getKakaoUsername(oAuth2User);
            email = getKakaoEmail(oAuth2User);
        } else if ("google".equals(registrationId)) {
            username = getGoogleUsername(oAuth2User);
            email = getGoogleEmail(oAuth2User);
        } else {
            throw new IllegalArgumentException("지원하지 않는 로그인 제공자입니다: " + registrationId);
        }

        // 데이터베이스에서 해당 사용자 정보 조회
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Long userId = user.getUserId();  // 데이터베이스에 있는 user_id 사용
            String roleName = user.getRole().getRoleName();  // 역할 가져오기

            // JWT 생성
            String token = jwtTokenProvider.createToken(username, roleName, userId);

            // JWT 생성 후 로그 찍기
            System.out.println("Generated JWT Token: " + token);

            // JWT를 프론트엔드로 리다이렉트 (쿼리 파라미터로 전달)
            response.setHeader("Authorization", "Bearer " + token);
            response.sendRedirect("http://localhost:3000/oauth2/callback?token=" + token);
        } else {
            // 유저가 없을 경우 처리 로직 추가 가능
            throw new IllegalStateException("로그인한 사용자를 찾을 수 없습니다.");
        }
    }

    // 카카오에서 사용자 정보 추출하는 메서드
    private String getKakaoUsername(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return (String) profile.get("nickname");
    }

    private String getKakaoEmail(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    // 구글에서 사용자 정보 추출하는 메서드
    private String getGoogleUsername(OAuth2User oAuth2User) {
        return (String) oAuth2User.getAttributes().get("name");
    }

    private String getGoogleEmail(OAuth2User oAuth2User) {
        return (String) oAuth2User.getAttributes().get("email");
    }
}