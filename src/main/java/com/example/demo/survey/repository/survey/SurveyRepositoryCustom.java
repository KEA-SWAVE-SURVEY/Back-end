package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyRepositoryCustom {
    Page<SurveyDocument> surveyDocumentPaging(User user,Pageable pageable);
    SurveyDocument surveyDocumentDetail(User userRequest, SurveyDocument surveyDocumentRequest);
}