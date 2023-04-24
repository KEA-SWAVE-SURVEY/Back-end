package com.example.demo.survey.repository.surveyResponse;

import com.example.demo.survey.domain.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long>, SurveyResponseRepositoryCustom {
}
