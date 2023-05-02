package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
public class SurveyAnalyze {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_analyze_id")
    private Long id;
    @Column(name = "survey_document_id")
    private Long surveyDocumentId;
    @Column(name = "analyze_result1")
    private String analyzeResult1;
    @Column(name = "analyze_result2")
    private String analyzeResult2;
    @Column(name = "analyze_result3")
    private String analyzeResult3;

    @Builder
    public SurveyAnalyze(String analyzeResult1, String analyzeResult2, String analyzeResult3, Long surveyDocumentId) {
        this.analyzeResult1 = analyzeResult1;
        this.analyzeResult2 = analyzeResult2;
        this.analyzeResult3 = analyzeResult3;
        this.surveyDocumentId = surveyDocumentId;
    }
}
