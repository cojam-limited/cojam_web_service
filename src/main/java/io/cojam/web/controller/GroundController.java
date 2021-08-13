package io.cojam.web.controller;


import io.cojam.web.account.Account;
import io.cojam.web.constant.QuestCode;
import io.cojam.web.domain.*;
import io.cojam.web.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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

    @ResponseBody
    @RequestMapping(value = "/draft" , method = RequestMethod.POST)
    public ResponseDataDTO draft(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.draftMarket(questKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/pending" , method = RequestMethod.POST)
    public ResponseDataDTO pending(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.pendingMarket(questKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/unpending" , method = RequestMethod.POST)
    public ResponseDataDTO unPending(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.unPendingMarket(questKey,account);
    }


    @ResponseBody
    @RequestMapping(value = "/invalid" , method = RequestMethod.POST)
    public ResponseDataDTO invalid(
            @NotNull @NotEmpty String questKey
            , @NotNull @NotEmpty String description
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.invalidMarket(questKey,description,account);
    }

    @RequestMapping(value = "/quest/{questKey}" , method = RequestMethod.POST)
    public String questDetail(@PathVariable String questKey,Model model) {
        ResponseDataDTO response = new ResponseDataDTO();
        Quest detail = questService.getQuestDetail(questKey);
        model.addAttribute("detail",detail);
        model.addAttribute("answerList", questService.getQuestAnswerList(questKey));

        return "thymeleaf/fragment/popup :: #groundDetailCMS";
    }


    @ResponseBody
    @RequestMapping(value = "/answer" , method = RequestMethod.POST)
    public ResponseDataDTO answer(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.answerApprove(questKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/approveMarket" , method = RequestMethod.POST)
    public ResponseDataDTO approve(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.approveMarket(questKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/hot" , method = RequestMethod.POST)
    public ResponseDataDTO hot(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.hotMarket(questKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/finish" , method = RequestMethod.POST)
    public ResponseDataDTO finish(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.finishMarket(questKey,account);
    }


    @ResponseBody
    @RequestMapping(value = "/getAnswerList" , method = RequestMethod.POST)
    public ResponseDataDTO getAnswerList(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        List<QuestAnswer> list = questService.getQuestAnswerList(questKey);
        if(list==null || list.size() < 1){
            response.setCheck(false);
            response.setMessage("No answer data!.");
        }else {
            response.setCheck(true);
            response.setMessage("No answer data!.");
            response.setItem(list);
        }
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/getSuccessInfo" , method = RequestMethod.POST)
    public ResponseDataDTO getSuccessInfo(
            @NotNull @NotEmpty String selectedQuestKey
            ,@NotNull @NotEmpty  String selectedAnswerKey) throws Exception {
        return questService.getSuccessInfo(selectedQuestKey,selectedAnswerKey);
    }


    @ResponseBody
    @RequestMapping(value = "/successMarket" , method = RequestMethod.POST)
    public ResponseDataDTO successMarket(
            @NotNull @NotEmpty String selectedQuestKey
            ,@NotNull @NotEmpty  String selectedAnswerKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.successMarket(selectedQuestKey,selectedAnswerKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/getAdjournInfo" , method = RequestMethod.POST)
    public ResponseDataDTO getAdjournInfo(
            @NotNull @NotEmpty String adjournQuestKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.getAdjournInfo(adjournQuestKey);
    }

    @ResponseBody
    @RequestMapping(value = "/adjournMarket" , method = RequestMethod.POST)
    public ResponseDataDTO adjournMarket(
            @NotNull @NotEmpty String adjournQuestKey
            ,@NotNull @NotEmpty  String adjournDesc
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.adjournMarket(adjournQuestKey,adjournDesc,account);
    }


    @ResponseBody
    @RequestMapping(value = "/retrieve" , method = RequestMethod.POST)
    public ResponseDataDTO retrieveMarket(
            @NotNull @NotEmpty String retrieveQuestKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.retrieveMarket(retrieveQuestKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/adjourn/retrieve" , method = RequestMethod.POST)
    public ResponseDataDTO adjournRetrieveMarket(
            @NotNull @NotEmpty String retrieveQuestKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.adjournRetrieveMarket(retrieveQuestKey,account);
    }

    @ResponseBody
    @RequestMapping(value = "/push" , method = RequestMethod.POST)
    public ResponseDataDTO push(
            @NotNull @NotEmpty String questKey
            , @AuthenticationPrincipal Account account) throws Exception {
        return questService.pushMarket(questKey,account);
    }
}
