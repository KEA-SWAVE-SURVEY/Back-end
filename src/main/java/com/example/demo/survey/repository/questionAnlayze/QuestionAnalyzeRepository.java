package com.example.demo.survey.repository.questionAnlayze;

import com.example.demo.survey.domain.QuestionAnalyze;
import com.example.demo.survey.domain.SurveyAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAnalyzeRepository  extends JpaRepository<QuestionAnalyze, Long>, QuestionAnalyzeRepositoryCustom {

    QuestionAnalyze findBySurveyAnalyzeId(Long id);
}