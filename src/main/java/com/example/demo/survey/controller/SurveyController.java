package com.example.demo.survey.controller;

import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(value = "/api/create")
    public String create(@RequestHeader HttpServletRequest request, @RequestBody SurveyRequestDto surveyForm) throws InvalidTokenException {
        surveyService.createSurvey(request, surveyForm);

        return "Success";
    }

    @GetMapping(value = "/api/survey-list")
    public List<SurveyDocument> readList(@RequestHeader HttpServletRequest request) throws Exception {
        return surveyService.readSurveyList(request);
    }

    @GetMapping(value = "/api/survey-list/{id}")
    public SurveyDocument readDetail(@RequestHeader HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, id);
    }
}
