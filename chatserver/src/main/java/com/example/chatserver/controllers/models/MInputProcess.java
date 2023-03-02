package com.example.chatserver.controllers.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Builder @Getter @Setter
public class MInputProcess {

    private String senderName;
    private String receiverName;
    private String content;
    private String date;
    private Status status;
    private MultipartFile file;

}
