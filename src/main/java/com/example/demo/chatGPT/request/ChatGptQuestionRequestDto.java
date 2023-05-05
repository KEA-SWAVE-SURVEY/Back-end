package com.example.demo.chatGPT.request;

import lombok.Getter;

import java.io.Serializable;
@Getter
public class ChatGptQuestionRequestDto implements Serializable {
    private String question;
}
