package com.familymedical.controller;

import com.familymedical.common.Result;
import com.familymedical.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final OssService ossService;

    @PostMapping("/upload")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = ossService.uploadImage(file);
        return Result.success(url);
    }
}
