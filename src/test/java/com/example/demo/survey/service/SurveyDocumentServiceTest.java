package com.example.demo.survey.service;

import com.example.demo.survey.domain.Choice;
import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import com.example.demo.util.OAuth.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.survey.domain.QSurveyDocument.surveyDocument;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SurveyDocumentServiceTest {
    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    QuestionDocumentRepository questionDocumentRepository;
    @Autowired
    ChoiceRepository choiceRepository;
    @Autowired
    UserService2 userService;
    @Autowired
    UserRepository userRepository;
    MockHttpServletRequest servletRequest;
    @BeforeEach
    void clean() {
        // User 정보 생성
        User user = User.builder()
                .nickname("김기현")
                .email("abc@naver.com")
                .provider("google")
                .userRole("ROLE_USER")
                .build();
        userRepository.save(user);
        String userToken = userService.createToken(user);
        servletRequest = new MockHttpServletRequest();
        servletRequest.setAttribute("userCode", userRepository.findByEmail("abc@naver.com").getUserCode());
        servletRequest.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + userToken);

        surveyRepository.deleteAll();
    }

    @Test @DisplayName("설문 레포지토리에 저장")
    @Transactional
    void test1() throws Exception{
        // given
        List<ChoiceRequestDto> choiceList = new ArrayList<>();
        List<QuestionRequestDto> questionList = new ArrayList<>();

        // 설문 문항 1 내부 선지
        ChoiceRequestDto choiceRequest1InQ1 = ChoiceRequestDto.builder()
                .choiceName("선지 1")
                .build();
        ChoiceRequestDto choiceRequest2InQ1 = ChoiceRequestDto.builder()
                .choiceName("선지 2")
                .build();
        choiceList.add(choiceRequest1InQ1);
        choiceList.add(choiceRequest2InQ1);

        QuestionRequestDto questionRequest2 = new QuestionRequestDto("객관식 문항", 2, choiceList);
        QuestionRequestDto questionRequest0 = new QuestionRequestDto("주관식 문항", 0);
        questionList.add(questionRequest2);
        questionList.add(questionRequest0);

        SurveyRequestDto surveyRequest = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트")
                .description("설문 설명 테스트")
                .questionRequest(questionList)
                .build();

        // when
        surveyService.createSurvey(servletRequest, surveyRequest);

        // then
        SurveyDocument createdSurvey = surveyRepository.findAll().get(0)
                .getSurveyDocumentList().get(0);

        assertEquals("설문 제목 테스트", createdSurvey.getTitle());
        assertEquals("설문 설명 테스트", createdSurvey.getDescription());
        assertEquals(0, createdSurvey.getType());

        for(QuestionDocument questionDocument : createdSurvey.getQuestionDocumentList()) {
            switch (questionDocument.getQuestionType()) {
                case 2: // 객관식
                    assertEquals("객관식 문항", questionDocument.getTitle());
                    int i = 1;
                    for(Choice choice : questionDocument.getChoiceList()) {
                        assertEquals("선지 " + i, choice.getTitle());
                        i++;
                    }
                    break;
                case 0: // 주관식
                    assertEquals("주관식 문항", questionDocument.getTitle());
                    break;
            }
        }
    }

    @Test @DisplayName("주관식 설문 저장")
    void test2() throws Exception {

    }

}
