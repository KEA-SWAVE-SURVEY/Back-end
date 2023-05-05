package com.example.demo.survey.repository.survey;

import com.example.demo.survey.domain.QSurvey;
import com.example.demo.survey.domain.QSurveyDocument;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.user.domain.QUser;
import com.example.demo.user.domain.User;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SurveyRepositoryImpl implements SurveyRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    // survey document 의 list 를 페이징 처리하여 조회
    @Override
    public Page<SurveyDocument> surveyDocumentPaging(User userRequest, Pageable pageable) {
        List<SurveyDocument> results = getSurveyDocumentList(userRequest, pageable);

        return new PageImpl<>(results, pageable, results.size());
    }

    public List<SurveyDocument> getSurveyDocumentList(User userRequest, Pageable pageable) {
        QSurveyDocument surveyDocument = QSurveyDocument.surveyDocument;
        QSurvey survey = QSurvey.survey;
        QUser user = QUser.user;

        List<SurveyDocument> results = jpaQueryFactory
                .selectFrom(surveyDocument)
                .leftJoin(surveyDocument.survey).on(survey.user.eq(userRequest))
                .orderBy(SurveyDocumentSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return results;
    }

    // survey document 상세 조회
    @Override
    public SurveyDocument surveyDocumentDetail(User userRequest, SurveyDocument surveyDocumentRequest) {
        QUser user = QUser.user;
        QSurvey survey = QSurvey.survey;
        QSurveyDocument surveyDocument = QSurveyDocument.surveyDocument;

        SurveyDocument result = (SurveyDocument) jpaQueryFactory
                .select(survey.surveyDocumentList)
                .from(survey)
                .where(survey.user.eq(userRequest)
                        .and(survey.surveyDocumentList.contains(surveyDocumentRequest)))
                .fetch();
        return null;
    }

    // 특정 기준(날짜 or 오름차순)에 따라 정렬
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
                        log.info("날짜 순으로 정렬");
                        return new OrderSpecifier(direction, surveyDocument.startDate);
                    case "title":
                        log.info("제목 순으로 정렬");
                        return new OrderSpecifier(direction, surveyDocument.title);

                }
            }
        }
        return null;
    }

}