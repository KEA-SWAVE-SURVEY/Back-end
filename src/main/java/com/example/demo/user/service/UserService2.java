package com.example.demo.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidSurveyException;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.response.SurveyMyPageDto;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.UserNotFoundException;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.request.UserUpdateRequest;
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

import java.util.*;

@Slf4j
@Service
public class UserService2 {
    @Autowired
    OAuthService oAuthService;
    @Autowired
    UserRepository userRepository;

    public List<SurveyMyPageDto> mySurveyList(HttpServletRequest request) throws InvalidTokenException {
        checkInvalidToken(request);
        List<SurveyMyPageDto> surveyMyPageDtos = new ArrayList<>();

        Survey survey= getUser(request).getSurvey();
        System.out.println(survey);
        for(SurveyDocument surveyDocument:survey.getSurveyDocumentList()){
            SurveyMyPageDto surveyMyPageDto = new SurveyMyPageDto();
            surveyMyPageDto.setId(surveyDocument.getId());
            surveyMyPageDto.setDescription(surveyDocument.getDescription());
            surveyMyPageDto.setTitle(surveyDocument.getTitle());
            surveyMyPageDto.setDeadline(surveyDocument.getDeadline());
            surveyMyPageDto.setStartDate(surveyDocument.getStartDate());
            surveyMyPageDtos.add(surveyMyPageDto);

        }
        return surveyMyPageDtos;
    }

    public User getUser(HttpServletRequest request) { //(1)
        Long userCode = (Long) request.getAttribute("userCode");
        User user = userRepository.findByUserCode(userCode).orElseThrow(UserNotFoundException::new);
        return user;
    }

    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) throws Exception {
        checkInvalidToken(request);
        User user = getUser(request);
        System.out.println(user.getEmail());
        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity getLogin(String code,String provider){
        OauthToken oauthToken = oAuthService.getAccessToken(code, provider);
        String jwtToken = oAuthService.SaveUserAndGetToken(oauthToken.getAccess_token(), provider);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        return ResponseEntity.ok().headers(headers).body("\"success\"");
    }
    public String updateMyPage(HttpServletRequest request, UserUpdateRequest userUpdateRequest) throws InvalidTokenException {
        checkInvalidToken(request);
        Long userId =getUser(request).getUserCode();
        Optional<User> optionalUser = userRepository.findByUserCode(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setNickname(userUpdateRequest.getNickname());
            user.setDescription(userUpdateRequest.getDescription());
            userRepository.save(user);
        }else {
            throw new InvalidSurveyException();
        }
        return "success";
    }

    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
        if(request.getHeader("Authorization") == null) {
            log.info("error");
            throw new InvalidTokenException();
        }
        log.info("토큰 체크 완료");
    }


}
