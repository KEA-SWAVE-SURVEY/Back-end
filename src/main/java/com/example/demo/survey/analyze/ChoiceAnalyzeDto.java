package com.example.demo.survey.analyze;

import com.example.demo.survey.request.QuestionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceAnalyzeDto {
    private Long id;
    private String choiceTitle;
}
