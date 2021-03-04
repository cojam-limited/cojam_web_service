package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user/quest")
public class QuestController {

    @Autowired
    SeasonService seasonService;

    @GetMapping
    public String questList(Model model, @AuthenticationPrincipal Account account) {
        model.addAttribute("seasonCategoryList",seasonService.getSeasonCategoryList());
        return "thymeleaf/page/quest/list";
    }



}
