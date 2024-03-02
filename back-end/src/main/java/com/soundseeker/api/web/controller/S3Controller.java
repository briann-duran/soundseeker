package com.soundseeker.api.web.controller;

import com.soundseeker.api.service.IS3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class S3Controller {

    private final IS3Service s3Service;

    public S3Controller(IS3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/uploadImg")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return this.s3Service.uploadFile(file);
    }
}
