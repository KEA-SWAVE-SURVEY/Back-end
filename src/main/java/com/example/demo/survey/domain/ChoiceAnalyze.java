package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class ChoiceAnalyze {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;
    @Column(name = "support", nullable = false)
    private double support;
    //하나만 가져옴
    @Column(name = "choice_id_연관분석된_choice_id")
    private long choiceId;
    @Column(name = "question_title")
    private String questionTitle;
    @Column(name = "choice_title")
    private String choiceTitle;


    @ManyToOne
    @JoinColumn(name = "question_analyze_id")
    private QuestionAnalyze questionAnalyzeId;

}
