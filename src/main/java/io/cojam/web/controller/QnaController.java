package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.Pagination;
import io.cojam.web.domain.Question;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class QnaController {

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = "/cms/qna" , method = RequestMethod.GET)
    public String list() {

        return "thymeleaf/page/cms/qna/list";
    }

    @RequestMapping(value = "/cms/qna/list",method = RequestMethod.POST)
    public String getList(Model model , @RequestParam(defaultValue = "1") int page , Question question){

        // 총 게시물 수
        int totalListCnt = questionService.getQuestionListCnt(question);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        question.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        question.setPageSize(pagination.getPageSize());
        List<Question> qnaList = questionService.getQuestionList(question);

        model.addAttribute("qnaList", qnaList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/qna/list :: #qnaList";
    }

    @RequestMapping(value = "/cms/qna/register" , method = RequestMethod.GET)
    public String register(Model model) {
        return "thymeleaf/page/cms/qna/register";
    }

    @ResponseBody
    @RequestMapping(value = "/cms/qna/register" , method = RequestMethod.POST)
    public ResponseDataDTO register(
            @Valid Question question
            ,@AuthenticationPrincipal Account account) {
        return questionService.saveQuestion(question,account);
    }

    @RequestMapping(value = "/cms/qna/view",method = RequestMethod.GET)
    public String view(Model model,@NotNull @NotEmpty String idx){
        Question question = new Question();
        question.setQuestionKey(idx);

        model.addAttribute("detail", questionService.getQuestionInfo(question));
        return "thymeleaf/page/cms/qna/view";
    }

    @ResponseBody
    @RequestMapping(value = "/cms/qna/update" , method = RequestMethod.POST)
    public ResponseDataDTO update(
            @NotEmpty @NotNull String questionKey
            ,@Valid Question question
            ,@AuthenticationPrincipal Account account) {
        question.setQuestionKey(questionKey);
        return questionService.updateQuestion(question,account);
    }


    @ResponseBody
    @RequestMapping(value = "/cms/qna/delete" , method = RequestMethod.POST)
    public ResponseDataDTO delete(
            @NotEmpty @NotNull String questionKey
            ,@AuthenticationPrincipal Account account
    ) {
        Question question = new Question();
        question.setQuestionKey(questionKey);
        return questionService.deleteQuestion(question,account);
    }
}
