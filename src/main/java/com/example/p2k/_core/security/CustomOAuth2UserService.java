package com.example.p2k._core.security;

import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.oAuthUserInfo(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User existingUser = userRepository.findByEmail(attributes.getEmail()).orElse(null);
        User user = null;

        if (existingUser != null) {
            user = existingUser;
        } else {
            user = attributes.toEntity();
            userRepository.save(user);
        }

        httpSession.setAttribute("user", new SessionUser(user));

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}
