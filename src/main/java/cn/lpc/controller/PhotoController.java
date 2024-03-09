package cn.lpc.controller;

import org.springframework.beans.factory.annotation.Value;
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
    public String returnImg(@RequestBody MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        System.out.println(originalFilename);
        int index = 0;
        if (originalFilename != null) {
            index = originalFilename.indexOf(".");
        }
        String formatFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")).toString();
        String newFileName = null;
        if (originalFilename != null) {
            newFileName = formatFileName + originalFilename.substring(index);
        }
        try {
            //将文件保存指定目录
            file.transferTo(new File(imgUrl + newFileName));
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "failure";

    }
}