package com.soundseeker.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IS3Service {
    String uploadFile(MultipartFile file) throws IOException;
}
