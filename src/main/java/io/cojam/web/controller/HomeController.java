package io.cojam.web.controller;


import com.fasterxml.jackson.databind.util.JSONPObject;
import io.cojam.web.domain.Popup;
import io.cojam.web.domain.Season;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.service.WalletApiService;
import io.cojam.web.service.*;
import io.cojam.web.service.contract.ContractApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigInteger;

@Controller
public class HomeController {

    @Autowired
    BoardService boardService;

    @Autowired
    QuestionService questionService;

    @Autowired
    SeasonService seasonService;

    @Autowired
    PopupService popupService;

    @Autowired
    QuestService questService;

    @Autowired
    ContractApplicationService contractApplicationService;

    @RequestMapping(value = "/user/home" , method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("mainPopupList",popupService.getMainPopupList(new Popup()));
        return "thymeleaf/page/home/index";
    }

    @RequestMapping(value = "/user/home/notice",method = RequestMethod.POST)
    public String notice(Model model) {
        model.addAttribute("boardList",boardService.getHomeBoardList(null));
        return "thymeleaf/page/home/index :: #noticeTop3";
    }



    @RequestMapping(value = "/user/home/seasonInfo",method = RequestMethod.POST)
    @Cacheable(value = "seasonInfo" ,cacheManager = "userCacheManager")
    public String seasonInfo(Model model) {
        Season seasonInfo = seasonService.getSeasonInfo();
        model.addAttribute("seasonInfo",seasonInfo);
        return "thymeleaf/page/home/index :: #seasonInfo";
    }

    @RequestMapping(value = "/cms/home" , method = RequestMethod.GET)
    public String homeCms(Model model) {

        return "thymeleaf/page/cms/home/index";
    }

    @RequestMapping(value = "/user/home/questInfo",method = RequestMethod.POST)
    public String questInfo(Model model) {

        model.addAttribute("questMainList",questService.getHomeQuestList(null));
        return "thymeleaf/page/home/index :: #questMainList";
    }
}
