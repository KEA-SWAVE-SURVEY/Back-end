package com.example.demo.survey.repository.chiAnlayze;

import com.example.demo.survey.domain.ChiAnalyze;
import com.example.demo.survey.domain.SurveyAnalyze;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiAnalyzeRepository  extends JpaRepository<ChiAnalyze, Long>, ChiAnalyzeRepositoryCustom {

}