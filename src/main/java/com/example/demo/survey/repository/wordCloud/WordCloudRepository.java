package com.example.demo.survey.repository.wordCloud;

import com.example.demo.survey.domain.WordCloud;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordCloudRepository extends JpaRepository<WordCloud, Long>, WordCloudRepositoryCustom {

}
