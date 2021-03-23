package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.Board;
import io.cojam.web.domain.Quest;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.QuestService;
import io.cojam.web.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(value = "/user/quest")
public class QuestController {

    @Autowired
    SeasonService seasonService;

    @Autowired
    QuestService questService;

    @GetMapping
    public String questList(Model model, @AuthenticationPrincipal Account account) {
        model.addAttribute("seasonCategoryList",seasonService.getSeasonCategoryList());
        return "thymeleaf/page/quest/list";
    }

    @GetMapping
    @RequestMapping(value = "/view")
    public String view(Model model, @AuthenticationPrincipal Account account) {
        model.addAttribute("seasonCategoryList",seasonService.getSeasonCategoryList());
        return "thymeleaf/page/quest/view";
    }

    @ResponseBody
    @RequestMapping(value = "/register" , method = RequestMethod.POST)
    public ResponseDataDTO register(
            Quest quest
            , @AuthenticationPrincipal Account account
            , @RequestParam(value = "file",required = false) MultipartFile file) throws Exception {
        return questService.saveQuest(quest,file,account);
    }

}
