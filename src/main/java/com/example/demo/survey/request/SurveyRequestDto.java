package com.example.demo.survey.request;

import com.example.demo.survey.domain.Question;
import lombok.Builder;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Set;

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
