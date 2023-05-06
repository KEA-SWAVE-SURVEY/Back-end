package com.example.demo.survey.response;

import com.example.demo.survey.domain.SurveyDocument;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyAnalyzeDto {
    private Long id;
    private List<QuestionAnalyzeDto> questionAnalyzeList;
}
