package com.example.demo.survey.response;

import com.example.demo.survey.request.QuestionRequestDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class SurveyResponseDto {
    String title;
    String description;
    int type;
    List<QuestionResponseDto> questionResponse;

//    @ConstructorProperties({"title", "description", "type", "questionRequest"})
    @Builder
    public SurveyResponseDto(String title, String description, int type, List<QuestionResponseDto> questionResponse) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.questionResponse = questionResponse;
    }
}
