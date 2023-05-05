package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyAnalyze;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {
    Survey findByUser(Long id);
}