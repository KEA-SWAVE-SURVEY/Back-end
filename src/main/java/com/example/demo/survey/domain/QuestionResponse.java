package com.example.demo.survey.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class QuestionResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int type;
    private String checkAnswer;

    @Builder
    public QuestionResponse(int type, String checkAnswer) {
        this.type = type;
        this.checkAnswer = checkAnswer;
    }
}
