package com.example.demo.sruvey.service;

import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.repository.ChoiceRepository;
import com.example.demo.survey.repository.QuestionRepository;
import com.example.demo.survey.repository.SurveyRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
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
public class SurveyDocumentServiceTest {
    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    ChoiceRepository choiceRepository;

    @BeforeEach
    void clean() {
        surveyRepository.deleteAll();
    }

    @Test @DisplayName("설문 레포지토리에 저장")
    void test1() throws Exception{
        // given
        SurveyDocument surveyDocument
        SurveyRequestDto surveyRequest = SurveyRequestDto.builder()
                .type(0) // 대화형 설문인지 다른 설문인지 구분하는 type
                .title("설문 제목 테스트")
                .description("설문 설명 테스트")
                .build();

        // QuestionRequest type : 0 주관식 1 찬부식 2 객관식
        // 설문 문항 1
        List<ChoiceRequestDto> choiceList1 = new ArrayList<>();
        QuestionRequestDto questionRequest1 = new QuestionRequestDto("객관식 문제 1", 2, choiceList1);

        // 설문 문항 1 내부 선지
        ChoiceRequestDto choiceRequest1InQ1 = ChoiceRequestDto.builder()
                .choiceName("Q1_1")
                .build();
        ChoiceRequestDto choiceRequest2InQ1 = ChoiceRequestDto.builder()
                .choiceName("Q1_2")
                .build();

        // 내부 선지 Repository 저장
        choiceRepository.save();

        // 설문 문항 1 에 선지 저장
        choiceList1.add(choiceRequest1InQ1);
        choiceList1.add(choiceRequest2InQ1);
        questionRequest1.setChoiceList(choiceList1);

        // 설문 문항 2
        List<ChoiceRequestDto> choiceList2 = new ArrayList<>();
        QuestionRequestDto questionRequest2 = new QuestionRequestDto("객관식 문제 2", 2, choiceList2);

        // 설문 문항 2 내부 선지
        ChoiceRequestDto choiceRequest1InQ2 = ChoiceRequestDto.builder()
                .choiceName("Q2_1")
                .build();
        ChoiceRequestDto choiceRequest2InQ2 = ChoiceRequestDto.builder()
                .choiceName("Q2_2")
                .build();
        //
        // 설문 문항 2 에 선지 저장
        choiceList2.add(choiceRequest1InQ2);
        choiceList2.add(choiceRequest2InQ2);
        questionRequest2.setChoiceList(choiceList2);

        // 설문에 문항 1,2 저장
        List<QuestionRequestDto> setQuestions = new ArrayList<>();
        setQuestions.add(questionRequest1);
        setQuestions.add(questionRequest2);

        surveyRequest.setQuestionRequest(setQuestions);

        // when
        surveyService.createSurvey(surveyRequest);

        // then
        SurveyDocument surveyDocument = surveyRepository.findAll().get(0);
        assertEquals("설문 제목 테스트", surveyDocument.getTitle());
        assertEquals("설문 설명 테스트", surveyDocument.getDescription());
        assertEquals(0, surveyDocument.getType());
        int i = 0;
        for (QuestionDocument questionDocument : surveyDocument.getQuestionDocumentList()) {
            assertEquals("객관식 문제 " + i, questionDocument.getTitle());
            assertEquals("Q"+i+"_1", questionDocument.getChoiceList().get(0));
            i++;
        }
    }

    @Test @DisplayName("주관식 설문 저장")
    void test2() throws Exception {

    }

}
