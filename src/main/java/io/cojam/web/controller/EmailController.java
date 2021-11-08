package io.cojam.web.controller;

import io.cojam.web.domain.Board;
import io.cojam.web.domain.Email;
import io.cojam.web.domain.Pagination;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/cms/email")
public class EmailController {


    @Autowired
    EmailService emailService;

    /**
     * 관리자 이메일 관리
     * @param model
     * @param board
     * @return
     */
    @GetMapping
    public String list(Model model, Board board) {
        return "thymeleaf/page/cms/email/list";
    }


    /**
     * 관리자 이메일 허용 목록
     * @param model
     * @param page
     * @param email
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public String getList(Model model , @RequestParam(defaultValue = "1") int page , Email email){

        // 총 게시물 수
        int totalListCnt = emailService.getEnableEmailListCnt(email);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        email.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        email.setPageSize(pagination.getPageSize());
        List<Email> boardList = emailService.getEnableEmailList(email);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/email/list :: #boardList";
    }


    /**
     * 허용할 이메일 저장
     * @param email
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public ResponseDataDTO saveEmail(Email email){

        return emailService.saveEmail(email);
    }


    /**
     * 이메일 삭제
     * @param email
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public ResponseDataDTO deleteEmail(Email email){
        return emailService.deleteEmail(email);
    }


    /**
     * 이메일 수정
     * @param email
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ResponseDataDTO updateEmail(Email email){
        return emailService.updateEmail(email);
    }
}
