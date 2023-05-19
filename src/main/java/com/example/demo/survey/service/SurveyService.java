package com.example.demo.survey.service;

import com.example.demo.survey.exception.InvalidProcessException;
import com.example.demo.survey.response.SurveyAnalyzeDto;
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
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.*;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService2;
import com.example.demo.util.page.PageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException, UnknownHostException {

        // 유저 정보 받아오기
        checkInvalidToken(request);
        log.info("유저 정보 받아옴");

        // 유저 정보에 해당하는 Survey 저장소 가져오기
        Survey userSurvey = userService.getUser(request).getSurvey();
        if(userSurvey == null) {
            userSurvey = Survey.builder()
                    .user(userService.getUser(request))
                    .surveyDocumentList(new ArrayList<>())
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
                .surveyAnswerList(new ArrayList<>())
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

//        // 스냅샷 이미지 저장하기
//        // 172.16.210.25 : Image DB VM 접속하기
//        InetAddress imageVM = Inet4Address.getByAddress(new byte[]{(byte) 172, 16, (byte) 210, 25});
//
//        // 스냅샷 찍기
//        GrapzIt
    }

    public void captureSnapshot() {

    }

    // gird method 로 SurveyDocument 조회
    public List<SurveyDocument> readSurveyListByGrid(HttpServletRequest request, PageRequestDto pageRequest) {

        User user = userService.getUser(request);

        return surveyRepository.getSurveyDocumentListGrid(user, pageRequest);
    }

    // list method 로 SurveyDocument 조회
    public Page<SurveyDocument> readSurveyList(HttpServletRequest request, PageRequestDto pageRequest) throws Exception {

        checkInvalidToken(request);

        User user = userService.getUser(request);
        // gird 일 경우 그냥 다 보여주기
//        if(pageRequest.getMethod().equals("grid")) {
//            return surveyRepository.getSurveyDocumentListGrid();
//        }

        PageRequest page = PageRequest.builder()
                .page(pageRequest.getPage())
                .method(pageRequest.getMethod())
                .sortProperties(pageRequest.getSort1()) // date or title
                .direct(pageRequest.getSort2()) // ascending or descending
                .build();

        // Request Method
        // 1. view Method : grid or list
        // 2. what page number
        // 3. sort on What : date or title
        // 4. sort on How : ascending or descending
        Pageable pageable = page.of(page.getSortProperties(), page.getDirection(page.getDirect()));

        return surveyRepository.surveyDocumentPaging(user, pageable);
    }

    public SurveyDetailDto readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {

        checkInvalidToken(request);
//        User user = userService.getUser(request);
//
//        surveyRepository.findByUser(user.getId())
//                .getSurveyDocumentList().get()
        return getSurveyDetailDto(id);
    }

    // 설문 응답 참여
    public SurveyDetailDto getParticipantSurvey(Long id){
        return getSurveyDetailDto(id);
    }

    // 설문 응답 저장
    public void createSurveyAnswer(Long surveyDocumentId, SurveyResponseDto surveyResponse){
        // SurveyDocumentId를 통해 어떤 설문인지 가져옴
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();

        // Survey Response 를 Survey Answer 에 저장하기
        SurveyAnswer surveyAnswer = SurveyAnswer.builder()
                .surveyDocument(surveyDocument)
                .title(surveyResponse.getTitle())
                .description(surveyResponse.getDescription())
                .type(surveyResponse.getType())
                .questionAnswerList(new ArrayList<>())
                .build();
        surveyAnswerRepository.save(surveyAnswer);

        // Survey Response 를 Question Answer 에 저장하기
        surveyAnswerRepository.findById(surveyAnswer.getId());
        for (QuestionResponseDto questionResponseDto : surveyResponse.getQuestionResponse()) {
            // Question Answer 에 저장
            // todo: 주관식0 / 찬부식1, 객관식2 구분 저장
            QuestionAnswer questionAnswer = QuestionAnswer.builder()
                    .surveyAnswerId(surveyAnswerRepository.findById(surveyAnswer.getId()).get())
                    .title(questionResponseDto.getTitle())
                    .questionType(questionResponseDto.getType())
                    .checkAnswer(questionResponseDto.getAnswer())
                    .checkAnswerId(questionResponseDto.getAnswerId())
                    .surveyDocumentId(surveyDocumentId)
                    .build();
            questionAnswerRepository.save(questionAnswer);
            // if 찬부식 or 객관식
            // if 주관식 -> checkId에 주관식인 questionId가 들어감
            if(questionResponseDto.getType()!=0){
                //check 한 answer 의 id 값으로 survey document 의 choice 를 찾아서 count ++
                if (questionAnswer.getCheckAnswerId() != null) {
                    Optional<Choice> findChoice = choiceRepository.findById(questionAnswer.getCheckAnswerId());
    //                Optional<Choice> findChoice = choiceRepository.findByTitle(questionAnswer.getCheckAnswer());

                    if (findChoice.isPresent()) {
                        //todo: querydsl로 변경
                        findChoice.get().setCount(findChoice.get().getCount() + 1);
                        choiceRepository.save(findChoice.get());
                    }
                }
            }
            surveyAnswer.setQuestion(questionAnswer);
        }
        surveyAnswerRepository.flush();
        // 저장된 설문 응답을 Survey 에 연결 및 저장
        surveyDocument.setAnswer(surveyAnswer);
        surveyDocumentRepository.flush();

        //REST API to survey analyze controller
        restAPItoAnalyzeController(surveyDocumentId);
    }

    // 파이썬으로 DocumentId 보내주고 분석결과 Entity에 매핑해서 저장
    public void giveDocumentIdtoPython(String stringId) throws InvalidPythonException {
        long surveyDocumentId = Long.parseLong(stringId);

        try {
            System.out.println("pythonbuilder 시작");
            String arg1;
            ProcessBuilder builder;

            Resource[] resources = ResourcePatternUtils
                    .getResourcePatternResolver(new DefaultResourceLoader())
                    .getResources("classpath*:python/python2.py");

            log.info(String.valueOf(resources[0]));
            String substring = String.valueOf(resources[0]).substring(6, String.valueOf(resources[0]).length() -1);
            log.info(substring);

            builder = new ProcessBuilder("python", substring, String.valueOf(surveyDocumentId));

            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 자식 프로세스가 종료될 때까지 기다림
            int exitCode;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                // Handle interrupted exception
                exitCode = -1;
            }

            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                System.out.println("Error output:");
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println(errorLine);
                }
            }

            System.out.println("Process exited with code " + exitCode);

            //// 서브 프로세스가 출력하는 내용을 받기 위해
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = br.readLine();

            String inputString = line.replaceAll("'", "");
            log.info("result python");
            log.info(inputString);


            ObjectMapper objectMapper = new ObjectMapper();
            List<Object> List = objectMapper.readValue(inputString, List.class);

            // 값 분리해서 Analyze DB에 저장
            SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findBySurveyDocumentId(surveyDocumentId);
            // 과거의 분석 결과 있으면 questionAnalyze delete & null 주입
            if (surveyAnalyze != null) {
                Long id = surveyAnalyze.getId();
                questionAnalyzeRepository.deleteAllBySurveyAnalyzeId(surveyAnalyze);
            } else {
                surveyAnalyze = SurveyAnalyze.builder()
                        .surveyDocument(surveyDocumentRepository.findById(surveyDocumentId).get())
                        .questionAnalyzeList(new ArrayList<>())
                        .build();
            }

            surveyAnalyzeRepository.save(surveyAnalyze);
            //for 위의 예시(남성의 갯수) 배열의 갯수 만큼 (즉 설문의 총 choice 의 수) 루프
            //[[1,[[0.88,3],[0.8,5]]],[2,[[0.7,4],[0.5,6]]]]
            for (int j = 0; j < List.size(); j++) {
                java.util.List<Object> dataList = (List<Object>) List.get(j);
                Long choiceId = Long.valueOf((Integer) dataList.get(0));
                QuestionAnalyze questionAnalyze = new QuestionAnalyze();
                questionAnalyze = QuestionAnalyze.builder()
                        .surveyAnalyzeId(surveyAnalyze)
                        .choiceId(choiceId)
                        .choiceTitle(choiceRepository.findById(choiceId).get().getTitle())
                        .questionTitle(questionDocumentRepository.findById(choiceRepository.findById(choiceId).get().getQuestion_id().getId()).get().getTitle())
                        .choiceAnalyzeList(new ArrayList<>())
                        .build();

                questionAnalyzeRepository.save(questionAnalyze);

                // for문 [0.88,2] 같은 배열의 갯수 만큼
                // [[0.88,3],[0.8,5]]
                for (int i = 0; i < dataList.size()-1; i++) {
                    List<Object> subList = (List<Object>) dataList.get(i+1);
                    ChoiceAnalyze choiceAnalyze = new ChoiceAnalyze();
                    double support = Math.round((double) subList.get(0) *1000) / 1000.0;
                    Long choiceId2 = Long.valueOf((Integer) subList.get(1));
                    choiceAnalyze = choiceAnalyze.builder()
                            .choiceTitle(choiceRepository.findById(choiceId2).get().getTitle())
                            .support(support)
                            .questionAnalyzeId(questionAnalyze)
                            .choiceId(choiceId2)
                            .questionTitle(questionDocumentRepository.findById(choiceRepository.findById(choiceId2).get().getQuestion_id().getId()).get().getTitle())
                            .build();
                    choiceAnalyzeRepository.save(choiceAnalyze);
                }
            }
        } catch (IOException e) {
            // 체크 예외 -> 런타임 커스텀 예외 변환 처리
            // python 파일 오류
            throw new InvalidPythonException(e);
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
        return getSurveyDetailDto(surveyId);
    }

    // 분석 관리 Get
    public SurveyManageDto readSurveyMange(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
//        checkInvalidToken(request);

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
//        checkInvalidToken(request);
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
//        checkInvalidToken(request);
    }

    // 분석 상세 분석 Get
    public SurveyAnalyzeDto readSurveyDetailAnalyze(HttpServletRequest request, Long surveyId) throws InvalidTokenException {
        //Survey_Id를 가져와서 그 Survey 의 상세분석을 가져옴
        SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findBySurveyDocumentId(surveyId);

//        checkInvalidToken(request);
        return getSurveyDetailAnalyzeDto(surveyAnalyze.getId());
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
    private SurveyDetailDto getSurveyDetailDto(Long surveyDocumentId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();
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

            // question type에 따라 choice 에 들어갈 내용 구분
            // 주관식이면 choice title에 주관식 응답을 저장??
            // 객관식 찬부식 -> 기존 방식 과 똑같이 count를 올려서 저장
            List<ChoiceDetailDto> choiceDtos = new ArrayList<>();
            if (questionDocument.getQuestionType() == 0) {
                List<QuestionAnswer> questionAnswersByCheckAnswerId = questionAnswerRepository.findQuestionAnswersByCheckAnswerId(questionDocument.getId());
                for (QuestionAnswer questionAnswer : questionAnswersByCheckAnswerId) {
                    if (questionAnswer.getQuestionType() == 0) {
                        ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                        choiceDto.setId(questionAnswer.getId());
                        choiceDto.setTitle(questionAnswer.getCheckAnswer());
                        choiceDto.setCount(0);

                        choiceDtos.add(choiceDto);
                    }
                }
            } else {
                for (Choice choice : questionDocument.getChoiceList()) {
                    ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                    choiceDto.setId(choice.getId());
                    choiceDto.setTitle(choice.getTitle());
                    choiceDto.setCount(choice.getCount());

                    choiceDtos.add(choiceDto);
                }
            }
            questionDto.setChoiceList(choiceDtos);

            questionDtos.add(questionDto);
        }
        surveyDetailDto.setQuestionList(questionDtos);

        log.info(String.valueOf(surveyDetailDto));
        return surveyDetailDto;
    }

    private SurveyAnalyzeDto getSurveyDetailAnalyzeDto(Long surveyId) {
        SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findById(surveyId).get();
        SurveyAnalyzeDto surveyAnalyzeDto = new SurveyAnalyzeDto();

        // SurveyDocument에서 SurveyParticipateDto로 데이터 복사
        surveyAnalyzeDto.setId(surveyAnalyze.getId());

        List<QuestionAnalyzeDto> questionDtos = new ArrayList<>();
        for (QuestionAnalyze questionAnalyze : surveyAnalyze.getQuestionAnalyzeList()) {
            QuestionAnalyzeDto questionDto = new QuestionAnalyzeDto();
            questionDto.setId(questionAnalyze.getId());
            questionDto.setChoiceTitle(questionAnalyze.getChoiceTitle());
            questionDto.setQuestionTitle(questionAnalyze.getQuestionTitle());

            List<ChoiceAnalyzeDto> choiceDtos = new ArrayList<>();
            for (ChoiceAnalyze choice : questionAnalyze.getChoiceAnalyzeList()) {
                ChoiceAnalyzeDto choiceDto = new ChoiceAnalyzeDto();
                choiceDto.setId(choice.getId());
                choiceDto.setChoiceTitle(choice.getChoiceTitle());
                choiceDto.setSupport(choice.getSupport());
                choiceDto.setQuestionTitle(choice.getQuestionTitle());
                choiceDtos.add(choiceDto);
            }
            questionDto.setChoiceAnalyzeList(choiceDtos);

            questionDtos.add(questionDto);
        }
        surveyAnalyzeDto.setQuestionAnalyzeList(questionDtos);

        return surveyAnalyzeDto;
    }


    private static void restAPItoAnalyzeController(Long surveyDocumentId) {
        //REST API로 분석 시작 컨트롤러로 전달
        // Create a WebClient instance
        log.info("응답 저장 후 -> 분석 시작 REST API 전달");
        WebClient webClient = WebClient.create();

        // Define the API URL
        String apiUrl = "http://localhost:8080/api/research/analyze/create";

        // Make a GET request to the API and retrieve the response
        String post = webClient.post()
                .uri(apiUrl)
                .bodyValue(String.valueOf(surveyDocumentId))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Process the response as needed
        System.out.println("Request: " + post);
    }

}
