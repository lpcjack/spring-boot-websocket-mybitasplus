package cn.lpc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PhotoController {
    @Value("${file.upload-path}")
    private String imgUrl;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            // 文件名为空，输出日志并返回失败
            System.out.println("文件名为空");
            return ResponseEntity.badRequest().body("文件名为空");
        }

        int index = originalFilename.lastIndexOf(".");
        String formatFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm"));
        String newFileName = formatFileName + originalFilename.substring(index);

        try {
            // 将文件保存到指定目录
            file.transferTo(new File(imgUrl + newFileName));
            String imagePath = "http://localhost:8080/static/" + newFileName;
            return ResponseEntity.ok(imagePath);
        } catch (Exception e) {
            // 捕获异常并输出日志
            e.printStackTrace();
            return ResponseEntity.status(500).body("图片上传失败");
        }
    }
}
