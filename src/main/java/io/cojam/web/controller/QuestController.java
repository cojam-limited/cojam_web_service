package io.cojam.web.controller;

import com.klaytn.caver.utils.Convert;
import io.cojam.web.account.Account;
import io.cojam.web.constant.QuestCode;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.domain.*;
import io.cojam.web.service.QuestService;
import io.cojam.web.service.SeasonService;
import io.cojam.web.service.SequenceService;
import io.cojam.web.service.contract.ContractApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Controller
@RequestMapping(value = "/user/quest")
public class QuestController {

    @Autowired
    SeasonService seasonService;

    @Autowired
    QuestService questService;

    @Autowired
    ContractApplicationService contractApplicationService;

    @Autowired
    SequenceService sequenceService;

    @GetMapping
    public String quest(Model model
            , @AuthenticationPrincipal Account account) {
        model.addAttribute("seasonCategoryList",seasonService.getSeasonCategoryList());
        return "thymeleaf/page/quest/list";
    }

    @RequestMapping(value = "/seasonInfo",method = RequestMethod.GET)
    public String questList(Model model
            , @AuthenticationPrincipal Account account) {

        Season seasonInfo = seasonService.getSeasonInfo();
        model.addAttribute("seasonInfo", seasonInfo);
        if(seasonInfo!=null){
            List<SeasonCategory> list = seasonService.getSeasonCategoryCntList(seasonInfo.getSeasonKey());
            model.addAttribute("list",list);
        }else {
            model.addAttribute("list",null);
        }


        return "thymeleaf/fragment/popup :: #questSeaonCntInfo";
    }

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String questList(Model model
            , @RequestParam(defaultValue = "1") int page
            , Quest quest
            , @AuthenticationPrincipal Account account) {
        quest.setQuestStatus(QuestCode.QUEST_STATUS_APPROVE);
        if("HISTORY".equals(quest.getSeasonCategoryKey())){
            quest.setQuestStatus(QuestCode.QUEST_STATUS_SUCCESS);
            quest.setSeasonCategoryKey(null);
        }
        int totalListCnt = questService.getQuestListUserCnt(quest);
        Pagination pagination = new Pagination(totalListCnt, page,5,15);
        quest.setStartIndex(pagination.getStartIndex());
        quest.setPageSize(pagination.getPageSize());

        List<Quest> questList = questService.getQuestListUser(quest);

        model.addAttribute("questList", questList);
        model.addAttribute("pagination", pagination);
        model.addAttribute("totalListCnt", totalListCnt);
        return "thymeleaf/page/quest/list :: #questList";
    }

    @GetMapping
    @RequestMapping(value = "/view")
    public String view(Model model, @AuthenticationPrincipal Account account,String idx) {
        Quest quest = new Quest();
        quest.setQuestKey(idx);
        quest.setQuestStatus(QuestCode.QUEST_STATUS_APPROVE);
        model.addAttribute("detail", questService.getQuestDetailUser(quest));
        return "thymeleaf/page/quest/view";
    }

    @ResponseBody
    @RequestMapping(value = "/register" , method = RequestMethod.POST)
    public ResponseDataDTO register(
            Quest quest
            , @AuthenticationPrincipal Account account
            , @RequestParam(value = "file",required = false) MultipartFile file) throws Exception {
        contractApplicationService.initMaster();
        return questService.saveQuest(quest,file,account);
    }


    @ResponseBody
    @RequestMapping(value = "/betting" , method = RequestMethod.POST)
    public ResponseDataDTO betting(
            @Valid Betting betting
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.betting(betting,account);
    }

    @ResponseBody
    @RequestMapping(value = "/bettingList" , method = RequestMethod.POST)
    public ResponseDataDTO bettingList(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();
        response.setCheck(true);
        Betting betting = new Betting();
        betting.setQuestKey(questKey);
        response.setItem(questService.getBettingChartList(betting));
        return response;
    }


    @GetMapping
    @RequestMapping(value = "/bettingTotalList")
    public String bettingTotalList(Model model,Betting betting, @AuthenticationPrincipal Account account,String idx) {
        model.addAttribute("bettingList", questService.getBettingList(betting));
        return "thymeleaf/page/quest/view :: #bettingTotalList";
    }

    @ResponseBody
    @RequestMapping(value = "/successBetting" , method = RequestMethod.POST)
    public ResponseDataDTO successBetting(
            @Valid String bettingKey
            , @AuthenticationPrincipal Account account) throws Exception {
        Betting betting = new Betting();
        betting.setBettingKey(bettingKey);
        betting.setMemberKey(account.getMemberKey());
        return questService.successBetting(betting,account);
    }


    @ResponseBody
    @RequestMapping(value = "/rewardInfo" , method = RequestMethod.POST)
    public ResponseDataDTO rewardInfo(
            @NotEmpty @NotNull String bettingKey
            , @AuthenticationPrincipal Account account) throws Exception {
        Betting betting = new Betting();
        betting.setBettingKey(bettingKey);
        betting.setMemberKey(account.getMemberKey());
        return questService.rewardInfo(betting,account);
    }

    @ResponseBody
    @RequestMapping(value = "/noRewardInfo" , method = RequestMethod.POST)
    public ResponseDataDTO noRewardInfo(
            @NotEmpty @NotNull String bettingKey
            , @AuthenticationPrincipal Account account) throws Exception {
        Betting betting = new Betting();
        betting.setBettingKey(bettingKey);
        betting.setMemberKey(account.getMemberKey());
        return questService.noRewardInfo(betting,account);
    }


}
