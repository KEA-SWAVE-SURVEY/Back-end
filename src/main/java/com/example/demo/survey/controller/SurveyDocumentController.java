package com.example.demo.survey.controller;

import com.example.demo.survey.domain.*;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.*;
import com.example.demo.survey.service.SurveyDocumentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyDocumentController {

    private final SurveyDocumentService surveyService;

    @PostMapping(value = "/api/create")
    public String create(HttpServletRequest request, @RequestBody SurveyRequestDto surveyForm) throws InvalidTokenException, UnknownHostException {
        surveyService.createSurvey(request, surveyForm);

        return "Success";
    }

    // grid 로 조회
    @PostMapping(value = "/api/survey-list-grid")
    public List<SurveyDocument> readListGrid(HttpServletRequest request, @RequestBody PageRequestDto pageRequest) {
        return surveyService.readSurveyListByGrid(request, pageRequest);
    }

    // list 로 조회
    @PostMapping(value = "/api/survey-list")
    public Page<SurveyDocument> readList(HttpServletRequest request, @RequestBody PageRequestDto pageRequest) throws Exception {
        return surveyService.readSurveyList(request, pageRequest);
    }

    @GetMapping(value = "/api/survey-list/{id}")
    public SurveyDetailDto readDetail(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, id);
    }

    @GetMapping(value = "/api/getSurveyDocument/{id}")
    public SurveyDocument readDetail(@PathVariable Long id) {
        return surveyService.getSurveyDocument(id);
    }

    @PostMapping(value = "/api/count/{id}")
    public String countChoice(@PathVariable Long id) {
         surveyService.countChoice(id);
         return "count success";
    }

    @GetMapping(value = "/api/getChoice/{id}")
    public Choice getChoice(@PathVariable Long id) {
        return surveyService.getChoice(id);
    }

    @GetMapping(value = "/api/getQuestion/{id}")
    public QuestionDocument getQuestion(@PathVariable Long id) {

        return surveyService.getQuestion(id);
    }

    @GetMapping(value = "/api/getQuestionByChoiceId/{id}")
    public QuestionDocument getQuestionByChoiceId(@PathVariable Long id) {
        return surveyService.getQuestionByChoiceId(id);
    }

    @PostMapping(value = "/api/setWordCloud/{id}")
    public void setWordCloud(@PathVariable Long id, @RequestBody List<WordCloudDto> wordCloudList) {
        surveyService.setWordCloud(id, wordCloudList);
    }

}
