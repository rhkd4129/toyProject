package com.toyproject.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Value("${spring.servlet.multipart.location}")
//    private String uploadPath;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")//보낼수있는 헤더
                .exposedHeaders("Authorization", "Content-Disposition")  //받을수잇는헤더
                .allowCredentials(true)
                .maxAge(3600);
    }
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/files/**")
////                .addResourceLocations("file:upload/");
//                .addResourceLocations("file:///C:/Server/IMG/");
//    }

}
//addResourceHandler("/files/**"): "/files/" 로 시작하는 모든 URL 경로에 대해 리소스 핸들러를 추가.
//addResourceLocations("file:///"+uploadPath): 실제 파일 시스템의 특정 경로(uploadPath)에서 리소스를 제공.



//uploadPath가 "/home/user/uploads"라면
//"http://yourwebsite.com/files/example.pdf" 요청은
//"/home/user/uploads/example.pdf" 파일을 찾아 제공
