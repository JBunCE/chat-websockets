package com.example.chatserver.controllers.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter @Setter @Builder
public class BaseResponse {
    Object data;
    String message;
    Boolean success;
    HttpStatus status;
}
