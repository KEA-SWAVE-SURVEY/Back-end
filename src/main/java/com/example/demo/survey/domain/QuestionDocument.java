package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class QuestionDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;
    @Column(name = "question_title")
    private String title;
    @Column(name = "question_type")
    private int questionType;
    @OneToMany(mappedBy = "questionDocumentId", fetch = FetchType.LAZY)
    @Column(name = "choice_list")
    private List<Choice> choiceList;

    @ManyToOne
    @JoinColumn(name = "survey_document_id")
    @Column(name = "survey_id")
    private SurveyDocument survey_Document_id;

    // 생성자 오버로딩
    @Builder
    // 객관식 생성자
    public QuestionDocument(String title, int questionType, List<Choice> choiceList) {
        this.title = title;
        this.questionType = questionType;
        this.choiceList = choiceList;
    }

    @Builder
    // 주관식, 찬부신 생성자
    public QuestionDocument(String title, int questionType) {
        this.title = title;
        this.questionType = questionType;
    }

    public void setChoice(Choice choice) {
        this.choiceList.add(choice);
    }
}
