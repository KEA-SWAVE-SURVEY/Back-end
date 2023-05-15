package com.example.demo.survey.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
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
    private Date startDate;
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "survey_deadline")
    private Date deadline;
    @Column(name = "url")
    private String url;

    @OneToMany(mappedBy = "surveyDocumentId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore //순환참조 방지
    @Column(name = "content")
    private List<QuestionDocument> questionDocumentList;

    @OneToOne(mappedBy = "surveyDocument", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    @JsonIgnore
    private SurveyAnalyze surveyAnalyze;

    @ManyToOne
    @JsonIgnore // 순환참조 방지
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @OneToMany(mappedBy = "surveyDocument", fetch = FetchType.LAZY)
    @JsonIgnore //순환참조 방지
    @Column(name = "survey_answerList")
    private List<SurveyAnswer> surveyAnswerList;

    @Builder
    public SurveyDocument(Survey survey, String title, int type, String description, List<QuestionDocument> questionDocumentList, SurveyAnalyze surveyAnalyze) {
        this.survey = survey;
        this.title = title;
        this.type = type;
        this.description = description;
        this.questionDocumentList = questionDocumentList;
        this.surveyAnalyze = surveyAnalyze;
    }

    public void setAnswer(SurveyAnswer surveyAnswer) {
        this.surveyAnswerList.add(surveyAnswer);
    }
    // 문항 list 에 넣어주기
    public void setQuestion(QuestionDocument questionDocument) {
        this.questionDocumentList.add(questionDocument);
    }
    // 문항 analyze 에 넣어주기
    public void setAnalyze(SurveyAnalyze surveyAnalyze) {
        this.surveyAnalyze=surveyAnalyze;
    }
}
