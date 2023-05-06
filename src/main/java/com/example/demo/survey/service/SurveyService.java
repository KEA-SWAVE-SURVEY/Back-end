package com.example.demo.survey.service;

import com.example.demo.survey.analyze.SurveyAnalyzeDto;
import com.example.demo.survey.domain.*;
import com.example.demo.survey.exception.InvalidPythonException;
import com.example.demo.survey.exception.InvalidSurveyException;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.choiceAnalyze.ChoiceAnalyzeRepository;
import com.example.demo.survey.repository.questionAnlayze.QuestionAnalyzeRepository;
import com.example.demo.survey.repository.questionAnswer.QuestionAnswerRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.surveyAnalyze.SurveyAnalyzeRepository;
import com.example.demo.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.*;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import com.example.demo.util.page.PageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

//import static com.example.demo.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {
    private final ChoiceAnalyzeRepository choiceAnalyzeRepository;
    private final QuestionAnalyzeRepository questionAnalyzeRepository;

    private final UserService2 userService;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;
    private final QuestionDocumentRepository questionDocumentRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final ChoiceRepository choiceRepository;
    private final SurveyAnalyzeRepository surveyAnalyzeRepository;

    @Transactional
    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException {

        // 유저 정보 받아오기
        checkInvalidToken(request);
        log.info("유저 정보 받아옴");

        // 유저 정보에 해당하는 Survey 저장소 가져오기
        Survey userSurvey = userService.getUser(request).getSurvey();
        if(userSurvey == null) {
            userSurvey = Survey.builder()
                    .user(userService.getUser(request))
                    .surveyDocumentList(new ArrayList<>())
                    .surveyAnswerList(new ArrayList<>())
                    .build();
            surveyRepository.save(userSurvey);
        }

        // Survey Request 를 Survey Document 에 저장하기
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .survey(userSurvey)
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .questionDocumentList(new ArrayList<>())
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

            if(questionRequestDto.getType() == 0) continue; // 주관식

            // 객관식, 찬부식일 경우 선지 저장
            questionDocument.setChoiceList(new ArrayList<>());
            for(ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
                Choice choice = Choice.builder()
                        .question_id(questionDocumentRepository.findById(questionDocument.getId()).get())
                        .title(choiceRequestDto.getChoiceName())
                        .count(0)
                        .build();
                choiceRepository.save(choice);
                questionDocument.setChoice(choice);
            }
            surveyDocument.setQuestion(questionDocument);
            // choice 가 추가될 때마다 변경되는 Question Document 정보 저장
            questionDocumentRepository.flush();
        }
        // question 이 추가될 때마다 변경되는 Survey Document 정보 저장
        surveyDocumentRepository.flush();

        // Survey 에 SurveyDocument 저장
        userSurvey.setDocument(surveyDocument);
        surveyRepository.flush();

    }

    public Page<SurveyDocument> readSurveyList(HttpServletRequest request, PageRequest pageRequest) throws Exception {

        checkInvalidToken(request);

        User user = userService.getUser(request);

        // Request Method
        // 1. view Method : grid or list
        // 2. what page number
        // 3. sort on What : date or title
        // 4. sort on How : ascending or descending
        Pageable pageable = pageRequest.of(pageRequest.getSortProperties(), pageRequest.getDirection(pageRequest.getDirect()));

        return surveyRepository.surveyDocumentPaging(user, pageable);
    }

    public SurveyDocument readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {

//        checkInvalidToken(request);
//        User user = userService.getUser(request);
//
//        surveyRepository.findByUser(user.getId())
//                .getSurveyDocumentList().get()
        return null;
    }

    // 설문 응답 참여
    public SurveyDetailDto getParticipantSurvey(Long id){
        return getSurveyDetailDto(id);
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
            if (questionAnswer.getCheckAnswerId() != null) {
                Optional<Choice> findChoice = choiceRepository.findById(questionAnswer.getCheckAnswerId());
                if (findChoice.isPresent()) {
                    findChoice.get().setCount(findChoice.get().getCount() + 1);
                    choiceRepository.save(findChoice.get());
                }
            }
        }
        // 저장된 설문 응답을 Survey 에 연결 및 저장
        List<SurveyAnswer> surveyAnswerList = survey.getSurveyAnswerList();
        surveyAnswerList.add(surveyAnswer);
        survey.setSurveyAnswerList(surveyAnswerList);
        surveyRepository.save(survey);
    }

    // 파이썬으로 DocumentId 보내줌
    public void giveDocumentIdtoPython(Long surveyDocumentId) throws InvalidPythonException {
        try {
            Process process = new ProcessBuilder("python", "python", String.valueOf(surveyDocumentId)).start();

            /**
             [1(남성의choiceId),
              [
                [0.88,2(짜장의ChoiceId)],
                [0.80,3(싫음의ChoiceId)]
              ]
             ]
             **/

            // todo: 값 분리해서 Analyze DB에 저장
            SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findBySurveyDocumentId(surveyDocumentId);
            if (surveyAnalyze != null) {
                Long id = surveyAnalyze.getId();
                surveyAnalyzeRepository.deleteAllById(Collections.singleton(id));
            }
            Optional<SurveyDocument> byId = surveyDocumentRepository.findById(surveyDocumentId);
            surveyAnalyze.builder()
                    .surveyDocumentId(byId.get())
                    .questionAnalyzeList(new ArrayList<>())
                    .build();

            surveyAnalyzeRepository.save(surveyAnalyze);

            //for 위의 예시의 배열의 갯수 만큼 (즉 설문의 총 choice 의 수) 루프
            QuestionAnalyze questionAnalyze = new QuestionAnalyze();
            questionAnalyze.builder()
                    .surveyAnalyzeId(surveyAnalyze)
                    .choiceId(1L)
                    .choiceTitle(choiceRepository.findById(1L).get().getTitle())
                    .questionTitle(questionDocumentRepository.findById(choiceRepository.findById(1L).get().getQuestion_id().getId()).get().getTitle())
                    .choiceAnalyzeList(new ArrayList<>())
                    .build();



        } catch (IOException e) {
            // 체크 예외 -> 런타임 커스텀 예외 변환 처리
            throw new InvalidPythonException();
        }
    }

    // todo : 분석 응답 리스트 불러오기
    public List<SurveyAnswer> readSurveyAnswerList(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        //Survey_Id를 가져와서 그 Survey 의 AnswerList 를 가져와야 함
        List<SurveyAnswer> surveyAnswerList = surveyAnswerRepository.findSurveyAnswersBySurveyDocumentId(surveyId);

        checkInvalidToken(request);

        return surveyAnswerList;
    }

    // 분석 응답 (문항 별 응답 수 불러오기) (Count)
    public SurveyDetailDto readCountChoice(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
//        checkInvalidToken(request);

        //
        return getSurveyDetailDto(surveyId);
    }

    // 분석 관리 Get
    public SurveyManageDto readSurveyMange(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        checkInvalidToken(request);

        //Survey_Id를 가져와서 그 Survey 의 Document 를 가져옴
        Optional<SurveyDocument> findSurvey = surveyDocumentRepository.findById(surveyId);

        if (findSurvey.isPresent()) {
            //manage 부분만 추출
            SurveyManageDto manage = new SurveyManageDto();
            manage.builder()
                    .acceptResponse(findSurvey.get().isAcceptResponse())
                    .startDate(findSurvey.get().getStartDate())
                    .deadline(findSurvey.get().getDeadline())
                    .url(findSurvey.get().getUrl())
                    .build();

            return manage;
        }else {
            throw new InvalidSurveyException();
        }
    }

    // 분석 관리 Post
    public void setSurveyMange(HttpServletRequest request, Long surveyId, SurveyManageDto manage) throws InvalidTokenException {
        Optional<SurveyDocument> optionalSurvey = surveyDocumentRepository.findById(surveyId);

        if (optionalSurvey.isPresent()) {
            SurveyDocument surveyDocument = optionalSurvey.get();
            // update survey properties using the manage DTO
            surveyDocument.setDeadline(manage.getDeadline());
            surveyDocument.setUrl(manage.getUrl());
            surveyDocument.setStartDate(manage.getStartDate());
            surveyDocument.setAcceptResponse(manage.isAcceptResponse());

            surveyDocumentRepository.save(surveyDocument);
        } else {
            throw new InvalidSurveyException();
        }

        checkInvalidToken(request);
    }

    // 분석 상세 분석 Get
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

    // SurveyDocument Response 보낼 SurveyDetailDto로 변환하는 메서드
    private SurveyDetailDto getSurveyDetailDto(Long surveyId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyId).get();
        SurveyDetailDto surveyDetailDto = new SurveyDetailDto();

        // SurveyDocument에서 SurveyParticipateDto로 데이터 복사
        surveyDetailDto.setId(surveyDocument.getId());
        surveyDetailDto.setTitle(surveyDocument.getTitle());
        surveyDetailDto.setDescription(surveyDocument.getDescription());

        List<QuestionDetailDto> questionDtos = new ArrayList<>();
        for (QuestionDocument questionDocument : surveyDocument.getQuestionDocumentList()) {
            QuestionDetailDto questionDto = new QuestionDetailDto();
            questionDto.setId(questionDocument.getId());
            questionDto.setTitle(questionDocument.getTitle());
            questionDto.setQuestionType(questionDocument.getQuestionType());

            List<ChoiceDetailDto> choiceDtos = new ArrayList<>();
            for (Choice choice : questionDocument.getChoiceList()) {
                ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                choiceDto.setId(choice.getId());
                choiceDto.setTitle(choice.getTitle());

                choiceDtos.add(choiceDto);
            }
            questionDto.setChoiceList(choiceDtos);

            questionDtos.add(questionDto);
        }
        surveyDetailDto.setQuestionList(questionDtos);

        return surveyDetailDto;
    }
}
