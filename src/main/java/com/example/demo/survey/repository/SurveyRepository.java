package com.example.demo.survey.repository;

import com.example.demo.survey.domain.SurveyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<SurveyDocument, Long> {

}