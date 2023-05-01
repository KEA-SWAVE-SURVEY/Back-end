package com.example.demo.survey.repository.surveyAnalyze;

import com.example.demo.survey.domain.SurveyAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyAnalyzeRepository extends JpaRepository<SurveyAnalyze, Long>, SurveyAnalyzeRepositoryCustom {
    SurveyAnalyze findBySurveyDocumentId(Long surveyDocumentId);
}