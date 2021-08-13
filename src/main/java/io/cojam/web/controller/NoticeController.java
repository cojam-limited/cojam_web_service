package io.cojam.web.controller;

import io.cojam.web.domain.Board;
import io.cojam.web.domain.Pagination;
import io.cojam.web.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/user/notice")
public class NoticeController {

    @Autowired
    BoardService boardService;

    @GetMapping
    public String list(Model model,Board board){
        model.addAttribute("boardList",null);
        model.addAttribute("categoryList",boardService.getNoticeCategoryList("N"));
        model.addAttribute("param",board);
        return "thymeleaf/page/notice/index";
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public String getList(Model model , @RequestParam(defaultValue = "1") int page , Board board){
        board.setResultYn("N");
        // 총 게시물 수
        int totalListCnt = boardService.getNoticeBoardListCnt(board);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,9);
        // DB select start index
        board.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        board.setPageSize(pagination.getPageSize());
        List<Board> boardList = boardService.getNoticeBoardList(board);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/service/index :: #boardList";
    }

    @RequestMapping(value = "/view",method = RequestMethod.GET)
    public String view(Model model,String idx){
        Board board = new Board();
        board.setBoardKey(idx);
        board.setResultYn("N");
        model.addAttribute("detail", boardService.getNoticeBoardDetail(board));
        model.addAttribute("relatedList", boardService.getNoticeRelatedList(board));
        return "thymeleaf/page/notice/view";
    }
}
