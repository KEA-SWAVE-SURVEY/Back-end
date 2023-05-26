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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.util.List;

//@RestController
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

    // 분석 문항
    @GetMapping(value = "/api/research/survey/load/{id}")
    public SurveyDetailDto readSurvey(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, id);
    }

    // 설문 응답 조회
    @GetMapping(value = "/api/response/{id}")
    public SurveyDetailDto readResponse(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readCountChoice(request, id);
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

    // 설문 삭제
    @PutMapping(value = "/api/survey/management/delete/{surveyId}")
    public ResponseEntity deleteSurveyDocument(HttpServletRequest request, @PathVariable Long surveyId) {
        return surveyService.deleteSurvey(request, surveyId);
    }

    // 설문 수정
    @PutMapping(value = "/api/survey/management/update/{surveyId}")
    public ResponseEntity updateSurveyDocument(SurveyRequestDto surveyRequest, @PathVariable Long surveyId) {
        return surveyService.updateSurvey(surveyRequest, surveyId);
    }
}
