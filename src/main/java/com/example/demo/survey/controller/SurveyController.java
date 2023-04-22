package com.example.demo.survey.controller;

import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.repository.SurveyRepository;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.aot.generate.AccessControl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(value = "/api/create")
    public String create(@RequestBody SurveyRequestDto surveyForm) {
        surveyService.createSurvey(surveyForm);

        return "Success";
    }

    @GetMapping(value = "/api/survey-list")
    public List<Survey> readList() {
        return surveyService.readSurveyList();
    }

    @GetMapping(value = "/api/survey-list/{id}")
    public Survey readDetail(@PathVariable Long id) {
        return surveyService.readSurveyDetail(id);
    }
}
