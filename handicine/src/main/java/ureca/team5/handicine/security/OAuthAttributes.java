package ureca.team5.handicine.security;

import lombok.Builder;
import lombok.Getter;
import ureca.team5.handicine.entity.Role;
import ureca.team5.handicine.entity.User;

import java.util.Map;
import java.util.Optional;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        }
        throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
    }

    // 구글 OAuth 속성 처리
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))  // 구글에서 제공하는 사용자 이름
                .email((String) attributes.get("email"))  // 구글에서 제공하는 사용자 이메일
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)  // 구글의 고유 사용자 ID (sub)
                .build();
    }

    // 카카오 OAuth 속성 처리
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        System.out.println("Kakao OAuth2 attributes: " + attributes);  // Kakao에서 받은 사용자 정보 로그 출력

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        // 이메일이 없을 경우 처리
        String email = (String) kakaoAccount.get("email");
        if (email == null || email.isEmpty()) {
            email = profile.get("nickname") + "@kakao.com";  // 임시 이메일 할당
            System.out.println("Email is null or empty. Assigning default email: " + email);
        }

        System.out.println("Kakao OAuth2 name: " + profile.get("nickname"));
        System.out.println("Kakao OAuth2 email: " + email);

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email(email)
                .attributes(attributes)
                .nameAttributeKey("id")
                .build();
    }

    // User 엔티티로 변환
    public User toEntity(Optional<Role> role) {
        return User.builder()
                .username(name)
                .email(email)
                .role(role.orElse(null))
                .build();
    }
}