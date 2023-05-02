package com.example.demo.survey.repository.surveyDocument;

import com.example.demo.survey.domain.QSurveyAnswer;
import com.example.demo.survey.domain.QSurveyDocument;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.survey.domain.QSurveyAnswer.surveyAnswer;
import static com.example.demo.survey.domain.QSurveyDocument.surveyDocument;
import static com.example.demo.survey.domain.QSurvey.survey;
import static com.example.demo.util.querydsl.DslCustomExpression.*;

@Repository
public class SurveyDocumentCustomImpl implements SurveyDocumentRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public SurveyDocumentCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<SurveyDocument> findSurveyDocumentList(SurveyDocument surveyRequest, Pageable pageable) {
        List<SurveyDocument> content = getContent(surveyRequest, pageable);

        return new PageImpl<>(content, pageable, content.size());
    }

    private List<SurveyDocument> getContent(SurveyDocument surveyRequest, Pageable pageable){
        return jpaQueryFactory
                .selectFrom(surveyDocument)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Long updateAnswerCount(Long idx) {
        return jpaQueryFactory
                .update(surveyDocument)
                .set(surveyDocument.responseCount, surveyDocument.responseCount.add(1))
                .where(surveyDocument.id.eq(idx))
                .execute();
    }
}
