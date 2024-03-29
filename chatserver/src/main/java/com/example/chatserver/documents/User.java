package com.example.chatserver.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    private String id;

    private String name;

    private Boolean isLogged;

    private String password;

}
