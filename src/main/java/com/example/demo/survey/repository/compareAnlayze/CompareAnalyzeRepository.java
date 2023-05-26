package com.example.demo.survey.repository.compareAnlayze;

import com.example.demo.survey.domain.CompareAnalyze;
import com.example.demo.survey.domain.SurveyAnalyze;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompareAnalyzeRepository  extends JpaRepository<CompareAnalyze, Long>, CompareAnalyzeRepositoryCustom {

}