package com.example.demo.survey.response;

import com.example.demo.survey.request.QuestionRequestDto;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SurveyManageDto {
    private boolean acceptResponse;
    private String startDate;
    private String deadline;
    private String url;

    @Builder
    public SurveyManageDto(boolean acceptResponse, String startDate, String deadline, String url) {
        this.acceptResponse = acceptResponse;
        this.startDate = startDate;
        this.deadline = deadline;
        this.url = url;
    }
}
