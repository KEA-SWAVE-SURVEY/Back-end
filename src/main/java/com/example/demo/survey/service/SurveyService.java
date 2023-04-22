package com.example.demo.survey.service;

import com.example.demo.survey.domain.Choice;
import com.example.demo.survey.domain.Question;
import com.example.demo.survey.domain.Survey;
import com.example.demo.survey.repository.ChoiceRepository;
import com.example.demo.survey.repository.QuestionRepository;
import com.example.demo.survey.repository.SurveyRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static com.example.demo.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

    public void createSurvey(SurveyRequestDto surveyRequest) {
        // todo : repo 저장, Survey - Question - Choice Connect 필요
        Survey survey = Survey.builder()
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .build();
        // survey repository save
        surveyRepository.save(survey);

        Survey surveyById = surveyRepository.findById(survey.getId()).orElseGet(null);

        // 설문 문항
        for (QuestionRequestDto questionRequestDto : surveyRequest.getQuestionRequest()) {
            Question question = new Question();
            question.setSurvey_id(surveyById);
            // 선지 문항
            switch (questionRequestDto.getType()) {
                case 2: // 객관식
                    Choice newChoice = new Choice();
                    for (Choice choice : linkChoiceToQuestion(questionRequestDto)) {
                        newChoice.setContent(choice.getContent());
                        //newChoice.setChoice(choice); // choice question 의 choice list 에 넣어준다
                        choiceRepository.save(choice); // choice repository 에 저장
                    }
                    break;
                case 1: // 찬부식, 주관식
                    //question.setTitle();
                    question = new Question(
                            questionRequestDto.getTitle(),
                            questionRequestDto.getType()
                    );
                case 0:
                    break;
                default:
            }
            questionRepository.save(question);
            surveyById.setQuestion(question);
        }

    }

    private List<Choice> linkChoiceToQuestion(QuestionRequestDto questionRequestDto) {
        List<Choice> choiceList = new ArrayList<>();
        for (ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
            Choice choice = Choice.builder()
                    .content(choiceRequestDto.getChoiceName()).build();
            choiceList.add(choice);
        }

        return choiceList;

    }
//    private void makeQuestionInSurvey_number(SurveyRequestDto surveyRequest, Survey survey) {
//        for (QuestionRequestDto questionRequest : surveyRequest.getQuestionRequest()) {
//            Question question = Question.builder()
//                    .title(questionRequest.getTitle())
////                    .description(questionRequest.getDescription())
//                    .build();
//
//            // Question 에 해당하는 답안(예를들어 1.치킨, 2.피자) 하나씩 받기
//            for (ChoiceRequestDto choiceRequest : questionRequest.getChoiceList()) {
//                Choice choice = Choice.builder()
//                        .content(choiceRequest.getChoiceName())
//                        .build();
//
//                // choice repository save
//                choiceRepository.save(choice);
//                question.setChoice(choice);
//            }
//
//            // question repository save
//            questionRepository.save(question);
//            survey.setQuestion(question);
//        }
//    }

    // todo : task 2 전체 설문 리스트 조회, 향후 페이징 처리시 쿼리 핸들링
    public List<Survey> readSurveyList() {
        return surveyRepository.findAll();
    }

    // todo : task 3 상세 설문 리스트 조회 문항만 보여줄 수 있는
    public Survey readSurveyDetail(Long id) {
        Survey survey = surveyRepository.findById(id).get();
        return survey;
    }
}
