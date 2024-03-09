package cn.lpc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class FileUploadController {
    @Value("${file.upload-path}")
    private String imgUrl;

    @PostMapping("/uploadfile")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            // 文件为空，输出日志并返回失败
            System.out.println("文件为空");
            return ResponseEntity.badRequest().body("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String formatFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        String newFileName = formatFileName + originalFilename.substring(index);

        try {
            // 将文件保存到指定目录
            file.transferTo(new File(imgUrl + newFileName));
            String imagePath = "http://localhost:8080/static/" + newFileName;
            return ResponseEntity.ok(imagePath);
        } catch (IOException e) {
            // 捕获异常并输出日志
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上传失败");
        }
    }
}
