package com.example.demo.survey.controller;

import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.SurveyAnalyzeDto;
import com.example.demo.survey.response.SurveyDetailDto;
import com.example.demo.survey.response.SurveyManageDto;
import com.example.demo.survey.response.SurveyResponseDto;
import com.example.demo.survey.service.SurveyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(value = "/api/create")
    public String create(HttpServletRequest request, @RequestBody SurveyRequestDto surveyForm) throws InvalidTokenException, UnknownHostException {
        surveyService.createSurvey(request, surveyForm);

        return "Success";
    }

    // grid 로 조회
    @PostMapping(value = "/api/survey-list-grid")
    public List<SurveyDocument> readListGrid(HttpServletRequest request, @RequestBody PageRequestDto pageRequest) throws Exception {
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

    // 설문 참여
    @GetMapping(value = "/api/survey/load")
    public SurveyDetailDto participateSurvey(@RequestBody Long surveyId) {
        return surveyService.getParticipantSurvey(surveyId);
    }

    // 설문 응답 저장
    @PostMapping(value = "/api/response/create")
    public String createResponse(@RequestBody SurveyResponseDto surveyForm) throws InvalidTokenException {
        // 설문 응답 저장
        surveyService.createSurveyAnswer(surveyForm);
        return "Success";
    }

    // 분석 문항
    @GetMapping(value = "/api/research/survey/load")
    public SurveyDetailDto readSurvey(HttpServletRequest request, @RequestBody Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, surveyId);
    }

    // 설문 응답 조회
    @GetMapping(value = "/api/response")
    public SurveyDetailDto readResponse(HttpServletRequest request, @RequestBody Long surveyId) throws InvalidTokenException {
        return surveyService.readCountChoice(request, surveyId);
    }

    // todo:설문 관리 수정
    @GetMapping(value = "/api/survey/management/{surveyId}")
    public SurveyManageDto getManageSurvey(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyMange(request, surveyId);
    }

    // todo:설문 관리 조회
    @PostMapping(value = "/api/survey/management/update/{surveyId}")
    public String setManageSurvey(HttpServletRequest request,@RequestBody SurveyManageDto surveyForm, @PathVariable Long surveyId) throws InvalidTokenException {
        surveyService.setSurveyMange(request, surveyId, surveyForm);
        return "success";
    }

    // 설문 분석 시작
    @PostMapping(value = "/api/research/analyze/create")
    public String saveAnalyze(@RequestBody String surveyId) throws InvalidTokenException {
        // 설문 분석 -> 저장 (python)
        surveyService.giveDocumentIdtoPython(surveyId);
        return "Success";
    }

    // 설문 상세 분석 조회
    @GetMapping(value = "/api/research/analyze")
    public SurveyAnalyzeDto readDetailAnalyze(HttpServletRequest request, @RequestBody Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyDetailAnalyze(request, surveyId);
    }
}
