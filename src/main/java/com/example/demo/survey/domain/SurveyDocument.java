package com.example.demo.survey.domain;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SurveyDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_document_id")
    private Long id;
    @Column(name = "survey_title")
    private String title;
    @Column(name = "survey_type")
    private int type;
    @Column(name = "survey_description")
    private String description;
    @Column(name = "accept_response")
    private boolean acceptResponse;
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "survey_start_date")
    private String startDate;
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "survey_deadline")
    private String deadline;
    @Column(name = "url")
    private String url;

    @OneToMany(mappedBy = "surveyDocumentId", fetch = FetchType.LAZY)
    @Column(name = "content")
    private List<QuestionDocument> questionDocumentList;

    @OneToMany(mappedBy = "surveyDocumentId", fetch = FetchType.LAZY)
    @Column(name = "analyze")
    private List<SurveyAnalyze> surveyAnalyzeList;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SurveyDocument(Survey survey, String title, int type, String description, List<QuestionDocument> questionDocumentList, List<SurveyAnalyze> surveyAnalyzeList) {
        this.survey = survey;
        this.title = title;
        this.type = type;
        this.description = description;
        this.questionDocumentList = questionDocumentList;
        this.surveyAnalyzeList = surveyAnalyzeList;
    }

    // 문항 list 에 넣어주기
    public void setQuestion(QuestionDocument questionDocument) {
        this.questionDocumentList.add(questionDocument);
    }
    // 문항 analyze 에 넣어주기
    public void setAnalyze(SurveyAnalyze surveyAnalyze) {
        this.surveyAnalyzeList.add(surveyAnalyze);
    }
}
