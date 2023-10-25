package com.example.p2k._core.security;

import com.example.p2k.user.Role;
import com.example.p2k.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Getter
@Builder
public class OAuthAttributes {
   private Map<String, Object> attributes;
   private String nameAttributeKey;
   private String name;
   private String email;

   public static OAuthAttributes oAuthUserInfo(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
       if("naver".equals(registrationId)){
           return NaverInfo("id", attributes);
       }else if("kakao".equals(registrationId)){
           return KakaoInfo(userNameAttributeName, attributes);
       }else{
           return GoogleInfo(userNameAttributeName, attributes);
       }
   }

   private static OAuthAttributes NaverInfo(String userNameAttributeName, Map<String, Object> attributes){
       Map<String, Object> response = (Map<String, Object>) attributes.get("response");
       return OAuthAttributes.builder()
               .name((String) response.get("name"))
               .email((String) response.get("email"))
               .attributes(response)
               .nameAttributeKey(userNameAttributeName)
               .build();
   }

    private static OAuthAttributes KakaoInfo(String userNameAttributeName, Map<String, Object> attributes){
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        return OAuthAttributes.builder()
                .name((String) account.get("name"))
                .email((String) account.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes GoogleInfo(String userNameAttributeName, Map<String, Object> attributes){
       return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

   public User toEntity() {
       return User.builder()
               .name(name)
               .email(email)
               .role(Role.ROLE_STUDENT)
               .pending(false)
               .build();
   }
}
