package com.example.demo.survey.service;

import com.example.demo.survey.domain.Choice;
import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static com.example.demo.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final UserService2 userService;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;
    private final QuestionDocumentRepository questionDocumentRepository;
    private final ChoiceRepository choiceRepository;

    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException {

        // 유저 정보 받아오기
        checkInvalidToken(request);

        // 유저 정보에 해당하는 Survey 저장소 가져오기
        Survey userSurvey = userService.getUser(request).getSurvey();
        if(userSurvey == null) {
            Survey survey = Survey.builder()
                    .user(userService.getUser(request))
                    .surveyDocumentList(null)
                    .surveyDocumentList(null)
                    .build();
            surveyRepository.save(survey);
        }

        // Survey Request 를 Survey Document 에 저장하기
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .survey(userSurvey)
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .build();
        surveyDocumentRepository.save(surveyDocument);


        // 설문 문항
        surveyDocumentRepository.findById(surveyDocument.getId());
        for (QuestionRequestDto questionRequestDto : surveyRequest.getQuestionRequest()) {
            // 설문 문항 저장
            QuestionDocument questionDocument = QuestionDocument.builder()
                    .survey_document_id(surveyDocumentRepository.findById(surveyDocument.getId()).get())
                    .title(questionRequestDto.getTitle())
                    .questionType(questionRequestDto.getType())
                    .build();
            questionDocumentRepository.save(questionDocument);

            if(questionRequestDto.getType() == 1) continue; // 주관식

            // 객관식, 찬부식일 경우 선지 저장
            for(ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
                Choice choice = Choice.builder()
                        .question_id(questionDocumentRepository.findById(questionDocument.getId()).get())
                        .title(choiceRequestDto.getChoiceName())
                        .build();
                choiceRepository.save(choice);
            }
        }

    }

    // todo : 메인 페이지에서 설문 리스트 (유저 관리 페이지에서 설문 리스트 x)
    public List<SurveyDocument> readSurveyList(HttpServletRequest request) throws Exception {

        checkInvalidToken(request);

        User user = userService.getUser(request);

        return surveyRepository.findById(user.getId()).get()
                .getSurveyDocumentList();
    }

    // todo : task 3 상세 설문 리스트 조회

    public SurveyDocument readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(id).get();

        checkInvalidToken(request);

        return surveyDocument;
    }

    // 회원 유효성 검사, token 존재하지 않으면 예외처리
    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
        if(request.getHeader("Authorization") == null) {
            throw new InvalidTokenException();
        }
    }
}
