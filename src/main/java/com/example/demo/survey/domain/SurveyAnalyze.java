package com.example.demo.survey.domain;

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

    @OneToMany(mappedBy = "surveyAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @Column(name = "연관분석")
    private List<QuestionAnalyze> questionAnalyzeList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_document_id")
    private SurveyDocument surveyDocument;

    @Builder
    public SurveyAnalyze(List<QuestionAnalyze> questionAnalyzeList, SurveyDocument surveyDocument) {
        this.questionAnalyzeList = questionAnalyzeList;
        this.surveyDocument = surveyDocument;
    }
}
