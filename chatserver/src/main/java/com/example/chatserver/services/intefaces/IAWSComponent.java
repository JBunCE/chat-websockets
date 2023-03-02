package com.example.chatserver.services.intefaces;

import org.springframework.web.multipart.MultipartFile;

public interface IAWSComponent {
    String uploadFile(MultipartFile file);
}
