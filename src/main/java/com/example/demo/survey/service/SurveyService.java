package com.example.demo.survey.service;

import com.example.demo.survey.domain.Choice;
import com.example.demo.survey.domain.QuestionDocument;
import com.example.demo.survey.domain.SurveyDocument;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.repository.ChoiceRepository;
import com.example.demo.survey.repository.QuestionRepository;
import com.example.demo.survey.repository.SurveyRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//import static com.example.demo.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    public void createSurvey(SurveyRequestDto surveyRequest) {
        // todo : repo 저장, Survey - Question - Choice Connect 필요
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .build();
        // survey repository save
        surveyRepository.save(surveyDocument);

        SurveyDocument surveyDocumentById = surveyRepository.findById(surveyDocument.getId()).orElseGet(null);

        // 설문 문항
        for (QuestionRequestDto questionRequestDto : surveyRequest.getQuestionRequest()) {
            QuestionDocument questionDocument = new QuestionDocument();
            questionDocument.setSurvey_Document_id(surveyDocumentById);
            // 선지 문항
            switch (questionRequestDto.getType()) {
                case 2: // 객관식
                    Choice newChoice = new Choice();
                    for (Choice choice : linkChoiceToQuestion(questionRequestDto)) {
                        newChoice.setTitle(choice.getTitle());
                        //newChoice.setChoice(choice); // choice question 의 choice list 에 넣어준다
                        choiceRepository.save(choice); // choice repository 에 저장
                    }
                    break;
                case 1: // 찬부식, 주관식
                    //question.setTitle();
                    questionDocument = new QuestionDocument(
                            questionRequestDto.getTitle(),
                            questionRequestDto.getType()
                    );
                case 0:
                    break;
                default:
            }
            questionRepository.save(questionDocument);
            surveyDocumentById.setQuestion(questionDocument);
        }

    }

    private List<Choice> linkChoiceToQuestion(QuestionRequestDto questionRequestDto) {
        List<Choice> choiceList = new ArrayList<>();
        for (ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
            Choice choice = Choice.builder()
                    .title(choiceRequestDto.getChoiceName()).build();
            choiceList.add(choice);
        }

        return choiceList;

    }

    // todo : 메인 페이지에서 설문 리스트 (유저 관리 페이지에서 설문 리스트 x)
    public List<SurveyDocument> readSurveyList(HttpServletRequest request) throws Exception {

        checkInvalidToken(request);

        return surveyRepository.findAll();
    }

    // todo : task 3 상세 설문 리스트 조회

    public SurveyDocument readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {
        SurveyDocument surveyDocument = surveyRepository.findById(id).get();

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
