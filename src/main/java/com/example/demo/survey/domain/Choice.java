package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Choice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionDocument question_id;
    private String title;

    @Builder
    public Choice(String title, QuestionDocument question_id) {
        this.question_id = question_id;
        this.title = title;
    }
}
