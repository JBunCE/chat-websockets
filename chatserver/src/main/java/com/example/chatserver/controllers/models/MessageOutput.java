package com.example.chatserver.controllers.models;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class MessageOutput {

    private String senderName;
    private String receiverName;
    private String content;
    private String date;
    private Status status;
    private String fileUrl;

}
