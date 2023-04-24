package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/***
 * 2023-04-23 Gihyun Kim
 * Survey Document 와 Survey Response 를 저장할 Survey Entity
 */
@Data
@Entity
@NoArgsConstructor
public class Survey {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;
    @OneToMany(fetch = FetchType.LAZY)
    @Column(name = "survey_documentList")
    private List<SurveyDocument> surveyDocumentList;
    @OneToMany(fetch = FetchType.LAZY)
    @Column(name = "survey_responseList")
    private List<SurveyResponse> surveyResponseList;

    // List 에 survey Document & Response 를 저장할 method
    public void setDocument(SurveyDocument surveyDocument) {
        this.surveyDocumentList.add(surveyDocument);
    }
    public void setResponse(SurveyResponse surveyResponse) {
        this.surveyResponseList.add(surveyResponse);
    }

    @Builder
    public Survey(List<SurveyDocument> surveyDocumentList, List<SurveyResponse> surveyResponseList) {
        this.surveyDocumentList = surveyDocumentList;
        this.surveyResponseList = surveyResponseList;
    }
}
