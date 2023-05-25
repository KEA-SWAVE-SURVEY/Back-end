package com.example.demo.survey.service;

import com.example.demo.survey.domain.*;
import com.example.demo.survey.exception.InvalidPythonException;
import com.example.demo.survey.exception.InvalidSurveyException;
import com.example.demo.survey.exception.InvalidTokenException;
import com.example.demo.survey.repository.aprioriAnlayze.AprioriAnalyzeRepository;
import com.example.demo.survey.repository.chiAnlayze.ChiAnalyzeRepository;
import com.example.demo.survey.repository.choice.ChoiceRepository;
import com.example.demo.survey.repository.choiceAnalyze.ChoiceAnalyzeRepository;
import com.example.demo.survey.repository.compareAnlayze.CompareAnalyzeRepository;
import com.example.demo.survey.repository.questionAnlayze.QuestionAnalyzeRepository;
import com.example.demo.survey.repository.questionAnswer.QuestionAnswerRepository;
import com.example.demo.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.demo.survey.repository.survey.SurveyRepository;
import com.example.demo.survey.repository.surveyAnalyze.SurveyAnalyzeRepository;
import com.example.demo.survey.repository.surveyAnswer.SurveyAnswerRepository;
import com.example.demo.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.demo.survey.repository.wordCloud.WordCloudRepository;
import com.example.demo.survey.request.ChoiceRequestDto;
import com.example.demo.survey.request.PageRequestDto;
import com.example.demo.survey.request.QuestionRequestDto;
import com.example.demo.survey.request.SurveyRequestDto;
import com.example.demo.survey.response.*;
import com.example.demo.user.domain.User;
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
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.*;

import static org.thymeleaf.util.ArrayUtils.contains;

//import static com.example.demo.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyDocumentService {
    private final WordCloudRepository wordCloudRepository;
    private final ChiAnalyzeRepository chiAnalyzeRepository;
    private final CompareAnalyzeRepository compareAnalyzeRepository;
    private final AprioriAnalyzeRepository aprioriAnalyzeRepository;
    private final ChoiceAnalyzeRepository choiceAnalyzeRepository;
    private final QuestionAnalyzeRepository questionAnalyzeRepository;

    private final UserService2 userService;
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
    public void createSurveyAnswer(SurveyResponseDto surveyResponse){
        Long surveyDocumentId = surveyResponse.getId();
        // SurveyDocumentId를 통해 어떤 설문인지 가져옴
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();

        // Survey Response 를 Survey Answer 에 저장하기
        SurveyAnswer surveyAnswer = SurveyAnswer.builder()
                .surveyDocumentId(surveyDocument.getId())
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
//        surveyDocument.setAnswer(surveyAnswer);
        surveyDocumentRepository.flush();

        //REST API to survey analyze controller
        restAPItoAnalyzeController(surveyDocumentId);
    }

    // 파이썬에 DocumentId 보내주고 분석결과 Entity에 매핑해서 저장
    public void analyze(String stringId) throws InvalidPythonException {
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

            //[[1,[[0.88,3],[0.8,5]]],[2,[[0.7,4],[0.5,6]]]]
            /**
             * [
             * [['1', [0.6666666666666666, '3'], [0.3333333333333333, '4']], ['2', [0.6666666666666666, '4'], [0.3333333333333333, '3']], ['3', [0.6666666666666666, '1'], [0.3333333333333333, '2']], ['4', [0.6666666666666666, '2'], [0.3333333333333333, '1']]],
             * [[[1.0], [1.0]], [[1.0], [1.0]]],
             * [[0.10247043485974942, 1.0], [1.0, 0.10247043485974942]]
             * ]
             **/
            String testString = "[[['1', [0.6666666666666666, '3'], [0.3333333333333333, '4']], ['2', [0.6666666666666666, '4'], [0.3333333333333333, '3']], ['3', [0.6666666666666666, '1'], [0.3333333333333333, '2']], ['4', [0.6666666666666666, '2'], [0.3333333333333333, '1']]], [[[1.0], [1.0]], [[1.0], [1.0]]], [[0.10247043485974942, 1.0], [1.0, 0.10247043485974942]]]";

//            test
//            String inputString = line.replaceAll("'", "");
            String inputString = testString.replaceAll("'", "");

            log.info("result python");
            log.info(inputString);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Object> testList = objectMapper.readValue(inputString, List.class);
            log.info(String.valueOf(testList));

            ArrayList<Object> apriori = (ArrayList<Object>) testList.get(0);
            ArrayList<Object> compare = (ArrayList<Object>) testList.get(1);
            ArrayList<Object> chi= (ArrayList<Object>) testList.get(2);

            // 값 분리해서 Analyze DB에 저장
            SurveyAnalyze surveyAnalyze = surveyAnalyzeRepository.findBySurveyDocumentId(surveyDocumentId);
            // 과거의 분석 결과 있으면 questionAnalyze delete & null 주입
            if (surveyAnalyze != null) {
                Long id = surveyAnalyze.getId();
                questionAnalyzeRepository.deleteAllBySurveyAnalyzeId(surveyAnalyze);
            } else {
                surveyAnalyze = SurveyAnalyze.builder()
                        .surveyDocumentId(surveyDocumentId)
                        .questionAnalyzeList(new ArrayList<>())
                        .build();
            }
            surveyAnalyzeRepository.save(surveyAnalyze);

            int p = 0;
            for (QuestionDocument questionDocument : surveyDocumentRepository.findById(surveyDocumentId).get().getQuestionDocumentList()) {
                if (questionDocument.getQuestionType() == 0) {
                    continue;
                }
                QuestionAnalyze questionAnalyze = new QuestionAnalyze();
                questionAnalyze = QuestionAnalyze.builder()
                        .questionTitle(questionDocument.getTitle())
                        .surveyAnalyzeId(surveyAnalyze)
                        .build();

                questionAnalyzeRepository.save(questionAnalyze);

                // compare
                // [[[1.0], [1.0]], [[1.0], [1.0]]]
                List<Object> compareList = (List<Object>) compare.get(p);
                // [[1.0], [1.0]] -> compareList
                List<QuestionDocument> questionDocumentList = surveyDocumentRepository.findById(surveyDocumentId).get().getQuestionDocumentList();
                int size = questionDocumentList.size();
                int o=0;
                for (int k = 0; k < size; k++) {
                    if (questionDocumentList.get(k).getQuestionType() == 0) {
                        continue;
                    }
                    if (questionDocumentList.get(k).getTitle() == questionAnalyze.getQuestionTitle()) {
                        continue;
                    }
                    ArrayList<Double> temp = (ArrayList<Double>) compareList.get(o);
                    Double pValue = temp.get(0); // Assuming you want to retrieve the first Double value from the ArrayList

                    CompareAnalyze compareAnalyze = new CompareAnalyze();
                    compareAnalyze = CompareAnalyze.builder()
                            .questionAnalyzeId(questionAnalyze)
                            .pValue(pValue)
                            .questionTitle(questionDocumentList.get(k).getTitle())
                            .build();
                    o++;
                    compareAnalyzeRepository.save(compareAnalyze);
                }

                // chi
                // [[0.10247043485974942, 1.0], [1.0, 0.10247043485974942]]
                List<Object> chiList = (List<Object>) chi.get(p);
                // [0.10247043485974942, 1.0] -> chiList
                o=0;
                for (int k = 0; k < size; k++) {
                    if (questionDocumentList.get(k).getQuestionType() == 0) {
                        continue;
                    }
                    if (questionDocumentList.get(k).getTitle() == questionAnalyze.getQuestionTitle()) {
                        continue;
                    }
                    Double pValue = (Double) chiList.get(o);
                    ChiAnalyze chiAnalyze = new ChiAnalyze();
                    chiAnalyze = ChiAnalyze.builder()
                            .questionAnalyzeId(questionAnalyze)
                            .pValue(pValue)
                            .questionTitle(questionDocumentList.get(k).getTitle())
                            .build();
                    o++;
                    chiAnalyzeRepository.save(chiAnalyze);
                }

                //apriori
                for (int j = 0; j < apriori.size(); j++) {
                    // [['1', [0.66, '3'], [0.33, '4']]
                    List<Object> dataList = (List<Object>) apriori.get(j);
                    Long choiceId = Long.valueOf((Integer) dataList.get(0));
                    AprioriAnalyze aprioriAnalyze = new AprioriAnalyze();
                    aprioriAnalyze = AprioriAnalyze.builder()
                            .choiceId(choiceId)
                            .choiceTitle(choiceRepository.findById(choiceId).get().getTitle())
                            .questionTitle(questionDocumentRepository.findById(choiceRepository.findById(choiceId).get().getQuestion_id().getId()).get().getTitle())
                            .choiceAnalyzeList(new ArrayList<>())
                            .questionAnalyzeId(questionAnalyze)
                            .build();

                    aprioriAnalyzeRepository.save(aprioriAnalyze);
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
                                .aprioriAnalyzeId(aprioriAnalyze)
                                .choiceId(choiceId2)
                                .questionTitle(questionDocumentRepository.findById(choiceRepository.findById(choiceId2).get().getQuestion_id().getId()).get().getTitle())
                                .build();
                        choiceAnalyzeRepository.save(choiceAnalyze);
                    }
                    aprioriAnalyzeRepository.flush();
                }

                questionAnalyzeRepository.flush();
                p++;
            }

            // compare
            // 1번 문항에 대해 고른 응답의 비율과 2번 문항에 대해 고른 응답의 비율
            // [[[찬부식 - 찬부식],[찬부식 - 객관식]][[객관식-찬부식],[찬부식-객관식]]]
            // [[[1.0], [1.0]], [[1.0], [1.0]]]
            // compare의 size는 총 question(not 주관식)의 갯수
            for (int j = 0; j < compare.size(); j++) {
                // [[1.0], [1.0]]
                List<Object> dataList = (List<Object>) compare.get(j);
                //dataList size == questionList size
                for (int i = 0; i < dataList.size(); i++) {

                }
            }

            // Word Cloud 분석해서 이미지를 외부 서버에 저장 후 url를 가져와서 count 처럼 document에 저장
            // Word Cloud
            surveyAnalyzeRepository.flush();
        }catch (IOException e) {
            // 체크 예외 -> 런타임 커스텀 예외 변환 처리
            // python 파일 오류
            throw new InvalidPythonException(e);
        }
    }

    public void wordCloud(String stringId) {
        long surveyDocumentId = Long.parseLong(stringId);
        // 값 분리해서 Analyze DB에 저장
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();
        List<QuestionDocument> questionDocumentList = surveyDocument.getQuestionDocumentList();
        for (QuestionDocument questionDocument : questionDocumentList) {
            // 주관식 문항의 id로 그 주관식 문항에 대답한 questionAnswerList를 찾아옴
            List<QuestionAnswer> questionAnswersByCheckAnswerId = questionAnswerRepository.findQuestionAnswersByCheckAnswerId(questionDocument.getId());

            //wordCloud 분석
            ArrayList<String> answerList = new ArrayList<>();
            for (QuestionAnswer questionAnswer : questionAnswersByCheckAnswerId) {
                if (questionAnswer.getQuestionType() != 0) {
                    continue;
                }
                answerList.add(questionAnswer.getCheckAnswer());
            }
            log.info(String.valueOf(answerList));

            Resource[] resources = new Resource[0];
            try {
                resources = ResourcePatternUtils
                        .getResourcePatternResolver(new DefaultResourceLoader())
                        .getResources("classpath*:python/stopword.txt");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            log.info(String.valueOf(resources[0]));
            String substring = String.valueOf(resources[0]).substring(6, String.valueOf(resources[0]).length() -1);
            log.info(substring);

            List<String> stopwords = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(substring))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stopwords.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String filterWords = removeStopwords(answerList, stopwords);

            for (String s : Arrays.asList("\\[", "\\]", ",", "'")) {
                filterWords = filterWords.replaceAll(s, "");
            }
            log.info(filterWords);

            Map<String, Integer> wordCount = countWords(filterWords);
            // Sort the wordCount map in descending order of values
            List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(wordCount.entrySet());
            sortedList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // Print the sorted word counts
            log.info("Word Counts (Descending Order):");
            List<WordCloud> wordCloudList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : sortedList) {
                WordCloud wordCloud = new WordCloud();
                wordCloud.setQuestionDocument(questionDocument);
                wordCloud.setTitle(entry.getKey());
                wordCloud.setCount(entry.getValue());
                log.info(entry.getKey() + ": " + entry.getValue());
                wordCloudRepository.save(wordCloud);
                wordCloudList.add(wordCloud);
            }
            questionDocument.setWordCloudList(wordCloudList);
            questionDocumentRepository.flush();
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

    public SurveyDocument getSurveyDocument(Long surveyDocumentId) {
        return surveyDocumentRepository.findById(surveyDocumentId).get();
    }

    //count +1
    public void countChoice(Long choiceId) {
        Optional<Choice> findChoice = choiceRepository.findById(choiceId);
        if (findChoice.isPresent()) {
            //todo: querydsl로 변경
            findChoice.get().setCount(findChoice.get().getCount() + 1);
            choiceRepository.flush();
        }
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
                // 주관식 답변들 리스트
//                List<QuestionAnswer> questionAnswersByCheckAnswerId = questionAnswerRepository.findQuestionAnswersByCheckAnswerId(questionDocument.getId());
                //REST API GET questionAnswersByCheckAnswerId
                List<QuestionAnswer> questionAnswersByCheckAnswerId = getQuestionAnswersByCheckAnswerId(questionDocument.getId());
                for (QuestionAnswer questionAnswer : questionAnswersByCheckAnswerId) {
                    // 그 중에 주관식 답변만
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

            List<WordCloudDto> wordCloudDtos = new ArrayList<>();
            for (WordCloud wordCloud : questionDocument.getWordCloudList()) {
                WordCloudDto wordCloudDto = new WordCloudDto();
                wordCloudDto.setId(wordCloud.getId());
                wordCloudDto.setTitle(wordCloud.getTitle());
                wordCloudDto.setCount(wordCloud.getCount());

                wordCloudDtos.add(wordCloudDto);
            }
            questionDto.setWordCloudDtos(wordCloudDtos);

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
            questionDto.setQuestionTitle(questionAnalyze.getQuestionTitle());

            List<CompareAnalyzeDto> compareAnalyzeDtos = new ArrayList<>();
            for (CompareAnalyze compareAnalyze : questionAnalyze.getCompareAnalyzeList()) {
                CompareAnalyzeDto compareAnalyzeDto = new CompareAnalyzeDto();
                compareAnalyzeDto.setId(compareAnalyze.getId());
                compareAnalyzeDto.setPValue(compareAnalyze.getPValue());
                compareAnalyzeDto.setQuestionTitle(compareAnalyze.getQuestionTitle());

                compareAnalyzeDtos.add(compareAnalyzeDto);
            }
            questionDto.setCompareAnalyzeList(compareAnalyzeDtos);

            List<ChiAnalyzeDto> chiAnalyzeDtos = new ArrayList<>();
            for (ChiAnalyze chiAnalyze : questionAnalyze.getChiAnalyzeList()) {
                ChiAnalyzeDto chiAnalyzeDto = new ChiAnalyzeDto();
                chiAnalyzeDto.setId(chiAnalyze.getId());
                chiAnalyzeDto.setPValue(chiAnalyze.getPValue());
                chiAnalyzeDto.setQuestionTitle(chiAnalyze.getQuestionTitle());

                chiAnalyzeDtos.add(chiAnalyzeDto);
            }
            questionDto.setChiAnalyzeList(chiAnalyzeDtos);


            List<AprioriAnalyzeDto> aprioriAnalyzeDtos = new ArrayList<>();
            for (AprioriAnalyze aprioriAnalyze : questionAnalyze.getAprioriAnalyzeList()) {
                AprioriAnalyzeDto aprioriAnalyzeDto = new AprioriAnalyzeDto();
                aprioriAnalyzeDto.setId(aprioriAnalyze.getId());
                aprioriAnalyzeDto.setChoiceTitle(aprioriAnalyze.getChoiceTitle());
                aprioriAnalyzeDto.setQuestionTitle(aprioriAnalyze.getQuestionTitle());

                List<ChoiceAnalyzeDto> choiceDtos = new ArrayList<>();
                    for (ChoiceAnalyze choice : aprioriAnalyze.getChoiceAnalyzeList()) {
                        ChoiceAnalyzeDto choiceDto = new ChoiceAnalyzeDto();
                        choiceDto.setId(choice.getId());
                        choiceDto.setChoiceTitle(choice.getChoiceTitle());
                        choiceDto.setSupport(choice.getSupport());
                        choiceDto.setQuestionTitle(choice.getQuestionTitle());
                        choiceDtos.add(choiceDto);
                    }
                aprioriAnalyzeDto.setChoiceAnalyzeList(choiceDtos);
                aprioriAnalyzeDtos.add(aprioriAnalyzeDto);
            }
            questionDto.setAprioriAnalyzeList(aprioriAnalyzeDtos);

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
                .header("Authorization","NouNull")
                .bodyValue(String.valueOf(surveyDocumentId))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Process the response as needed
        System.out.println("Request: " + post);
    }

    private static List<QuestionAnswer> getQuestionAnswersByCheckAnswerId(Long questionDocumentId) {
        //REST API로 분석 시작 컨트롤러로 전달
        // Create a WebClient instance
        log.info("응답 db에서 주관식 리스트 가져오기");
        WebClient webClient = WebClient.create();

        // Define the API URL
        String apiUrl = "http://localhost:8080/api/question/list/"+ questionDocumentId;

        // Make a GET request to the API and retrieve the response
        String post = webClient.get()
                .uri(apiUrl)
                .header("Authorization","NotNull")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Process the response as needed
        System.out.println("Request: " + post);

        List<QuestionAnswer> questionAnswerList = new ArrayList<>();
        return questionAnswerList;
    }

    private static String removeStopwords(List<String> inputList, List<String> stopwords) {
        List<String> filteredList = new ArrayList<>();

        for (String inputString : inputList) {
            // Tokenize the input string
            String[] words = StringUtils.tokenizeToStringArray(inputString, " ");

            // Remove stopwords
            List<String> filteredWords = new ArrayList<>();
            for (String word : words) {
                // Convert word to lowercase for case-insensitive matching
                String lowercaseWord = word.toLowerCase();

                // Skip stopwords
                if (!contains(new List[]{stopwords}, lowercaseWord)) {
                    filteredWords.add(word);
                }
            }

            // Reconstruct the filtered string
            String filteredString = StringUtils.arrayToDelimitedString(filteredWords.toArray(), " ");
            filteredList.add(filteredString);
        }

        return filteredList.toString().trim();
    }

    public static Map<String, Integer> countWords(String text) {
        String[] words = text.split("\\s+");
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        return wordCount;
    }


    public Choice getChoice(Long id) {
        Optional<Choice> byId = choiceRepository.findById(id);
        return byId.get();
    }
    public QuestionDocument getQuestion(Long id) {
        Optional<QuestionDocument> byId = questionDocumentRepository.findById(id);
        return byId.get();
    }

    public QuestionDocument getQuestionByChoiceId(Long id) {
        return choiceRepository.findById(id).get().getQuestion_id();
    }

    public void setWordCloud(Long id, List<WordCloudDto> wordCloudDtos) {
        List<WordCloud> wordCloudList = new ArrayList<>();
        for (WordCloudDto wordCloudDto : wordCloudDtos) {
            WordCloud wordCloud = new WordCloud();
            wordCloud.setId(wordCloudDto.getId());
            wordCloud.setTitle(wordCloudDto.getTitle());
            wordCloud.setCount(wordCloudDto.getCount());
            wordCloud.setQuestionDocument(questionDocumentRepository.findById(id).get());
            wordCloudList.add(wordCloud);
        }
        wordCloudRepository.deleteAllByQuestionDocument(questionDocumentRepository.findById(id).get());
        questionDocumentRepository.findById(id).get().setWordCloudList(wordCloudList);
        questionDocumentRepository.flush();
    }
}
