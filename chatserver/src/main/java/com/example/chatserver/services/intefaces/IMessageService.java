package com.example.chatserver.services.intefaces;

import com.example.chatserver.controllers.models.MInputProcess;
import com.example.chatserver.controllers.models.MessagePrePross;
import com.example.chatserver.controllers.models.MessageOutput;

public interface IMessageService {
    MessageOutput from(MInputProcess input);
}
