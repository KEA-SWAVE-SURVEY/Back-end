package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
public class Question {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private int questionType;
    @OneToMany(mappedBy = "questionId", fetch = FetchType.LAZY)
    private List<Choice> choiceList;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey_id;

    // 생성자 오버로딩
    @Builder
    // 객관식 생성자
    public Question(String title, int questionType, List<Choice> choiceList) {
        this.title = title;
        this.questionType = questionType;
        this.choiceList = choiceList;
    }

    @Builder
    // 주관식, 찬부신 생성자
    public Question(String title, int questionType) {
        this.title = title;
        this.questionType = questionType;
    }

    public void setChoice(Choice choice) {
        this.choiceList.add(choice);
    }
}
