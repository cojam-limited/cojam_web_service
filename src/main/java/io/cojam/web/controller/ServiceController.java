package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/user/community")
public class ServiceController {

    @Autowired
    QuestionService questionService;

    @GetMapping
    public String serviceList() {

        return "thymeleaf/page/service/index";
    }


    @RequestMapping(value = "/question",method = RequestMethod.POST)
    @Cacheable(value = "question" ,cacheManager = "userCacheManager")
    public String question(Model model) {
        model.addAttribute("questionList",questionService.getHomeQuestionList());
        return "thymeleaf/page/service/index :: #questionList";
    }
}
