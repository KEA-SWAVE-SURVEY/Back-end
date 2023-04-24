package com.example.demo.survey.repository.questionResponse;

import com.example.demo.survey.domain.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, Long>, QuestionResponseRepositoryCustom {
}
