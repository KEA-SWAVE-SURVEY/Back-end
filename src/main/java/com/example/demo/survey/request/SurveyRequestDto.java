package com.example.demo.survey.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class SurveyRequestDto {
    String title;
    String description;
    int type;
    List<QuestionRequestDto> questionRequest;

//    @ConstructorProperties({"title", "description", "type", "questionRequest"})
    @Builder
    public SurveyRequestDto(String title, String description, int type, List<QuestionRequestDto> questionRequest) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.questionRequest = questionRequest;
    }
}
