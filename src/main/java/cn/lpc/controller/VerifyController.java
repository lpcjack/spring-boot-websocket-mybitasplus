package cn.lpc.controller;

import cn.lpc.util.VerifyCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 用于生成验证码
 */
@Slf4j
@RestController
@RequestMapping("/api") // 将RequestMapping注解放在类级别，指定根路径
@CrossOrigin(origins = "http://localhost:5173")
public class VerifyController {
    @RequestMapping("/verify")
    public void Verify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        VerifyCode verifyCode = new VerifyCode();
        BufferedImage img = verifyCode.createImage();

        // 获取验证码，存入session
        String txt = verifyCode.getText();
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("verifyCode", txt);
        log.info("存储的验证码为："+httpSession.getAttribute("verifyCode"));
        log.info("session的ID为："+httpServletRequest.getSession().getId());
        //验证码
        System.err.println(verifyCode. getText());
        //验证码图片格式
        ImageIO.write(img,"jpg",httpServletResponse.getOutputStream());

    }


}
