package com.example.demo.survey.repository.questionDocument;

import com.example.demo.survey.domain.QuestionDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionDocumentRepository extends JpaRepository<QuestionDocument, Long>, QuestionDocumentRepositoryCustom {
}
