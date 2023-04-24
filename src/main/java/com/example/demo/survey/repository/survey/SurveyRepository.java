package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {

}