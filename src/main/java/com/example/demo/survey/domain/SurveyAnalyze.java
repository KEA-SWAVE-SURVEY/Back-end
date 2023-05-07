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

    @OneToMany(mappedBy = "surveyAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore //순환참조 방지
    @Column(name = "연관분석")
    private List<QuestionAnalyze> questionAnalyzeList;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "survey_document_id")
    private SurveyDocument surveyDocument;

    @Builder
    public SurveyAnalyze(List<QuestionAnalyze> questionAnalyzeList, SurveyDocument surveyDocument) {
        this.questionAnalyzeList = questionAnalyzeList;
        this.surveyDocument = surveyDocument;
    }
}
