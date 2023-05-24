package com.example.demo.survey.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class QuestionAnalyze {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_analyze_id")
    private Long id;
    @Column(name = "question_title")
    private String questionTitle;
    @Column(name = "word_cloud")
    private String wordCloud;

    @OneToMany(mappedBy = "questionAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore //순환참조 방지
    @Column(name = "apriori_list")
    private List<AprioriAnalyze> aprioriAnalyzeList;

    @OneToMany(mappedBy = "questionAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore //순환참조 방지
    @Column(name = "chi_list")
    private List<ChiAnalyze> chiAnalyzeList;

    @OneToMany(mappedBy = "questionAnalyzeId", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore //순환참조 방지
    @Column(name = "compare_list")
    private List<CompareAnalyze> compareAnalyzeList;

    @ManyToOne
    @JsonIgnore // 순환참조 방지
    @JoinColumn(name = "survey_analyze_id")
    private SurveyAnalyze surveyAnalyzeId;

}
