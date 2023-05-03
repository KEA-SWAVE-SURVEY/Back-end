package com.example.demo.sruvey.service;

import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

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
    SurveyDocumentRepository surveyDocumentRepository;
    @Autowired
    QuestionDocumentRepository questionDocumentRepository;
    @Autowired
    ChoiceRepository choiceRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService2 userService2;

    MockHttpServletRequest mockHttpServletRequest;

    @BeforeEach
    void clean() {
        // 유저 등록, 로그인
        userService2.SaveUserAndGetToken()
        // 임시 토큰 항시 생성
        mockHttpServletRequest = new MockHttpServletRequest();
        userService2.getAccessToken()
        surveyRepository.deleteAll();
    }

//    @Test @DisplayName("설문 레포지토리에 저장")
//    void test1() throws Exception{
//        // given
//        SurveyRequestDto surveyRequest = SurveyRequestDto.builder()
//                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
//                .title("설문 제목 테스트")
//                .description("설문 설명 테스트")
//                .build();
//
//        // QuestionRequest type : 0 주관식 1 찬부식 2 객관식
//        // 설문 문항 1
//        List<ChoiceRequestDto> choiceList1 = new ArrayList<>();
//        QuestionRequestDto questionRequest1 = new QuestionRequestDto("객관식 문제 1", 2, choiceList1);
//
//        // 설문 문항 1 내부 선지
//        ChoiceRequestDto choiceRequest1InQ1 = ChoiceRequestDto.builder()
//                .choiceName("Q1_1")
//                .build();
//        ChoiceRequestDto choiceRequest2InQ1 = ChoiceRequestDto.builder()
//                .choiceName("Q1_2")
//                .build();
//
//        // 내부 선지 Repository 저장
//        choiceRepository.save();
//
//        // 설문 문항 1 에 선지 저장
//        choiceList1.add(choiceRequest1InQ1);
//        choiceList1.add(choiceRequest2InQ1);
//        questionRequest1.setChoiceList(choiceList1);
//
//        // 설문 문항 2
//        List<ChoiceRequestDto> choiceList2 = new ArrayList<>();
//        QuestionRequestDto questionRequest2 = new QuestionRequestDto("객관식 문제 2", 2, choiceList2);
//
//        // 설문 문항 2 내부 선지
//        ChoiceRequestDto choiceRequest1InQ2 = ChoiceRequestDto.builder()
//                .choiceName("Q2_1")
//                .build();
//        ChoiceRequestDto choiceRequest2InQ2 = ChoiceRequestDto.builder()
//                .choiceName("Q2_2")
//                .build();
//        //
//        // 설문 문항 2 에 선지 저장
//        choiceList2.add(choiceRequest1InQ2);
//        choiceList2.add(choiceRequest2InQ2);
//        questionRequest2.setChoiceList(choiceList2);
//
//        // 설문에 문항 1,2 저장
//        List<QuestionRequestDto> setQuestions = new ArrayList<>();
//        setQuestions.add(questionRequest1);
//        setQuestions.add(questionRequest2);
//
//        surveyRequest.setQuestionRequest(setQuestions);
//
//        // when
//        surveyService.createSurvey(request, surveyRequest);
//
//        // then
//        SurveyDocument surveyDocument = surveyRepository.findAll().get(0);
//        assertEquals("설문 제목 테스트", surveyDocument.getTitle());
//        assertEquals("설문 설명 테스트", surveyDocument.getDescription());
//        assertEquals(0, surveyDocument.getType());
//        int i = 0;
//        for (QuestionDocument questionDocument : surveyDocument.getQuestionDocumentList()) {
//            assertEquals("객관식 문제 " + i, questionDocument.getTitle());
//            assertEquals("Q"+i+"_1", questionDocument.getChoiceList().get(0));
//            i++;
//        }
//    }

    @Test @DisplayName("설문 레포지토리에 저장")
    void test2() throws Exception {
        // given
        // choice 만들기
        ChoiceRequestDto choiceRequest1 = ChoiceRequestDto.builder()
                .choiceName("선지 1")
                .build();
        ChoiceRequestDto choiceRequest2 = ChoiceRequestDto.builder()
                .choiceName("선지 2")
                .build();

        List<ChoiceRequestDto> choiceList = new ArrayList<>();
        choiceList.add(choiceRequest1);
        choiceList.add(choiceRequest2);

        // 문항 만들기
        QuestionRequestDto questionRequest1 = QuestionRequestDto.builder()
                .title("객관식 문항 1")
                .choiceList(choiceList)
                .type(3)
                .build();

        QuestionRequestDto questionRequestDto2 = QuestionRequestDto.builder()
                .title("주관식 문항 2")
                .type(2)
                .build();

        List<QuestionRequestDto> questionList = new ArrayList<>();
        questionList.add(questionRequest1);
        questionList.add(questionRequestDto2);

        // 설문 만들기
        SurveyRequestDto surveyRequest = SurveyRequestDto.builder()
                .title("설문 1")
                .description("설문 TEST 입니다")
                .type(1)
                .questionRequest(questionList)
                .build();

        surveyService.createSurvey(surveyRequest);
        // when

        // then

    }

}
