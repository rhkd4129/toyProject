package com.toyproject.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String hello(){
        System.out.println("hello_home");
        return "index";  // Thymeleaf는 확장자 없이 파일명만
    }


}
