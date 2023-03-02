package com.example.chatserver.controllers.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor @NoArgsConstructor @Builder
public class FileMessage implements MultipartFile {

    private byte[] content;
    private String name;
    private String originalFilename;
    private String contentType;


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content != null ? content.length : 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        throw new UnsupportedOperationException("transferTo() is not supported");
    }
}
