package com.example.moyeothon.Config.OAuth2;

import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        logger.info("요기1");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        logger.info("요기2");

        // Kakao 사용자 정보 추출
        Long id = oAuth2User.getAttribute(userNameAttributeName);
        Map<String, Object> properties = oAuth2User.getAttribute("properties");
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");

        String name = kakaoAccount != null ? (String) kakaoAccount.get("profile_nickname") : null;
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        // 사용자 정보를 로그에 기록
        logger.info("OAuth2 Login Successful - ID: {}, Name: {}, Email: {}", id, name, email);

        // 사용자가 이미 존재하는지 확인
        Optional<UserEntity> userEntityOptional = Optional.ofNullable(userRepository.findByUid(String.valueOf(id)));
        UserEntity userEntity;
        if (userEntityOptional.isPresent()) {
            userEntity = userEntityOptional.get();
            // 이름 및 이메일 업데이트
            userEntity.setName(name);
            userEntity.setEmail(email);
        } else {
            // 존재하지 않으면 새로 생성
            userEntity = UserEntity.builder()
                    .uid(String.valueOf(id))
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode("OAuth2_User_Password"))
                    .provider(registrationId)
                    .build();
            userRepository.save(userEntity);
        }

        logger.info("요기3");

        // 권한 설정
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        logger.info("요기4");

        // OAuth2User 반환
        return new CustomOAuth2User(userEntity, Collections.singletonList(authority), oAuth2User.getAttributes());
    }
}

