package io.cojam.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user/about")
public class AboutController {

    @GetMapping
    public String index() {
        return "thymeleaf/page/about/index";
    }
}
