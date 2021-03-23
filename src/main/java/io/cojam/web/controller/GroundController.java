package io.cojam.web.controller;


import io.cojam.web.constant.QuestCode;
import io.cojam.web.domain.Pagination;
import io.cojam.web.domain.Popup;
import io.cojam.web.domain.Quest;
import io.cojam.web.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/cms/ground")
public class GroundController {

    @Autowired
    QuestService questService;

    @RequestMapping(value = "/adjourn" , method = RequestMethod.GET)
    public String adjourn() {

        return "thymeleaf/page/cms/ground/adjourn";
    }

    @RequestMapping(value = "/adjourn/list" , method = RequestMethod.GET)
    public String adjournList(Model model , @RequestParam(defaultValue = "1") int page , Quest quest) {
        quest.setQuestStatus(QuestCode.QUEST_STATUS_ADJOURN);
        // 총 게시물 수
        int totalListCnt = questService.getQuestListAdminCnt(quest);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        quest.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        quest.setPageSize(pagination.getPageSize());
        List<Quest> popupList = questService.getQuestListAdmin(quest);

        model.addAttribute("questList", popupList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/ground/adjourn :: #questList";
    }

    @RequestMapping(value = "/approve" , method = RequestMethod.GET)
    public String approve() {

        return "thymeleaf/page/cms/ground/approve";
    }

    @RequestMapping(value = "/approve/list" , method = RequestMethod.GET)
    public String approveList(Model model , @RequestParam(defaultValue = "1") int page , Quest quest) {
        quest.setQuestStatus(QuestCode.QUEST_STATUS_APPROVE);
        // 총 게시물 수
        int totalListCnt = questService.getQuestListAdminCnt(quest);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        quest.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        quest.setPageSize(pagination.getPageSize());
        List<Quest> popupList = questService.getQuestListAdmin(quest);

        model.addAttribute("questList", popupList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/ground/approve :: #questList";
    }

    @RequestMapping(value = "/invalid" , method = RequestMethod.GET)
    public String invalid() {

        return "thymeleaf/page/cms/ground/invalid";
    }

    @RequestMapping(value = "/invalid/list" , method = RequestMethod.GET)
    public String invalidList(Model model , @RequestParam(defaultValue = "1") int page , Quest quest) {
        quest.setQuestStatus(QuestCode.QUEST_STATUS_INVALID);
        // 총 게시물 수
        int totalListCnt = questService.getQuestListAdminCnt(quest);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        quest.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        quest.setPageSize(pagination.getPageSize());
        List<Quest> popupList = questService.getQuestListAdmin(quest);

        model.addAttribute("questList", popupList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/ground/invalid :: #questList";
    }


    @RequestMapping(value = "/ongoing" , method = RequestMethod.GET)
    public String ongoing() {
        return "thymeleaf/page/cms/ground/ongoing";
    }

    @RequestMapping(value = "/ongoing/list" , method = RequestMethod.GET)
    public String ongoingList(Model model , @RequestParam(defaultValue = "1") int page , Quest quest) {
        quest.setQuestStatus(QuestCode.QUEST_STATUS_ONGOING);
        // 총 게시물 수
        int totalListCnt = questService.getQuestListAdminCnt(quest);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        quest.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        quest.setPageSize(pagination.getPageSize());
        List<Quest> popupList = questService.getQuestListAdmin(quest);

        model.addAttribute("questList", popupList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/ground/ongoing :: #questList";
    }



    @RequestMapping(value = "/success" , method = RequestMethod.GET)
    public String success() {

        return "thymeleaf/page/cms/ground/success";
    }

    @RequestMapping(value = "/success/list" , method = RequestMethod.GET)
    public String successList(Model model , @RequestParam(defaultValue = "1") int page , Quest quest) {
        quest.setQuestStatus(QuestCode.QUEST_STATUS_SUCCESS);
        // 총 게시물 수
        int totalListCnt = questService.getQuestListAdminCnt(quest);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        quest.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        quest.setPageSize(pagination.getPageSize());
        List<Quest> popupList = questService.getQuestListAdmin(quest);

        model.addAttribute("questList", popupList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/ground/success :: #questList";
    }
}
