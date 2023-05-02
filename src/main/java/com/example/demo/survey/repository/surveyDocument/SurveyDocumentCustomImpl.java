package com.example.demo.survey.repository.surveyDocument;

import com.example.demo.survey.domain.QSurveyDocument;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyDocumentCustomImpl implements SurveyDocumentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public SurveyDocumentCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<SurveyDocument> findSurveyDocumentList() {
        jpaQueryFactory
                .select(new QSurveyDocument())
                .from()
        return null;
    }

//    @Override
//    public Survey updateResponseCount() {
//        return null;
//    }

}
