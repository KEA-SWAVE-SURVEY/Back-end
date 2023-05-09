package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyRepositoryCustom {
    Page<SurveyDocument> surveyDocumentPaging(User user,Pageable pageable);
    SurveyDocument surveyDocumentDetail(User userRequest, SurveyDocument surveyDocumentRequest);

    List<SurveyDocument> getSurveyDocumentListGrid(HttpServletRequest request, PageRequestDto pageRequest);

}