package com.example.demo.survey.controller;

import com.example.demo.survey.domain.SurveyAnalyze;
import com.example.demo.survey.domain.SurveyAnswer;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.SurveyResponseDto;
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

    // 설문 참여 응답 생성 -> 분석 생성
    @PostMapping(value = "/api/create-response/{surveyId}")
    public String createResponseAndSaveAnalyze(@RequestHeader HttpServletRequest request, @RequestBody SurveyResponseDto surveyForm, @PathVariable Long surveyId) throws InvalidTokenException {
        // 설문 응답 저장
        surveyService.createSurveyAnswer(request, surveyForm);
        // 설문 분석 -> 저장 (python)
        surveyService.giveDocumentIdtoPython(surveyId);
        return "Success";
    }

    // 분석 문항
    @GetMapping(value = "/api/research/1/{surveyId}")
    public SurveyDocument readSurvey(@RequestHeader HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, surveyId);
    }

    // 분석 응답
    @GetMapping(value = "/api/research/2/{surveyId}")
    public List<SurveyAnswer> readResponse(@RequestHeader HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyAnswerList(request, surveyId);
    }

    // todo:분석 관리 (설문 수정)
    @PostMapping(value = "/api/research/3/{surveyId}")
    public List<SurveyAnswer> manageSurvey(@RequestHeader HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return null;
    }

    // 분석 상세 분석
    // todo: python 에서 저장한 상세 분석 리스트 db 에서 가져오기
    @GetMapping(value = "/api/research/4/{surveyId}/")
    public SurveyAnalyze readDetailAnalyze(@RequestHeader HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyDetailAnalyze(request, surveyId);
    }
}
