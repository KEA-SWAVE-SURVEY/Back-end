package com.example.demo.survey.repository.surveyAnswer;

import com.example.demo.survey.domain.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long>, SurveyAnswerRepositoryCustom {
}
