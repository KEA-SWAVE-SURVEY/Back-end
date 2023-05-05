package com.example.demo.survey.domain;

import com.example.demo.survey.analyze.QuestionAnalyzeDto;
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

//    @OneToMany(mappedBy = "surveyAnalyzeId", fetch = FetchType.LAZY)
//    @Column(name = "content")
//    private List<QuestionAnalyze> questionAnalyzeList;

//    @OneToMany(mappedBy = "surveyDocumentId", fetch = FetchType.LAZY)
//    @Column(name = "content")
//    private List<QuestionDocument> questionDocumentList;

    @ManyToOne
    @JoinColumn(name = "survey_document_id")
    private SurveyDocument surveyDocumentId;
}
