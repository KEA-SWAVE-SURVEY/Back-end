package com.example.demo.survey.repository.surveyDocument;

import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyDocumentRepositoryCustom {
    Page<SurveyDocument> findSurveyDocumentList(SurveyDocument surveyRequest, Pageable pageable);
    Long updateAnswerCount(Long idx);
}
