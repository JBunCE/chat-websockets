package com.example.chatserver.controllers.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class BaseUserRequest {

    String name;
    String password;

}
