package com.example.chatserver.services;

import com.example.chatserver.controllers.models.MInputProcess;
import com.example.chatserver.controllers.models.MessagePrePross;
import com.example.chatserver.controllers.models.MessageOutput;
import com.example.chatserver.services.intefaces.IAWSComponent;
import com.example.chatserver.services.intefaces.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired private IAWSComponent component;

    @Override
    public MessageOutput from(MInputProcess input) {

        if(input.getFile() != null){
            String fileUrl = component.uploadFile(input.getFile());
            return MessageOutput.builder()
                    .receiverName(input.getReceiverName())
                    .status(input.getStatus())
                    .date(input.getDate())
                    .senderName(input.getSenderName())
                    .content(input.getContent())
                    .fileUrl(fileUrl).build();
        }

        return MessageOutput.builder()
                .receiverName(input.getReceiverName())
                .status(input.getStatus())
                .date(input.getDate())
                .senderName(input.getSenderName())
                .content(input.getContent())
                .fileUrl(null).build();
    }
}
