package com.example.moyeothon.Service;

import com.example.moyeothon.Config.JWT.JwtTokenProvider;
import com.example.moyeothon.Config.OAuthProperties.GoogleOAuthProperties;
import com.example.moyeothon.Config.OAuthProperties.KakaoOAuthProperties;
import com.example.moyeothon.DTO.JWTDTO;
import com.example.moyeothon.DTO.UserDTO;
import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final GoogleOAuthProperties googleOAuthProperties;

    // 아이디 중복 확인
    public boolean isUidDuplicate(String uid) {
        return userRepository.existsByUid(uid);
    }

    // 닉네임 중복 확인
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 일반 회원 가입
    public UserDTO createUser(UserDTO userDTO) {
        if (isUidDuplicate(userDTO.getUid())) {
            throw new IllegalArgumentException("중복된 아이디가 존재합니다");
        } else if (isNicknameDuplicate(userDTO.getNickname())) {
            throw new IllegalArgumentException("중복된 닉네임이 존재합니다");
        }
        UserEntity userEntity = userDTO.dtoToEntity();
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setProvider("normal");
        UserEntity savedUser = userRepository.save(userEntity);
        logger.info("회원가입 완료! " + userEntity);
        return UserDTO.entityToDto(savedUser);
    }

    // 일반 로그인
    public JWTDTO login(String uid, String password) {
        UserEntity userEntity = userRepository.findByUid(uid);
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        String token = jwtTokenProvider.generateToken(uid);
        logger.info("로그인 성공! 새로운 토큰이 발급되었습니다");
        return new JWTDTO(token, UserDTO.entityToDto(userEntity));
    }

    // 유저 조회
    public UserDTO getUserByUid(String uid) {
        UserEntity userEntity = userRepository.findByUid(uid);
        return UserDTO.entityToDto(userEntity);
    }

    // 회원 정보 수정
    public UserDTO updateUser(String uid, UserDTO userDTO, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("권한이 없습니다");
        }

        UserEntity userEntity = userRepository.findByUid(uid);

        if (userDTO.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getNickname() != null) {
            userEntity.setNickname(userDTO.getNickname());
        }

        UserEntity updatedUser = userRepository.save(userEntity);
        logger.info("사용자 정보 업데이트 완료! " + updatedUser);
        return UserDTO.entityToDto(updatedUser);
    }

    // 회원 탈퇴
    public UserDTO deleteUser(String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("권한이 없습니다");
        }
        UserEntity userEntity = userRepository.findByUid(uid);
        userRepository.delete(userEntity);
        logger.info("유저의 uid가 " + uid + "인 회원탈퇴 완료!");
        return UserDTO.entityToDto(userEntity);
    }

    // 닉네임 수정, 소셜 로그인 사용자라면 초기 닉네임 설정
    public UserDTO updateNickname(String uid, String nickname, UserDetails userDetails) {
        if(!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("권한이 없습니다");
        }
        UserEntity userEntity = userRepository.findByUid(uid);
        userEntity.setNickname(nickname);
        UserEntity updatedUser = userRepository.save(userEntity);
        logger.info("사용자 닉네임 업데이트 완료! " + updatedUser);
        return UserDTO.entityToDto(updatedUser);
    }

    // 닉네임 랜덤 생성 메서드
    private String randomNickname() {
        String[] A = {"멋진", "용감한", "빠른", "슬기로운", "조용한", "기분좋은", "귀여운", "신비로운", "재밌는", "상큼한", "활기찬", "따뜻한", "멋진", "반짝이는"};
        String[] B = {"사자", "호랑이", "사슴", "독수리", "나무늘보", "고양이", "토끼", "강아지", "부엉이", "너구리", "햄스터", "다람쥐", "펭귄", "고슴도치"};

        Random random = new Random();
        String adjective = A[random.nextInt(A.length)];
        String noun = B[random.nextInt(B.length)];

        return adjective + noun;
    }

    // 카카오 인가 코드로 액세스 토큰을 요청하는 메서드
    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOAuthProperties.getClientId());
        params.add("redirect_uri", kakaoOAuthProperties.getRedirectUri());
        params.add("code", code);
        params.add("client_secret", kakaoOAuthProperties.getClientSecret());

        logger.info("액세스 토큰 요청 URL: {}", url);
        logger.info("액세스 토큰 요청 헤더: {}", headers);
        logger.info("액세스 토큰 요청 파라미터: {}", params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                String accessToken = (String) responseBody.get("access_token");
                logger.info("액세스 토큰을 성공적으로 가져왔습니다: {}", accessToken);
                return accessToken;
            } else {
                logger.error("액세스 토큰을 가져오는데 실패했습니다. 응답 본문이 비어있습니다.");
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("액세스 토큰을 가져오는 중 오류가 발생하였습니다. (위치: getAccessToken): {}", e.getMessage());
            logger.error("응답 본문 (위치: getAccessToken): {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    // 액세스 토큰으로 사용자 정보를 요청하는 메서드
    public Map<String, Object> getUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                logger.info("사용자 정보를 성공적으로 가져왔습니다 : {}", responseBody);
                return responseBody;
            } else {
                logger.error("사용자 정보를 가져오는데 실패했습니다. 응답 본문이 비어있습니다.");
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("사용자 정보를 가져오는 중 오류가 발생했습니다. (위치: getUserInfo): {}", e.getMessage());
            logger.error("응답 본문 (위치: getUserInfo): {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    // 최종적으로 카카오 로그인을 처리하는 메서드
    public JWTDTO loginWithOAuth2(String code) {
        try {
            String accessToken = getAccessToken(code);
            Map<String, Object> userInfo = getUserInfo(accessToken);

            String uid = String.valueOf(userInfo.get("id"));
            if (uid == null) {
                throw new RuntimeException("사용자 ID를 가져올 수 없습니다.");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
            @SuppressWarnings("unchecked")
            Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");

            String name = null;
            String nickname = null;
            if (properties != null) {
                name = (String) properties.get("nickname");
            }
            if (name == null) {
                name = "카카오사용자";
            }

            if (nickname == null) {
                nickname = randomNickname();
            }

            String email = null;
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
            }
            if (email == null) {
                throw new RuntimeException("사용자 이메일을 가져올 수 없습니다.");
            }

            UserEntity userEntity = userRepository.findByUid(uid);

            boolean isNewUser = false;
            if (userEntity == null) {
                userEntity = UserEntity.builder()
                        .uid(uid)
                        .nickname(nickname)
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode("oauth2user"))
                        .provider("kakao")
                        .build();
                userRepository.save(userEntity);
                isNewUser = true;
            } else {
                userEntity.setName(name);
                userEntity.setNickname(nickname);
                userEntity.setEmail(email);
                userRepository.save(userEntity);
            }

            String token = jwtTokenProvider.generateToken(uid);
            logger.info("카카오 로그인 성공! 새로운 토큰이 발급되었습니다");
            return new JWTDTO(token, UserDTO.entityToDto(userEntity));
        } catch (HttpClientErrorException e) {
            logger.error("카카오 API 호출 중 오류가 발생했습니다: {}", e.getMessage());
            logger.error("응답 본문: {}", e.getResponseBodyAsString());
            throw new RuntimeException("카카오 API 호출 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("카카오 로그인 중 오류가 발생했습니다 (위치 : loginWithOAuth2) : {}", e.getMessage());
            throw new RuntimeException("카카오 로그인 중 오류가 발생했습니다. (위치 : loginWithOAuth2)", e);
        }
    }

    // 구글 인가 코드로 액세스 토큰을 요청하는 메서드
    public String getGoogleAccessToken(String code) {
        String url = "https://oauth2.googleapis.com/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleOAuthProperties.getClientId());
        params.add("client_secret", googleOAuthProperties.getClientSecret());
        params.add("redirect_uri", googleOAuthProperties.getRedirectUri());
        params.add("code", code);

        logger.info("액세스 토큰 요청 URL: {}", url);
        logger.info("액세스 토큰 요청 헤더: {}", headers);
        logger.info("액세스 토큰 요청 파라미터: {}", params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                String accessToken = (String) responseBody.get("access_token");
                logger.info("액세스 토큰을 성공적으로 가져왔습니다: {}", accessToken);
                return accessToken;
            } else {
                logger.error("액세스 토큰을 가져오는데 실패했습니다. 응답 본문이 비어있습니다.");
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("액세스 토큰을 가져오는 중 오류가 발생하였습니다. (위치: getGoogleAccessToken): {}", e.getMessage());
            logger.error("응답 본문 (위치: getGoogleAccessToken): {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    // 액세스 토큰으로 사용자 정보를 요청하는 메서드
    public Map<String, Object> getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        logger.info("사용자 정보 요청 URL: {}", url);
        logger.info("사용자 정보 요청 헤더: {}", headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                logger.info("사용자 정보를 성공적으로 가져왔습니다 : {}", responseBody);
                return responseBody;
            } else {
                logger.error("사용자 정보를 가져오는데 실패했습니다. 응답 본문이 비어있습니다.");
                return null;
            }
        } catch (HttpClientErrorException e) {
            logger.error("사용자 정보를 가져오는 중 오류가 발생했습니다. (위치: getGoogleUserInfo): {}", e.getMessage());
            logger.error("응답 본문 (위치: getGoogleUserInfo): {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    // 최종적으로 구글 로그인을 처리하는 메서드
    public JWTDTO loginWithGoogleOAuth2(String code) {
        try {
            String accessToken = getGoogleAccessToken(code);
            Map<String, Object> userInfo = getGoogleUserInfo(accessToken);

            String uid = (String) userInfo.get("sub");
            String name = (String) userInfo.get("name");
            String email = (String) userInfo.get("email");

            if (uid == null || name == null || email == null) {
                throw new RuntimeException("필수 사용자 정보를 가져올 수 없습니다.");
            }

            String nickname = null;
            if (nickname == null) {
                nickname = randomNickname();
            }

            Optional<UserEntity> userEntityOptional = Optional.ofNullable(userRepository.findByUid(uid));
            UserEntity userEntity;
            if (userEntityOptional.isPresent()) {
                userEntity = userEntityOptional.get();
                userEntity.setName(name);
                userEntity.setNickname(nickname);
                userEntity.setEmail(email);
            } else {
                userEntity = UserEntity.builder()
                        .uid(uid)
                        .name(name)
                        .nickname(nickname)
                        .email(email)
                        .password(passwordEncoder.encode("OAuth2_User_Password"))
                        .provider("google")
                        .build();
                userRepository.save(userEntity);
            }

            String token = jwtTokenProvider.generateToken(uid);
            logger.info("구글 로그인 성공! 새로운 토큰이 발급되었습니다");
            return new JWTDTO(token, UserDTO.entityToDto(userEntity));
        } catch (HttpClientErrorException e) {
            logger.error("구글 API 호출 중 오류가 발생했습니다: {}", e.getMessage());
            logger.error("응답 본문: {}", e.getResponseBodyAsString());
            throw new RuntimeException("구글 API 호출 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("구글 로그인 중 오류가 발생했습니다 (위치 : loginWithGoogleOAuth2) : {}", e.getMessage());
            throw new RuntimeException("구글 로그인 중 오류가 발생했습니다. (위치 : loginWithGoogleOAuth2)", e);
        }
    }
}
