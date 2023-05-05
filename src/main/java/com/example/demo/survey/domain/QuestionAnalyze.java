package com.example.demo.survey.domain;

import com.example.demo.survey.analyze.ChoiceAnalyzeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_analyze_id")
    private Long id;
    private Long support;
    private String questionTitle;
    private String choiceTitle;
    private List<ChoiceAnalyzeDto> choiceAnalyzeList;


//    @ManyToOne
//    @JoinColumn(name = "survey_answer_id")
//    private SurveyAnalyze  surveyAnalyzeId;
}
