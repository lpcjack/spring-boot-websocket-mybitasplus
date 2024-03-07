package cn.lpc.controller;

import cn.lpc.bean.CodeMsg;
import cn.lpc.dto.ResponseDTO;
import cn.lpc.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;

/**
 * 统一图片查看器
 * @author 82320
 *
 */
@RequestMapping("/photo")
@RestController
public class PhotoController {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${yjq.upload.file.path}")
    private String uploadPhotoPath;//文件保存位置

    private static final Logger logger = LoggerFactory.getLogger(PhotoController.class);

    /**
     * 系统统一的图片查看方法
     * @param filename
     * @return
     */
    @RequestMapping(value="/view")
    public ResponseEntity<?> viewPhoto(@RequestParam(name="filename",required=true)String filename){
        Resource resource = resourceLoader.getResource("file:" + uploadPhotoPath + filename);
        try {
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * 上传图片统一处理
     * @param photo
     * @param request
     * @return
     */
    @PostMapping(value="/upload")
    public ResponseDTO<String> uploadPhoto(MultipartFile photo, HttpServletRequest request){
        if(photo == null){
            return ResponseDTO.errorByMsg(CodeMsg.PHOTO_EMPTY);
        }
        //检查上传文件大小 不能超过1MB
        if(photo.getSize() > 1024*1024) {
            return ResponseDTO.errorByMsg(CodeMsg.PHOTO_SURPASS_MAX_SIZE);
        }
        //获取文件后缀
        String suffix = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".")+1,photo.getOriginalFilename().length());
        if(!CommonUtil.isPhoto(suffix)){
            return ResponseDTO.errorByMsg(CodeMsg.PHOTO_FORMAT_NOT_CORRECT);
        }
        String path = request.getContextPath();
        String savePath = uploadPhotoPath + CommonUtil.getFormatterDate(new Date(), "yyyyMMdd") + "\\";
        File savePathFile = new File(savePath);
        if(!savePathFile.exists()){
            //若不存在改目录，则创建目录
            savePathFile.mkdir();
        }
        String filename = new Date().getTime()+"."+suffix;
        logger.info("保存图片的路径:{}",savePath + filename);
        try {
            //将文件保存至指定目录
            photo.transferTo(new File(savePath + filename));
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseDTO.errorByMsg(CodeMsg.SAVE_FILE_EXCEPTION);
        }
        String filepath = CommonUtil.getFormatterDate(new Date(), "yyyyMMdd") + "/" + filename;
        return ResponseDTO.successByMsg(filepath, "图片上传成功！");
    }
}
