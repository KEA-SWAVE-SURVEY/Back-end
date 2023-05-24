package com.example.demo.survey.repository.surveyAnswer;

import com.example.demo.survey.domain.SurveyAnalyze;
import com.example.demo.survey.domain.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long>, SurveyAnswerRepositoryCustom {
    List<SurveyAnswer> findSurveyAnswersBySurveyDocumentId(Long surveyId);
}
