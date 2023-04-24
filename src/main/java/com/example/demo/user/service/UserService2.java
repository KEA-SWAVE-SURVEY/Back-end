package com.example.demo.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.OAuth.Google.GoogleProfile;
import com.example.demo.util.OAuth.JwtProperties;
import com.example.demo.util.OAuth.OauthToken;
import com.example.demo.util.OAuth.kakao.KakaoProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Date;

@Slf4j
@Service
public class UserService2 {

    @Autowired
    UserRepository userRepository;

    public OauthToken getAccessToken(String code, String provider) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String grantType;
        String clientId;
        String clientSecret;
        String redirectUri;
        if (provider.equals("kakao")) {
            grantType = "authorization_code";
            clientId = "4646a32b25c060e42407ceb8c13ef14a";
            clientSecret = "AWyAH1M24R9EYfUjJ1KCxcsh3DwvK8F7";
            redirectUri = "http://localhost:3000/oauth/callback/kakao";
        } else if (provider.equals("google")) {
            log.info("code: " + code);
            log.info("Provider: " + provider);
            grantType = "authorization_code";
            clientId = "278703087355-limdvm0almc07ldn934on122iorpfdv5.apps.googleusercontent.com";
            clientSecret = "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";
            redirectUri = "http://localhost:3000/oauth/callback/google";
        } else {
            throw new IllegalArgumentException("Invalid Provider: " + provider);
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> tokenResponse = rt.exchange(
                getProviderTokenUrl(provider),
                HttpMethod.POST,
                tokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(tokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    //provider에 따라 URL 제공 구분
    private String getProviderTokenUrl(String provider) {
        if (provider.equals("kakao")) {
            return "https://kauth.kakao.com/oauth/token";
        } else if (provider.equals("google")) {
            return "https://oauth2.googleapis.com/token";
        } else {
            throw new IllegalArgumentException("Invalid Provider: " + provider);
        }
    }

    public User saveUser(String token, String provider) {
        Object profile = null;
        User user = null;
        if (provider.equals("kakao")) {
            profile = findKakaoProfile(token);
            user = userRepository.findByEmail(((KakaoProfile) profile).getKakao_account().getEmail());

            if (user == null) {
                user = User.builder()
                        .id(((KakaoProfile) profile).getId())
                        //(4)
                        .profileImg(((KakaoProfile) profile).getKakao_account().getProfile().getProfile_image_url())
                        .nickname(((KakaoProfile) profile).getKakao_account().getProfile().getNickname())
                        .email(((KakaoProfile) profile).getKakao_account().getEmail())
                        //(5)
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            }
        } else if (provider.equals("google")) {
            profile = findGoogleProfile(token);
            user = userRepository.findByEmail(((GoogleProfile) profile).getEmail());

            if (user == null) {
                user = User.builder()
                        .id(((GoogleProfile) profile).getId())
                        .profileImg(((GoogleProfile) profile).getPicture())
                        .nickname(((GoogleProfile) profile).getName())
                        .email(((GoogleProfile) profile).getEmail())
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            }
        } else {
            throw new IllegalArgumentException("Invalid Provider: " + provider);
        }

        return user;
    }

    public KakaoProfile findKakaoProfile(String token) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;
    }

    public GoogleProfile findGoogleProfile(String token) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> googleProfileRequest =
                new HttpEntity<>(headers);

        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> googleProfileResponse = rt.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.POST,
                googleProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleProfile googleProfile = null;
        try {
            googleProfile = objectMapper.readValue(googleProfileResponse.getBody(), GoogleProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return googleProfile;
    }

    public String SaveUserAndGetToken(String token, String provider) {
        if (provider.equals("kakao")) {
            KakaoProfile profile = findKakaoProfile(token);

            User user = userRepository.findByEmail(profile.getKakao_account().getEmail());
            if(user == null) {
                user = User.builder()
                        .id(profile.getId())
                        .profileImg(profile.getKakao_account().getProfile().getProfile_image_url())
                        .nickname(profile.getKakao_account().getProfile().getNickname())
                        .email(profile.getKakao_account().getEmail())
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            }

            return createToken(user);
        } else if (provider.equals("google")) {
            GoogleProfile profile = findGoogleProfile(token);

            User user = userRepository.findByEmail(profile.getEmail());
            if(user == null) {
                user = User.builder()
                        .id(profile.getId())
                        .profileImg(profile.getPicture())
                        .nickname(profile.getName())
                        .email(profile.getEmail())
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            }

            return createToken(user);
        } else {
            throw new RuntimeException("Unsupported provider: " + provider);
        }
    }

    public String createToken(User user) {

        String jwtToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))
                .withClaim("id", user.getUserCode())
                .withClaim("nickname", user.getNickname())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        return jwtToken;
    }

    public User getUser(HttpServletRequest request) { //(1)
        Long userCode = (Long) request.getAttribute("userCode");

        User user = userRepository.findByUserCode(userCode);

        return user;
    }
}
