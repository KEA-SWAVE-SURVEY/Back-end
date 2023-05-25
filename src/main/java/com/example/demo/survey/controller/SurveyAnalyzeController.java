package com.example.demo.survey.controller;

import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.survey.response.SurveyAnalyzeDto;
import com.example.demo.survey.response.SurveyDetailDto;
import com.example.demo.survey.response.SurveyManageDto;
import com.example.demo.survey.response.SurveyResponseDto;
import com.example.demo.survey.service.SurveyAnalyzeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyAnalyzeController {

    private final SurveyAnalyzeService surveyService;

//    // 분석 문항
//    @GetMapping(value = "/api/research/survey/load/{id}")
//    public SurveyDetailDto readSurvey(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
//        return surveyService.readSurveyDetail(request, id);
//    }

//    // 설문 응답 조회
//    @GetMapping(value = "/api/response/{id}")
//    public SurveyDetailDto readResponse(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
//        return surveyService.readCountChoice(request, id);
//    }
//
//    // todo:설문 관리 수정
//    @GetMapping(value = "/api/survey/management/{surveyId}")
//    public SurveyManageDto getManageSurvey(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
//        return surveyService.readSurveyMange(request, surveyId);
//    }
//
//    // todo:설문 관리 조회
//    @PostMapping(value = "/api/survey/management/update/{surveyId}")
//    public String setManageSurvey(HttpServletRequest request,@RequestBody SurveyManageDto surveyForm, @PathVariable Long surveyId) throws InvalidTokenException {
//        surveyService.setSurveyMange(request, surveyId, surveyForm);
//        return "success";
//    }

    // 설문 분석 시작
    @PostMapping(value = "/api/research/analyze/create")
    public String saveAnalyze(@RequestBody String surveyId) {
        // 설문 분석 -> 저장 (python)
        surveyService.analyze(surveyId);
        // 주관식 분석 -> 저장
        surveyService.wordCloud(surveyId);
        return "Success";
    }

    // 설문 상세 분석 조회
    @GetMapping(value = "/api/research/analyze/{id}")
    public SurveyAnalyzeDto readDetailAnalyze(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetailAnalyze(request, id);
    }
}
