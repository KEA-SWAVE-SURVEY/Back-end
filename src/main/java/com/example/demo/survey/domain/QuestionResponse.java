package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class QuestionResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_response_id")
    private Long id;
    @Column(name = "question_type")
    private int type;
    @Column(name = "choice_answer")
    private String checkAnswer;

    @JoinColumn(name = "survey_response_id")
    SurveyResponse survey_response_id;

    @Builder
    public QuestionResponse(int type, String checkAnswer) {
        this.type = type;
        this.checkAnswer = checkAnswer;
    }
}
