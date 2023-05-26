package com.example.demo.survey.repository.wordCloud;

import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.WordCloud;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface WordCloudRepository extends JpaRepository<WordCloud, Long>, WordCloudRepositoryCustom {

    void deleteAllByQuestionDocument(QuestionDocument questionDocument);
}
