package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.*;
import io.cojam.web.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    public String register(Model model) {
        List<SeasonCategory> list =seasonService.getSeasonCategoryList();
        model.addAttribute("ctgrList",list);
        return "thymeleaf/page/cms/season/register";
    }

    @RequestMapping(value = "/cms/season/view" , method = RequestMethod.GET)
    public String view(@NotEmpty @NotNull String idx,Model model) {
        Season detail = seasonService.getSeasonDetail(idx);
        model.addAttribute("detail",detail);
        if(detail != null){
            model.addAttribute("ctgrList",seasonService.getSeasonCategoryMappingList(idx));
        }


        return "thymeleaf/page/cms/season/view";
    }


    @ResponseBody
    @RequestMapping(value = "/cms/season/save" , method = RequestMethod.POST)
    public ResponseDataDTO save(
            @Valid SeasonInput season
            ,HttpServletRequest request
            ,@AuthenticationPrincipal Account account) {

        return seasonService.saveSeason(season,request,account);
    }

    @ResponseBody
    @RequestMapping(value = "/cms/season/update" , method = RequestMethod.POST)
    public ResponseDataDTO update(
            @Valid SeasonInput season
            ,HttpServletRequest request
            ,@AuthenticationPrincipal Account account) {

        return seasonService.updateSeason(season,request,account);
    }



}
