package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.Email;
import io.cojam.web.domain.Pagination;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.domain.RewardInfo;
import io.cojam.web.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/cms/reward")
public class RewardController {

    @Autowired
    RewardService rewardService;

    @GetMapping
    public String reward(Model model
            , @AuthenticationPrincipal Account account) {
        return "thymeleaf/page/cms/reward/list";
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public String getList(Model model){

        model.addAttribute("list",rewardService.getRewardInfoList());

        return "thymeleaf/page/cms/reward/list :: #boardList";
    }

    @ResponseBody
    @PostMapping(value = "/update")
    public ResponseDataDTO rewardUpdate(Model model
            , @Valid RewardInfo rewardInfo
            , @AuthenticationPrincipal Account account) {

        return rewardService.updateRewardInfo(rewardInfo);
    }
}
