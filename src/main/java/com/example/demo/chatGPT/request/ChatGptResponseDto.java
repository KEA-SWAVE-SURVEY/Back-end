package com.example.demo.chatGPT.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ChatGptResponseDto implements Serializable {

    private List<ChatGptChoice> choices;

    @Builder
    public ChatGptResponseDto(List<ChatGptChoice> choices) {
        this.choices = choices;
    }
}