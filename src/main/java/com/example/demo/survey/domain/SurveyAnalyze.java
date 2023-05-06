package com.example.demo.survey.domain;

import com.example.demo.survey.analyze.QuestionAnalyzeDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
public class SurveyAnalyze {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_analyze_id")
    private Long id;

    @OneToMany(mappedBy = "surveyAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @Column(name = "연관분석")
    private List<QuestionAnalyze> questionAnalyzeList;

    @ManyToOne
    @JoinColumn(name = "survey_document_id")
    private SurveyDocument surveyDocumentId;

    @Builder
    public SurveyAnalyze(List<QuestionAnalyze> questionAnalyzeList, SurveyDocument surveyDocumentId) {
        this.questionAnalyzeList = questionAnalyzeList;
        this.surveyDocumentId = surveyDocumentId;
    }
}
