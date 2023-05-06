package com.example.demo.survey.repository.surveyDocument;

import com.example.demo.survey.domain.SurveyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyDocumentRepository extends JpaRepository<SurveyDocument, Long>,  SurveyDocumentRepositoryCustom{
    SurveyDocument findByTitle(String title);
}
