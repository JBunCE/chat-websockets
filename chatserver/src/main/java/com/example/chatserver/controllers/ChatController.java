package com.example.chatserver.controllers;

import com.example.chatserver.controllers.models.FileMessage;
import com.example.chatserver.controllers.models.MInputProcess;
import com.example.chatserver.controllers.models.MessagePrePross;
import com.example.chatserver.controllers.models.MessageOutput;
import com.example.chatserver.services.intefaces.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IMessageService messageService;

    @MessageMapping("/messageInput")
    @SendTo("/chatroom/public")
    private MessageOutput receivePublicMessage(@Payload MessagePrePross messageInput){

        return messageService.from(mInputProcess(messageInput));
    }

    @MessageMapping("/private-messageInput")
    private MessageOutput receiverPrivateMessage(@Payload MessagePrePross messageInput){
        MessageOutput messageOutput = messageService.from(mInputProcess(messageInput));
        messagingTemplate.convertAndSendToUser(messageOutput.getReceiverName(), "/private", messageOutput);
        return messageOutput;
    }

    private MInputProcess mInputProcess(MessagePrePross input){
        if(input.getFile() != null){
            String base64Image = input.getFile();
            byte[] imageBytes = Base64.decodeBase64(base64Image);
            String filename = input.getFileName();
            String contentType = "image/jpeg";

            MultipartFile multipartFile = FileMessage.builder()
                    .originalFilename(filename)
                    .name("filename")
                    .content(imageBytes)
                    .contentType("file").build();

            return MInputProcess.builder()
                    .receiverName(input.getReceiverName())
                    .content(input.getContent())
                    .status(input.getStatus())
                    .file(multipartFile)
                    .senderName(input.getSenderName())
                    .date(input.getDate())
                    .status(input.getStatus()).build();
        }
        return MInputProcess.builder()
                .receiverName(input.getReceiverName())
                .content(input.getContent())
                .status(input.getStatus())
                .file(null)
                .senderName(input.getSenderName())
                .date(input.getDate())
                .status(input.getStatus()).build();
    }

}
