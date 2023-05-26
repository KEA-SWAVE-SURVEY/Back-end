package com.example.demo.survey.controller;

import com.example.demo.survey.domain.QuestionAnswer;
import com.example.demo.survey.response.SurveyDetailDto;
import com.example.demo.survey.response.SurveyResponseDto;
import com.example.demo.survey.service.SurveyAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyAnswerController {

    private final SurveyAnswerService surveyService;

    // 설문 참여
    @GetMapping(value = "/api/survey/load/{id}")
    public SurveyDetailDto participateSurvey(@PathVariable Long id) {
        return surveyService.getParticipantSurvey(id);
    }

    // 설문 응답 저장
    @PostMapping(value = "/api/response/create")
    public String createResponse(@RequestBody SurveyResponseDto surveyForm) {
        // 설문 응답 저장
        surveyService.createSurveyAnswer(surveyForm);
        return "Success";
    }

    // 설문 응답 조회
    @GetMapping(value = "/api/response/{id}")
    public SurveyDetailDto readResponse(@PathVariable Long id){
        return surveyService.getParticipantSurvey(id);
    }

    // getQuestionAnswer
    @GetMapping(value = "/api/question/list/{id}")
    public List<QuestionAnswer> getQuestionAnswers(@PathVariable Long id){
        return surveyService.getQuestionAnswers(id);
    }

    // getQuestionAnswerByCheckAnswerId
    @GetMapping(value = "/api/getQuestionAnswerByCheckAnswerId/{id}")
    public List<QuestionAnswer> getQuestionAnswerByCheckAnswerId(@PathVariable Long id){
        return surveyService.getQuestionAnswerByCheckAnswerId(id);
    }
}
