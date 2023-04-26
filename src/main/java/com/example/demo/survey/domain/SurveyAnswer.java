package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SurveyAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_answer_id")
    private Long id;
    @Column(name = "survey_answer_user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "survey_type")
    private int type;
    @Column(name = "survey_title")
    private String title;
    @Column(name = "survey_description")
    private String description;
    @OneToMany(mappedBy = "survey_answer_id", fetch = FetchType.LAZY)
    @Column(name = "content")
    private List<QuestionAnswer> questionanswersList;
    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SurveyAnswer(Survey survey, String title, int type, String description, List<QuestionDocument> questionDocumentList) {
        this.survey = survey;
        this.title = title;
        this.type = type;
        this.description = description;
        this.questionanswersList = questionanswersList;
    }

    // 문항 List 넣어주기
    public void setQuestion(QuestionAnswer questionanswer) {
        this.questionanswersList.add(questionanswer);
    }


}