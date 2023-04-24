package com.example.demo.survey.domain;

import com.example.demo.user.domain.User;
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
    @OneToOne
    @JoinColumn(name = "user_Id")
    private User user;
    @OneToMany(mappedBy = "survey_id", fetch = FetchType.LAZY)
    @Column(name = "survey_documentList")
    private List<SurveyDocument> surveyDocumentList;
    @OneToMany(mappedBy = "survey_id", fetch = FetchType.LAZY)
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
    public Survey(User user, List<SurveyDocument> surveyDocumentList, List<SurveyResponse> surveyResponseList) {
        this.user = user;
        this.surveyDocumentList = surveyDocumentList;
        this.surveyResponseList = surveyResponseList;
    }
}
