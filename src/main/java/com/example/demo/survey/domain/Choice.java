package com.example.demo.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Choice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "questionId")
    private QuestionDocument questionDocumentId;
    private String content;

    @Builder
    public Choice(Long id, String content) {
        this.content = content;
    }
}
