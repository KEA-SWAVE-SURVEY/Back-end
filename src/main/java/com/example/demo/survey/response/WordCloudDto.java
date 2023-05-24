package com.example.demo.survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordCloudDto {
    private Long id;
    private String title;
    private int count;
}
