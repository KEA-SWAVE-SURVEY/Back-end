package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SurveyResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(fetch = FetchType.LAZY)
    private List<QuestionResponse> questionResponses;

}
