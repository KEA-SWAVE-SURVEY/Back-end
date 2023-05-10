package com.example.demo.survey.service;

import com.example.demo.survey.domain.Choice;
import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.survey.SurveyRepositoryCustom;
import com.example.demo.survey.repository.survey.SurveyRepositoryImpl;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import com.example.demo.util.OAuth.JwtProperties;
import com.example.demo.util.page.PageRequest;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.survey.domain.QSurveyDocument.surveyDocument;
import static org.junit.jupiter.api.Assertions.*;
import static com.example.demo.util.page.PageRequest.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SurveyDocumentServiceTest {
    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    SurveyDocumentRepository surveyDocumentRepository;
    @Autowired
    QuestionDocumentRepository questionDocumentRepository;
    @Autowired
    ChoiceRepository choiceRepository;
    @Autowired
    UserService2 userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    SurveyRepositoryImpl surveyRepositoryImpl;

    MockHttpServletRequest servletRequest;

    @Mock
    JPAQueryFactory jpaQueryFactory;

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
    void surveyTest1() throws Exception{
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

    @Test @DisplayName("설문 리스트 query dsl mock 쿼리 조회")
    @Transactional
    void surveyTest2() throws Exception {
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

        SurveyRequestDto surveyRequest1 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트1")
                .description("설문 설명 테스트1")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest2 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트2")
                .description("설문 설명 테스트2")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest3 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트3")
                .description("설문 설명 테스트3")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest4 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트4")
                .description("설문 설명 테스트4")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest5 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트5")
                .description("설문 설명 테스트5")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest6 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트6")
                .description("설문 설명 테스트6")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest7 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트7")
                .description("설문 설명 테스트7")
                .questionRequest(questionList)
                .build();
        SurveyRequestDto surveyRequest8 = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트8")
                .description("설문 설명 테스트8")
                .questionRequest(questionList)
                .build();

        // when
        surveyService.createSurvey(servletRequest, surveyRequest1);
        surveyService.createSurvey(servletRequest, surveyRequest2);
        surveyService.createSurvey(servletRequest, surveyRequest3);
        surveyService.createSurvey(servletRequest, surveyRequest4);
        surveyService.createSurvey(servletRequest, surveyRequest5);
        surveyService.createSurvey(servletRequest, surveyRequest6);
        surveyService.createSurvey(servletRequest, surveyRequest7);
        surveyService.createSurvey(servletRequest, surveyRequest8);


        // when
        PageRequestDto pageRequestDto = new PageRequestDto("list", 1, "title", "ascending");
        Page<SurveyDocument> pageImpl = surveyService.readSurveyList(servletRequest, pageRequestDto);

        // then
        System.out.println(pageImpl.getNumberOfElements());
        assertEquals(5, pageImpl.getContent().size());
        assertEquals(1, pageImpl.getTotalPages());
        assertEquals(0, pageImpl.getNumber());
        int i = 1;
        for(SurveyDocument surveyInPage : pageImpl.getContent()) {
            assertEquals(surveyInPage.getTitle(), surveyDocumentRepository.findByTitle("설문 제목 테스트" + i).getTitle());
            i++;
        }
    }

    @Test @DisplayName("설문 리스트 페이징 처리 조회")
    @Transactional
    void surveyTest3() throws Exception {

    }
}
