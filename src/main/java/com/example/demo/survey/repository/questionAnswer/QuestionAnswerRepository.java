package com.example.demo.survey.repository.questionAnswer;

import com.example.demo.survey.domain.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long>, QuestionAnswerRepositoryCustom {
    List<QuestionAnswer> findQuestionAnswersBySurveyDocumentId(Long surveyDocumentId);
}
