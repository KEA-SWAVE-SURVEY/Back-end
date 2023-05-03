package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.QSurveyDocument;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.user.domain.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SurveyCustomImpl implements SurveyRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    public SurveyCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<SurveyDocument> findSurveyDocumentList(Long usrIdx, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SurveyDocument> findByCustom_offsetPaging(Pageable pageable) {
        QSurveyDocument surveyDocument = QSurveyDocument.surveyDocument;
        QUser user = QUser.user;

        List<SurveyDocument> results = jpaQueryFactory
                .select(surveyDocument)
                .from(surveyDocument)
                .leftJoin(surveyDocument.survey, user.survey)
                .fetchJoin()
                .orderBy(SurveyDocumentSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    private OrderSpecifier<?> SurveyDocumentSort(Pageable page) {
        QSurveyDocument surveyDocument = QSurveyDocument.surveyDocument;
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
            for (Sort.Order order : page.getSort()) {
                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
                switch (order.getProperty()){
                    case "startDate" :
                    case "descending":
                        return new OrderSpecifier(direction, surveyDocument);
                    case "title":
                        return new OrderSpecifier(direction, surveyDocument.title);

                }
            }
        }
        return null;
    }

}