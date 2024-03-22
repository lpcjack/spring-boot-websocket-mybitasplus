package cn.lpc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private String filePath = "D:\\nginxstore\\";
//    private String filePath = "D:\\idea document\\InstantMessageUserInterfaceService\\src\\main\\resources\\static\\";
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将前者的路径映射到后者的static路径
        registry.addResourceHandler("/static/**").addResourceLocations("file:" + filePath);
        System.out.println("静态资源获取");
    }

}
