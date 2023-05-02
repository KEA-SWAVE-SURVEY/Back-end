package com.example.demo.survey.repository.surveyDocument;

import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import org.springframework.data.domain.Page;

public interface SurveyDocumentRepositoryCustom {
    Page<SurveyDocument> findSurveyDocumentList();
}
