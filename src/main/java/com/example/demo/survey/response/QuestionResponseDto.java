package com.example.demo.survey.response;

import com.example.demo.survey.request.ChoiceRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class QuestionResponseDto {
    private String title;
    private int type;
    private String answer;

    /*
    HttpMessageConverter Error code : no Creators, like default constructor, exist
     */
    public QuestionResponseDto() {};

    // 주관식, 찬부식 + 객관식 -> 나중에 서비스에서 구분필요
    public QuestionResponseDto(String title, int type, String answer) {
        this.type = type;
        this.title = title;
        this.answer = answer;
    }
}
