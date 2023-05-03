package com.example.demo.survey.controller;

import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SurveyDocumentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    SurveyService surveyService;
    @Autowired
    SurveyRepository surveyRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void clean() throws Exception {
        surveyRepository.deleteAll();
    }

    @Test @DisplayName("Survey 생성시 DB 저장")
    void test1() throws Exception {
        // given
        SurveyRequestDto surveyRequest = SurveyRequestDto.builder()
                .title("설문 제목 테스트")
                .description("설문 설명 테스트")
                .type(0)
                .build();

        QuestionRequestDto questionRequest1 = new QuestionRequestDto("주관식 문제 1",0 ,new ArrayList<>());
        QuestionRequestDto questionRequest2 = new QuestionRequestDto("주관식 문제 2", 0, new ArrayList<>());

        List<QuestionRequestDto> setQuestions = new ArrayList<>();
        setQuestions.add(questionRequest1);
        setQuestions.add(questionRequest2);


        surveyRequest.setQuestionRequest(setQuestions);

        // ObjectMapper : json 으로 convert
        String surveyJson = objectMapper.writeValueAsString(surveyRequest);

        // expected
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyJson))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test @DisplayName("")
    void test2() {

    }
}
