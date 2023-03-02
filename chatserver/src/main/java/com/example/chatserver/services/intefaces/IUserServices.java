package com.example.chatserver.services.intefaces;

import com.example.chatserver.controllers.dtos.BaseResponse;
import com.example.chatserver.controllers.dtos.BaseUserRequest;

public interface IUserServices {

    BaseResponse create(BaseUserRequest request);
    BaseResponse login(BaseUserRequest request);
    void exit(String userName);

}
