package com.example.chatserver.controllers.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MessagePrePross {

    private String senderName;
    private String receiverName;
    private String content;
    private String date;
    private Status status;
    private String file;
    private String fileName;

}
