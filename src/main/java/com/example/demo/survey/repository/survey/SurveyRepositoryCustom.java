package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.SurveyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyRepositoryCustom {
    Page<SurveyDocument> surveyDocumentPaging(Pageable pageable);
}