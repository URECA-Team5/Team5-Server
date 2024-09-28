package ureca.team5.handicine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import ureca.team5.handicine.entity.Role;
import ureca.team5.handicine.entity.User;
import ureca.team5.handicine.repository.RoleRepository;
import ureca.team5.handicine.repository.UserRepository;
import ureca.team5.handicine.security.OAuthAttributes;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("CustomOAuth2UserService loadUser 호출됨");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // Identify provider (Kakao or Google)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // Log the received user attributes for debugging
        System.out.println("OAuth2 User Attributes: " + oAuth2User.getAttributes());

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);  // Save or update user info in DB

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName())),
                attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        Optional<Role> defaultRole = roleRepository.findByRoleName("MEMBER");

        // Log to verify which attributes are being processed
        System.out.println("Processing user: " + attributes.getName());

        User user = userRepository.findByUsername(attributes.getName())
                .map(entity -> entity.update(attributes.getName(), attributes.getEmail()))
                .orElse(attributes.toEntity(defaultRole));

        if (user.getPassword() == null) {
            user.setPassword("oauth2user"); // 실제 운영에서는 보안에 맞는 기본값 또는 랜덤 값을 설정해야 합니다.
        }

        return userRepository.save(user);
    }
}