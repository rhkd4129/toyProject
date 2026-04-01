package com.toyproject.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    public String hello(){
        System.out.println("hji");
        return "index.html";
    }


}
