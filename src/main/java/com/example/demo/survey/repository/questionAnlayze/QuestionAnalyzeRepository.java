package com.example.demo.survey.repository.questionAnlayze;

import com.example.demo.survey.domain.QuestionAnalyze;
import com.example.demo.survey.domain.SurveyAnalyze;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnalyzeRepository  extends JpaRepository<QuestionAnalyze, Long>, QuestionAnalyzeRepositoryCustom {

    @Transactional
    void deleteAllBySurveyAnalyzeId(SurveyAnalyze id);
}