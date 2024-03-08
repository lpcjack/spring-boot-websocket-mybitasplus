package cn.lpc.controller;

import cn.lpc.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class PhotoController {
    @Value("${file.upload-path}")
    private String imgUrl;

    @PostMapping("/upload")
    public String returnImg(@RequestBody MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.indexOf(".");
        String formatFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")).toString();
        String newFileName = formatFileName + originalFilename.substring(index);
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