package com.example.demo.survey.response;

import com.example.demo.survey.request.ChoiceRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDetailDto {
    private Long id;
    private String title;
    private int questionType;
    private List<ChoiceDetailDto> choiceList;
    // getter, setter 생략
}
