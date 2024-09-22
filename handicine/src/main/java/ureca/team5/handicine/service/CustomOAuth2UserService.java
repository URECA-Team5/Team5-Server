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
    private RoleRepository RoleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // Kakao or Google
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleName())),
                attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        Optional<Role> defaultRole = RoleRepository.findByRoleName("MEMBER");
        User user = userRepository.findByUsername(attributes.getName())
                .map(entity -> entity.update(attributes.getName()))
                .orElse(attributes.toEntity(defaultRole));

        return userRepository.save(user);
    }
}