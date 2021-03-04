package io.cojam.web.controller;

import io.cojam.web.account.Account;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user/service")
public class ServiceController {

    @GetMapping
    public String serviceList() {

        return "thymeleaf/page/service/index";
    }
}
