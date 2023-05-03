package com.example.demo.survey.controller;

import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.SurveyResponseDto;
import com.example.demo.survey.service.SurveyService;
import com.example.demo.util.paging.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(value = "/api/create")
    public String create(HttpServletRequest request, @RequestBody SurveyRequestDto surveyForm) throws Exception {
        surveyService.createSurvey(request, surveyForm);

        return "Success";
    }

    @PostMapping(value = "/api/survey-list")
    public Page<SurveyDocument> readList(HttpServletRequest request, @RequestBody PageRequest pageRequest) throws Exception {
        return surveyService.readSurveyList(request, pageRequest);
    }

    @GetMapping(value = "/api/survey-list/{id}")
    public SurveyDocument readDetail(HttpServletRequest request, @PathVariable Long id) throws Exception {
        return surveyService.readSurveyDetail(request, id);
    }

    @PostMapping(value = "/api/create-response")
    public String createResponse(HttpServletRequest request, @RequestBody SurveyResponseDto surveyForm) throws InvalidTokenException {
        surveyService.createSurveyAnswer(request, surveyForm);

        return "Success";
    }
}
