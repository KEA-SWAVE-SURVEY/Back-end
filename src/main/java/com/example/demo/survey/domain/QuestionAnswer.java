package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class QuestionAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_answer_id")
    private Long id;
    @Column(name = "question_title")
    private String title;
    @Column(name = "question_type")
    private int questionType;
    @Column(name = "choice_answer")
    private String checkAnswer;

    @ManyToOne
    @JoinColumn(name = "survey_answer_id")
    private SurveyAnswer survey_answer_id;

    // 생성자 오버로딩
    @Builder
    // 객관식 생성자
    public QuestionAnswer(SurveyAnswer survey_answer_id, String title, int questionType, String checkAnswer) {
        this.survey_answer_id = survey_answer_id;
        this.title = title;
        this.questionType = questionType;
        this.checkAnswer = checkAnswer;
    }

    @Builder
    // 주관식, 찬부신 생성자
    public QuestionAnswer(String title, int questionType) {
        this.title = title;
        this.questionType = questionType;
    }
}
