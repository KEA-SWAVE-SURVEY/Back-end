package com.example.demo.survey.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SurveyAnalyze {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_analyze_id")
    private Long id;

    @Column(name = "survey_document_id")
    private Long surveyDocumentId;

    @OneToMany(mappedBy = "surveyAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @Column(name = "연관분석")
    private List<QuestionAnalyze> questionAnalyzeList;


    @Builder
    public SurveyAnalyze(List<QuestionAnalyze> questionAnalyzeList, Long surveyDocumentId) {
        this.questionAnalyzeList = questionAnalyzeList;
        this.surveyDocumentId = surveyDocumentId;
    }
}
