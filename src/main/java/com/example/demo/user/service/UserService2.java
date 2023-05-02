package com.example.demo.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.UserNotFoundException;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.OAuth.Git.GItProfile;
import com.example.demo.util.OAuth.Google.GoogleProfile;
import com.example.demo.util.OAuth.JwtProperties;
import com.example.demo.util.OAuth.OauthToken;
import com.example.demo.util.OAuth.kakao.KakaoProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

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
        } else if (provider.equals("git")) {
            log.info("code: " + code);
            log.info("Provider: " + provider);
            grantType = "authorization_code";
            clientId = "Iv1.986aaa4d78140fb7";
            clientSecret = "0c8e730012e8ca8e41a3922358572457f5cc57e4";
            redirectUri = "http://localhost:3000/oauth/callback/git";
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
            if (provider.equals("git")) {
                // git은 body가 문자열 형식
                String responseBody = tokenResponse.getBody();
                // 문자열 파싱하여 Map 객체 생성
                Map<String, String> map = new HashMap<>();
                String[] pairs = responseBody.split("&");
                for (String pair : pairs) {
                    String[] tokens = pair.split("=", 2);
                    String key = tokens[0];
                    String value = tokens.length == 2 ? tokens[1] : "";
                    map.put(key, value);
                }
                // Map 객체를 JSON 형태로 변환
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(map);

                oauthToken = objectMapper.readValue(json, OauthToken.class);
            } else {
                oauthToken = objectMapper.readValue(tokenResponse.getBody(), OauthToken.class);
            }
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
        } else if (provider.equals("git")) {
            return "https://github.com/login/oauth/access_token";
        } else {
            throw new IllegalArgumentException("Invalid Provider: " + provider);
        }
    }

    // SaveUserAndGetToken 중복..?
    public User saveUser(String token, String provider) throws ParseException {
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
        } else if (provider.equals("git")) {
            profile = findGitProfile(token);
            user = userRepository.findByEmail(((GItProfile) profile).getEmail());
            JSONParser parser = new JSONParser();
            String strJson=(((GItProfile) profile).getEmail());
            System.out.println(strJson);
            JSONArray values = (JSONArray)parser.parse(strJson);
            JSONObject value = (JSONObject)values.get(0);
            if (user == null) {
                user = User.builder()
                        .id(((GItProfile) profile).getId())
                        .profileImg(((GItProfile) profile).getPicture())
                        .nickname(((GItProfile) profile).getName())
                        .email((String)value.get("email"))
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            }
        }else {
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

    public GItProfile findGitProfile(String token) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> gitProfileRequest =
                new HttpEntity<>(headers);
        //spring oauth access token github server userinfo
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> gitProfileResponse = rt.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                gitProfileRequest,
                String.class
        );
        String name=null;
        String picture=null;
        try {
            JSONParser parser = new JSONParser();
            String userInfo = gitProfileResponse.getBody();
            JSONObject jsonObject = (JSONObject) parser.parse(userInfo);
            name = (String)jsonObject.get("login");
            picture = (String)jsonObject.get("avatar_url");
        }catch (Exception e) {
            e.printStackTrace();
        }


        //Git은 email 정보를 다시 한번 받아와야 함
        ResponseEntity<String> gitEmailResponse = rt.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                gitProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        GItProfile gitProfile = null;
        try {
            gitProfile = objectMapper.readValue(gitProfileResponse.getBody(), GItProfile.class);

            gitProfile.email = gitEmailResponse.getBody();
            gitProfile.name=name;
            gitProfile.picture=picture;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return gitProfile;
    }

    public String SaveUserAndGetToken(String token, String provider){
        if (provider.equals("kakao")) {
            KakaoProfile profile = findKakaoProfile(token);

            //회원 정보 조회 by Email
//            User user = userRepository.findByEmail(profile.getKakao_account().getEmail());
            User user = userRepository.findByEmailAndProvider(profile.getKakao_account().getEmail(),provider);
            if (user == null) {
                user = User.builder()
                        .id(profile.getId())
                        .profileImg(profile.getKakao_account().getProfile().getProfile_image_url())
                        .nickname(profile.getKakao_account().getProfile().getNickname())
                        .email(profile.getKakao_account().getEmail())
                        .provider(provider)
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            } else {
                log.info("기존 회원 -> 회원 가입 건너 뜀");
            }
            return createToken(user);
        } else if (provider.equals("google")) {
            GoogleProfile profile = findGoogleProfile(token);

            //회원 정보 조회 by Email
//            User user = userRepository.findByEmail(profile.getEmail());
            User user = userRepository.findByEmailAndProvider(profile.getEmail(),provider);
            //새로운 회원이면 등록
            if(user == null) {
                user = User.builder()
                        .id(profile.getId())
                        .profileImg(profile.getPicture())
                        .nickname(profile.getName())
                        .email(profile.getEmail())
                        .provider(provider)
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            } else {
                log.info("기존 회원 -> 회원 가입 건너 뜀");
            }
            //기존 회원이면 저장 건너뛰고 토큰 생성
            return createToken(user);
        }else if (provider.equals("git")) {
            GItProfile profile = findGitProfile(token);
            JSONObject value = null;
            try {
                JSONParser parser = new JSONParser();
                String strJson = (profile.getEmail());
                System.out.println(strJson);
                JSONArray values = (JSONArray) parser.parse(strJson);
                value = (JSONObject) values.get(0);
            }catch (Exception e) {
                e.printStackTrace();
            }

            //회원 정보 조회 by Email
//            User user = userRepository.findByEmail((String)value.get("email"));
            User user = userRepository.findByEmailAndProvider((String)value.get("email"),provider);
            if(user == null) {
                user = User.builder()
                        .id(profile.getId())
                        .profileImg(profile.getPicture())
                        .nickname(profile.getName())
                        .email((String)value.get("email"))
                        .provider(provider)
                        .userRole("ROLE_USER").build();

                userRepository.save(user);
            } else {
                log.info(String.valueOf(user));
                log.info("기존 회원 -> 회원 가입 건너 뜀");
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

        //회원 정보 조회 검사
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(UserNotFoundException::new);

        return user;
    }
}
