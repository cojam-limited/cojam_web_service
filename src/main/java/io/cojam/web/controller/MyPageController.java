package io.cojam.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user/mypage")
public class MyPageController {
    @GetMapping
    public String index(){
        return "thymeleaf/page/myPage/index";
    }
}
