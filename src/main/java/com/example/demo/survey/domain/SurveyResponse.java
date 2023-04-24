package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SurveyResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_response_id")
    private Long id;
    @Column(name = "survey_type")
    private int type;
    @Column(name = "survey_title")
    private String title;
    @Column(name = "survey_description")
    private String description;
    @OneToMany(mappedBy = "survey_response_id", fetch = FetchType.LAZY)
    @Column(name = "content")
    private List<QuestionResponse> questionResponses;
    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SurveyResponse(int type, String title, String description, List<QuestionResponse> questionResponses) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.questionResponses = questionResponses;
    }

}
