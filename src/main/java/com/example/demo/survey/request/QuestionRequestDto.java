package com.example.demo.survey.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequestDto {
    private String title;
    private int type;
    private List<ChoiceRequestDto> choiceList;

    /*
    HttpMessageConverter Error code : no Creators, like default constructor, exist
     */
    public QuestionRequestDto() {};

    // 주관식, 찬부식
    public QuestionRequestDto(String title, int type) {
        this.type = type;
        this.title = title;
    }

    // 객관식
    public QuestionRequestDto(String title, int type, List<ChoiceRequestDto> choiceList) {
        this.title = title;
        this.choiceList = choiceList;
        this.type = type;
    }
}
