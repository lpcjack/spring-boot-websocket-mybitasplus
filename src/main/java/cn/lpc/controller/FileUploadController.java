//package cn.lpc.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:5173")
//@RequestMapping("/api/upload")
//public class FileUploadController {
//    @PostMapping
//    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
//        try {
//            // 获取上传的文件信息
//            String fileName = file.getOriginalFilename();
//            byte[] fileContent = file.getBytes();
//
//            // 在这里可以处理上传的文件，比如保存到服务器文件系统或数据库中
//            // 这个示例只是返回文件名和成功信息
//            return ResponseEntity.ok("File '" + fileName + "' uploaded successfully.后端接收文件成功");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Failed to upload file.");
//        }
//    }
//}
