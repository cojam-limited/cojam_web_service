package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.domain.*;
import io.cojam.web.domain.wallet.Transaction;
import io.cojam.web.service.MemberService;
import io.cojam.web.service.QuestService;
import io.cojam.web.service.TransactionService;
import io.cojam.web.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@RequestMapping(value = "/user/mypage")
public class MyPageController {

    @Autowired
    MemberService memberService;

    @Autowired
    WalletService walletService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    QuestService questService;

    @GetMapping
    public String index(
            Model model
            , @AuthenticationPrincipal Account account
    )
    {
        Member member = new Member();
        member.setMemberKey(account.getMemberKey());
        Wallet wallet = walletService.getWalletInfo(account.getMemberKey());
        model.addAttribute("member",memberService.getMemberInfoForMemberKey(member));
        model.addAttribute("wallet",wallet);

        return "thymeleaf/page/myPage/index";

    }


    @RequestMapping(value = "/transaction" , method = RequestMethod.POST)
    public String transactionList(
            Model model
            , String memberId
            , String walletAddress
            , Transaction transaction
            , @AuthenticationPrincipal Account account
            , @RequestParam(defaultValue = "1") int page
    )
    {
        Member member = new Member();
        member.setMemberKey(account.getMemberKey());
        member.setMemberId(account.getMemberId());

        Wallet wallet =new Wallet();
        wallet.setWalletAddress(walletAddress);
        transaction.setAddress(walletAddress);
        // 총 게시물 수
        int totalListCnt = transactionService.getTransactionListCnt(transaction);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,20);
        // DB select start index
        transaction.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        transaction.setPageSize(pagination.getPageSize());
        List<Transaction> list = transactionService.getTransactionList(transaction);
        model.addAttribute("member", member);
        model.addAttribute("wallet", wallet);
        model.addAttribute("transactionList", list);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/myPage/index :: #transactionList";

    }


    @ResponseBody
    @RequestMapping(value = "/transaction/{transactionKey}", method= RequestMethod.GET)
    public ResponseDataDTO changeAuth(
            @PathVariable String transactionKey
            ,@AuthenticationPrincipal Account account
            , HttpServletResponse response
    ) throws Exception {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Transaction detail = transactionService.getTransaction(transactionKey);

        if(detail == null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
        }else {
            if(!WalletCode.TRANSACTION_STATUS_CONFIRM.equals(detail.getStatus())){
                responseDataDTO =transactionService.getTransactionStatus(detail);
            }else{
                responseDataDTO.setCheck(true);
                responseDataDTO.setItem(detail.getStatus());
                responseDataDTO.setMessage("success");
            }

        }


        return responseDataDTO;
    }

    @RequestMapping(value = "/ground" , method = RequestMethod.POST)
    public String groundList(
            Model model
            , Quest quest
            , @AuthenticationPrincipal Account account
            , @RequestParam(defaultValue = "1") int page
    )
    {
        // 총 게시물 수
        quest.setMemberKey(account.getMemberKey());
        int totalListCnt = questService.getQuestListMypageCnt(quest);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        quest.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        quest.setPageSize(pagination.getPageSize());
        List<Quest> list = questService.getQuestListMypage(quest);
        model.addAttribute("myQuestList", list);
        model.addAttribute("pagination", pagination);


        return "thymeleaf/page/myPage/index :: #groundList";

    }


    @RequestMapping(value = "/voting" , method = RequestMethod.POST)
    public String groundList(
            Model model
            , MyVoting myVoting
            , @AuthenticationPrincipal Account account
            , @RequestParam(defaultValue = "1") int page
    )
    {
        // 총 게시물 수
        myVoting.setMemberKey(account.getMemberKey());
        int totalListCnt = questService.getMyVotingListCnt(myVoting);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        myVoting.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        myVoting.setPageSize(pagination.getPageSize());
        List<MyVoting> list = questService.getMyVotingList(myVoting);
        model.addAttribute("myVotingList", list);
        model.addAttribute("pagination", pagination);


        return "thymeleaf/page/myPage/index :: #myVotingList";

    }

    @RequestMapping(value = "/quest/{questKey}" , method = RequestMethod.POST)
    public String questDetail(@PathVariable String questKey,Model model) {
        ResponseDataDTO response = new ResponseDataDTO();
        Quest detail = questService.getQuestDetail(questKey);
        model.addAttribute("detail",detail);
        model.addAttribute("answerList", questService.getQuestAnswerList(questKey));

        return "thymeleaf/fragment/popup :: #groundDetail";
    }
}
