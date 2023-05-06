package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class QuestionAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_analyze_id")
    private Long id;
    @Column(name = "choiceId(연관분석할 choiceId)")
    private Long choiceId;
    @Column(name = "question_title")
    private String questionTitle;
    @Column(name = "choice_title")
    private String choiceTitle;


    @OneToMany(mappedBy = "questionAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @Column(name = "choice_list")
    private List<ChoiceAnalyze> choiceAnalyzeList;

    @ManyToOne
    @JoinColumn(name = "survey_analyze_id")
    private SurveyAnalyze surveyAnalyzeId;

}
