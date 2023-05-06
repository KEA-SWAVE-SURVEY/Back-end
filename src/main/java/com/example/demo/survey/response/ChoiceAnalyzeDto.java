package com.example.demo.survey.response;

import com.example.demo.survey.request.QuestionRequestDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceAnalyzeDto {
    private Long id;
    private double support;
    private String questionTitle;
    private String choiceTitle;
}
