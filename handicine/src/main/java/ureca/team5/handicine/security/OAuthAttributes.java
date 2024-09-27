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
        if ("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        }
        return ofGoogle(userNameAttributeName, attributes); // 기본적으로 Google 사용
    }

    // Google 로그인 사용
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 카카오 로그인 사용
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))  // 이메일이 반드시 존재함
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