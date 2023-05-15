package com.example.demo.survey.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SurveyAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_answer_id")
    private Long id;
    @Column(name = "survey_type")
    private int type;
    @Column(name = "survey_title")
    private String title;
    @Column(name = "survey_description")
    private String description;

    //어떤 설문의 응답인지
    @Column(name = "survey_document_Id")
    private Long surveyDocumentId;

    @OneToMany(mappedBy = "surveyAnswerId", fetch = FetchType.LAZY)
    @JsonIgnore //순환참조 방지
    @Column(name = "content")
    private List<QuestionAnswer> questionanswersList;

    @ManyToOne
    @JsonIgnore // 순환참조 방지
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SurveyAnswer(Survey survey, String title, int type, String description, Long surveyDocumentId, List<QuestionAnswer> questionAnswerList) {
        this.survey = survey;
        this.title = title;
        this.type = type;
        this.description = description;
        this.surveyDocumentId = surveyDocumentId;
        this.questionanswersList = questionAnswerList;
    }

    // 문항 List 넣어주기
    public void setQuestion(QuestionAnswer questionanswer) {
        this.questionanswersList.add(questionanswer);
    }

}
