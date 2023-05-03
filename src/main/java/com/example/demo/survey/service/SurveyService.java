package com.example.demo.survey.service;

import com.example.demo.survey.domain.*;
import com.example.demo.survey.exception.InvalidPythonException;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.questionAnswer.QuestionAnswerRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.surveyAnalyze.SurveyAnalyzeRepository;
import com.example.demo.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.QuestionResponseDto;
import com.example.demo.survey.response.SurveyManageDto;
import com.example.demo.survey.response.SurveyResponseDto;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import com.example.demo.util.page.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//import static com.example.demo.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {

    private final UserService2 userService;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;
    private final QuestionDocumentRepository questionDocumentRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final ChoiceRepository choiceRepository;
    private final SurveyAnalyzeRepository surveyAnalyzeRepository;

    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException {

        // 유저 정보 받아오기
        checkInvalidToken(request);
        log.info("유저 정보 받아옴");

        // 유저 정보에 해당하는 Survey 저장소 가져오기
        Survey userSurvey = userService.getUser(request).getSurvey();
        if(userSurvey == null) {
            userSurvey = Survey.builder()
                    .user(userService.getUser(request))
                    .surveyDocumentList(null)
                    .surveyAnswerList(null)
                    .build();
            surveyRepository.save(userSurvey);
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
                    .surveyDocument(surveyDocumentRepository.findById(surveyDocument.getId()).get())
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
                        .count(0)
                        .build();
                choiceRepository.save(choice);
            }
        }

    }

    // todo : 메인 페이지에서 설문 리스트 (유저 관리 페이지에서 설문 리스트 x)
    public Page<SurveyDocument> readSurveyList(HttpServletRequest request, PageRequest pageRequest) throws Exception {

        checkInvalidToken(request);

        User user = userService.getUser(request);

        // Request Method
        // 1. view Method : grid or list
        // 2. what page number
        // 3. sort on What : date or title
        // 4. sort on How : ascending or descending
        Pageable pageable = pageRequest.of(pageRequest.getSortProperties(), pageRequest.getDirection(pageRequest.getDirect()));

        return surveyRepository.findByCustom_offsetPaging(pageable);
    }

    // todo : task 3 상세 설문 리스트 조회

    public SurveyDocument readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(id).get();

        checkInvalidToken(request);

        return surveyDocument;
    }

    // 설문 응답 참여
    public SurveyDocument getParticipantSurvey(Long id){
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(id).get();

        return surveyDocument;
    }

    // 설문 응답 저장
    public void createSurveyAnswer(Long surveyDocumentId, SurveyResponseDto surveyResponse){
        // SurveyDocumentId를 통해 어떤 설문인지 가져옴
        Optional<SurveyDocument> surveyDocument = surveyDocumentRepository.findById(surveyDocumentId);
        // surveyDocument 의 Survey 가져옴
        Survey survey = surveyDocument.get().getSurvey();

        // Survey Response 를 Survey Answer 에 저장하기
        SurveyAnswer surveyAnswer = SurveyAnswer.builder()
                .survey(survey)
                .title(surveyResponse.getTitle())
                .description(surveyResponse.getDescription())
                .type(surveyResponse.getType())
                .surveyDocumentId(surveyDocumentId)
                .build();
        surveyAnswerRepository.save(surveyAnswer);


        // Survey Response 를 Question Answer 에 저장하기
        surveyAnswerRepository.findById(surveyAnswer.getId());
        for (QuestionResponseDto questionResponseDto : surveyResponse.getQuestionResponse()) {
            // Question Answer 에 저장
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .surveyAnswerId(surveyAnswerRepository.findById(surveyAnswer.getId()).get())
                    .title(questionResponseDto.getTitle())
                    .questionType(questionResponseDto.getType())
                    .checkAnswer(questionResponseDto.getAnswer())
                    .checkAnswerId(questionResponseDto.getAnswerId())
                    .build();
            questionAnswerRepository.save(questionAnswer);
            //check 한 answer 의 id 값으로 survey document 의 choice 를 찾아서 count ++
            Optional<Choice> findChoice = choiceRepository.findById(questionAnswer.getCheckAnswerId());
            if (findChoice.isPresent()) {
                findChoice.get().setCount(findChoice.get().getCount() + 1);
                choiceRepository.save(findChoice.get());
            }
        }
        // 저장된 설문 응답을 Survey 에 연결 및 저장
        List<SurveyAnswer> surveyAnswerList = survey.getSurveyAnswerList();
        surveyAnswerList.add(surveyAnswer);
        survey.setSurveyAnswerList(surveyAnswerList);
        surveyRepository.save(survey);
    }

    // todo : 파이썬으로 DocumentId 보내줌
    public void giveDocumentIdtoPython(Long surveyDocumentId) throws InvalidPythonException {
        try {
            Process process = new ProcessBuilder("python", "python", String.valueOf(surveyDocumentId)).start();
        } catch (IOException e) {
            // 체크 예외 -> 런타임 커스텀 예외 변환 처리
            throw new InvalidPythonException();
        }

    }
    // 분석 응답 리스트 불러오기 (보류)
    public List<SurveyAnswer> readSurveyAnswerList(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        //Survey_Id를 가져와서 그 Survey 의 AnswerList 를 가져와야 함
        List<SurveyAnswer> surveyAnswerList = surveyAnswerRepository.findSurveyAnswersBySurveyDocumentId(surveyId);

        checkInvalidToken(request);

        return surveyAnswerList;
    }

    // todo : 분석 응답 (문항 별 응답 수 불러오기) (Count)
    public SurveyDocument readCountChoice(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        checkInvalidToken(request);

        //Survey_Id를 가져와서 그 Survey 의 AnswerList 를 가져와야 함
        Optional<SurveyDocument> byId = surveyDocumentRepository.findById(surveyId);
        if (byId.isPresent()) {
            SurveyDocument surveyDocument = byId.get();
            return surveyDocument;
        } else {
            throw new RuntimeException("Survey with ID " + surveyId + " not found.");
        }
    }

    // todo : 분석 관리 Get
    public SurveyManageDto readSurveyMange(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        checkInvalidToken(request);

        //Survey_Id를 가져와서 그 Survey 의 AnswerList 를 가져와야 함
        Optional<SurveyDocument> findSurvey = surveyDocumentRepository.findById(surveyId);

        if (findSurvey.isPresent()) {
            SurveyManageDto manage = new SurveyManageDto();
            manage.builder()
                    .acceptResponse(findSurvey.get().isAcceptResponse())
                    .startDate(findSurvey.get().getStartDate())
                    .deadline(findSurvey.get().getDeadline())
                    .url(findSurvey.get().getUrl())
                    .build();

            return manage;
        }else {
            throw new RuntimeException("Survey with ID " + surveyId + " not found.");
        }
    }

    // todo : 분석 관리 Post
    public void setSurveyMange(HttpServletRequest request, Long surveyId, SurveyManageDto manage) throws InvalidTokenException {
        Optional<SurveyDocument> optionalSurvey = surveyDocumentRepository.findById(surveyId);

        if (optionalSurvey.isPresent()) {
            SurveyDocument survey = optionalSurvey.get();
            // update survey properties using the manage DTO
            survey.setDeadline(manage.getDeadline());
            survey.setUrl(manage.getUrl());
            survey.setStartDate(manage.getStartDate());
            survey.setAcceptResponse(manage.isAcceptResponse());

            surveyDocumentRepository.save(survey);
        } else {
            throw new RuntimeException("Survey with ID " + surveyId + " not found.");
        }

        checkInvalidToken(request);
    }

    // todo : 분석 상세 분석
    public SurveyAnalyze readSurveyDetailAnalyze(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        //Survey_Id를 가져와서 그 Survey 의 상세분석을 가져옴
        SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findBySurveyDocumentId(surveyId);

        checkInvalidToken(request);

        return surveyAnalyze;
    }

    // 회원 유효성 검사, token 존재하지 않으면 예외처리
    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
        if(request.getHeader("Authorization") == null) {
            log.info("error");
            throw new InvalidTokenException();
        }
        log.info("토큰 체크 완료");
    }
}
