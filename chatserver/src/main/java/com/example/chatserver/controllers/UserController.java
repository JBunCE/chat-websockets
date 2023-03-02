package com.example.chatserver.controllers;

import com.example.chatserver.controllers.dtos.BaseResponse;
import com.example.chatserver.controllers.dtos.BaseUserRequest;
import com.example.chatserver.services.intefaces.IUserServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserServices service;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody BaseUserRequest request){
        BaseResponse response = service.login(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/reg")
    public ResponseEntity<BaseResponse> register(@RequestBody BaseUserRequest request){
        BaseResponse response = service.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
