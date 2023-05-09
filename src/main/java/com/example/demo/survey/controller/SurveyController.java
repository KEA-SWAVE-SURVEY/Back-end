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

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(value = "/api/create")
    public String create(HttpServletRequest request, @RequestBody SurveyRequestDto surveyForm) throws InvalidTokenException {
        surveyService.createSurvey(request, surveyForm);

        return "Success";
    }

    @PostMapping(value = "/api/survey-list")
    public Page<SurveyDocument> readList(HttpServletRequest request, @RequestBody PageRequestDto pageRequest) throws Exception {
        return surveyService.readSurveyList(request, pageRequest);
    }

    @GetMapping(value = "/api/survey-list/{id}")
    public SurveyDetailDto readDetail(HttpServletRequest request, @PathVariable Long id) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, id);
    }

    // 설문 참여
    @GetMapping(value = "/api/survey-participate/{surveyId}")
    public SurveyDetailDto participateSurvey(@PathVariable Long surveyId) {
        return surveyService.getParticipantSurvey(surveyId);
    }

    // 설문 참여 응답 생성 -> 분석 생성
    @PostMapping(value = "/api/create-response/{surveyId}")
    public String createResponseAndSaveAnalyze(@RequestBody SurveyResponseDto surveyForm, @PathVariable Long surveyId) throws InvalidTokenException {
        // 설문 응답 저장
        surveyService.createSurveyAnswer(surveyId, surveyForm);
        // 설문 분석 -> 저장 (python)
        surveyService.giveDocumentIdtoPython(surveyId);
        return "Success";
    }

    // 분석 문항
    @GetMapping(value = "/api/research/1/{surveyId}")
    public SurveyDetailDto readSurvey(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyDetail(request, surveyId);
    }

    // 분석 응답
    @GetMapping(value = "/api/research/2/{surveyId}")
    public SurveyDetailDto readResponse(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readCountChoice(request, surveyId);
    }

    // todo:분석 관리 (설문 수정)
    @GetMapping(value = "/api/research/3/{surveyId}")
    public SurveyManageDto getManageSurvey(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyMange(request, surveyId);
    }

    // todo:분석 관리 (설문 수정)
    @PostMapping(value = "/api/research/3/{surveyId}")
    public String setManageSurvey(HttpServletRequest request,@RequestBody SurveyManageDto surveyForm, @PathVariable Long surveyId) throws InvalidTokenException {
        surveyService.setSurveyMange(request, surveyId, surveyForm);
        return "success";
    }

    // 분석 상세 분석
    // todo: python 에서 저장한 상세 분석 리스트 db 에서 가져오기
    @GetMapping(value = "/api/research/4/{surveyId}/")
    public SurveyAnalyzeDto readDetailAnalyze(HttpServletRequest request, @PathVariable Long surveyId) throws InvalidTokenException {
        return surveyService.readSurveyDetailAnalyze(request, surveyId);
    }
}
