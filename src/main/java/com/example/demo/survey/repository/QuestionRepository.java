package com.example.demo.survey.repository;

import com.example.demo.survey.domain.QuestionDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionDocument, Long> {
}
