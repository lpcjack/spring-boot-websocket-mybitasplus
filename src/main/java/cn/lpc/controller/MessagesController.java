package cn.lpc.controller;

import cn.lpc.mapper.MessagesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // 将RequestMapping注解放在类级别，指定根路径
@CrossOrigin(origins = "http://localhost:5173")
public class MessagesController {
    @Autowired
    private MessagesMapper messagesMapper;

    @PostMapping("/store")
    void Insert(String sender , String receiver ,String message ,String type){
        messagesMapper.insertMessage(sender , receiver , message ,type);
    }

}
