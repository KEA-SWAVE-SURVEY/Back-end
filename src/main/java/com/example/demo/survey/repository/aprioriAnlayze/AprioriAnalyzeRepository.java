package com.example.demo.survey.repository.aprioriAnlayze;

import com.example.demo.survey.domain.AprioriAnalyze;
import com.example.demo.survey.domain.SurveyAnalyze;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AprioriAnalyzeRepository  extends JpaRepository<AprioriAnalyze, Long>, AprioriAnalyzeRepositoryCustom {

}