package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SurveyAnalyze {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_analyze_id")
    private Long id;
    @Column(name = "analyze_result1")
    private String analyzeResult1;
    @Column(name = "analyze_result2")
    private String analyzeResult2;
    @Column(name = "analyze_result3")
    private String analyzeResult3;

//    연결할 필요가? document 에 연결
//    @ManyToOne
//    @JoinColumn(name = "survey_document_id")
//    그냥 id 로만 받을지 -> SurveyAnswer 구분 처럼
    private Long surveyDocumentId;

    @Builder
    public SurveyAnalyze(String analyzeResult1, String analyzeResult2, String analyzeResult3, Long surveyDocumentId) {
        this.analyzeResult1 = analyzeResult1;
        this.analyzeResult2 = analyzeResult2;
        this.analyzeResult3 = analyzeResult3;
        this.surveyDocumentId = surveyDocumentId;
    }
}
