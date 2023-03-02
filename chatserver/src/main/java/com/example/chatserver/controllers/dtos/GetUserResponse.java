package com.example.chatserver.controllers.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class GetUserResponse{
    String id;
    String name;
    String password;
}
