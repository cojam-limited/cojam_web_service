package io.cojam.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SeasonController {

    @RequestMapping(value = "/cms/season" , method = RequestMethod.GET)
    public String list() {

        return "thymeleaf/page/cms/season/list";
    }


    @RequestMapping(value = "/cms/season/register" , method = RequestMethod.GET)
    public String register() {

        return "thymeleaf/page/cms/season/register";
    }

    @RequestMapping(value = "/cms/season/view" , method = RequestMethod.GET)
    public String view() {

        return "thymeleaf/page/cms/season/view";
    }
}
