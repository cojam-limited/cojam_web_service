package io.cojam.web.controller;

import io.cojam.web.domain.Season;
import io.cojam.web.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SeasonController {

    @Autowired
    SeasonService seasonService;

    @RequestMapping(value = "/cms/season" , method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("seasonList",seasonService.getSeasonList(new Season()));
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
