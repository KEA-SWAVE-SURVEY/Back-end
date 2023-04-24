package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class SurveyDocument {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_document_id")
    private Long id;
    @Column(name = "survey_title")
    private String title;
    @Column(name = "survey_type")
    private int type;
    @Column(name = "survey_description")
    private String description;

    @OneToMany(mappedBy = "survey_document_id", fetch = FetchType.LAZY)
    @Column(name = "content")
    private List<QuestionDocument> questionDocumentList;

    @Builder
    public SurveyDocument(Long id, String title, int type, String description, List<QuestionDocument> questionDocumentList) {
        this.title = title;
        this.type = type;
        this.description = description;
        this.questionDocumentList = questionDocumentList;
    }

    // 문항 list 에 넣어주기
    public void setQuestion(QuestionDocument questionDocument) {
        this.questionDocumentList.add(questionDocument);
    }
}
