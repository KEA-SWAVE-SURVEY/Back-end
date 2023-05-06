package com.example.demo.survey.repository.choiceAnalyze;

import com.example.demo.survey.domain.ChoiceAnalyze;
import com.example.demo.survey.domain.SurveyAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChoiceAnalyzeRepository  extends JpaRepository<ChoiceAnalyze, Long>, ChoiceAnalyzeRepositoryCustom {
}