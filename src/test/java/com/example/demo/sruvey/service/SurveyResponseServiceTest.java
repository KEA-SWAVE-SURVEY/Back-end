package com.example.demo.sruvey.service;

import com.example.demo.survey.domain.QuestionAnswer;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyAnswer;
import com.example.demo.survey.repository.questionAnswer.QuestionAnswerRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.demo.survey.response.QuestionResponseDto;
import com.example.demo.survey.response.SurveyResponseDto;
import com.example.demo.survey.service.SurveyService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SurveyResponseServiceTest {
    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    QuestionAnswerRepository questionAnswerRepository;
    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;

    @BeforeEach
    void clean() {
        surveyRepository.deleteAll();
    }

    // todo : request 부분 완료시 작성
    @Test @DisplayName("설문 응답 레포지토리에 저장")
    void test1() throws Exception {
        // given
        HttpServletRequest request = null;

        SurveyResponseDto surveyResponse = SurveyResponseDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트")
                .description("설문 설명 테스트")
                .build();

        // QuestionResponse type : 0 주관식 1 찬부식 2 객관식
        // 설문 응답 1
        QuestionResponseDto questionResponse1 = new QuestionResponseDto("객관식 문제 1", 2, "2");

        // 설문 응답 2
        QuestionResponseDto questionResponse2 = new QuestionResponseDto("주관식 문제 2", 0, "주관식 답변");

        // 설문 응답 3
        QuestionResponseDto questionResponse3 = new QuestionResponseDto("찬부식 문제 3", 1, "0");

        // 설문에 문항 1,2 저장
        List<QuestionResponseDto> setQuestions = new ArrayList<>();
        setQuestions.add(questionResponse1);
        setQuestions.add(questionResponse2);
        setQuestions.add(questionResponse3);

        surveyResponse.setQuestionResponse(setQuestions);

        // when
        surveyService.createSurveyAnswer(request, surveyResponse);

        // then
        SurveyAnswer surveyAnswer = surveyAnswerRepository.findAll().get(0);
        assertEquals("설문 제목 테스트", surveyAnswer.getTitle());
        assertEquals("설문 설명 테스트", surveyAnswer.getDescription());
        assertEquals(0, surveyAnswer.getType());
        for (QuestionAnswer questionAnswer : surveyAnswer.getQuestionanswersList()) {
            assertEquals(questionAnswer.getTitle(), questionAnswer.getTitle());
        }
    }
}
