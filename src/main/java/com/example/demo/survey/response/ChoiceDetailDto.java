package com.example.demo.survey.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceDetailDto {
    private Long id;
    private String title;
    // getter, setter 생략
}
