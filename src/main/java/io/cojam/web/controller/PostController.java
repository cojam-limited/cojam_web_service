package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.Board;
import io.cojam.web.domain.Pagination;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@RequestMapping(value = "/cms/post")
public class PostController {

    @Autowired
    BoardService boardService;

    @GetMapping
    public String list(Model model,Board board) {
        model.addAttribute("categoryList",boardService.getNoticeCategoryList());
        return "thymeleaf/page/cms/post/list";
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public String getList(Model model , @RequestParam(defaultValue = "1") int page , Board board){

        // 총 게시물 수
        int totalListCnt = boardService.getNoticeBoardListCnt(board);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        board.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        board.setPageSize(pagination.getPageSize());
        List<Board> boardList = boardService.getNoticeBoardList(board);

        model.addAttribute("boardList", boardList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/post/list :: #boardList";
    }


    @RequestMapping(value = "/register" , method = RequestMethod.GET)
    public String register(Model model) {
        model.addAttribute("categoryList",boardService.getNoticeCategoryList());
        return "thymeleaf/page/cms/post/register";
    }


    @ResponseBody
    @RequestMapping(value = "/register" , method = RequestMethod.POST)
    public ResponseDataDTO register(
            @NotEmpty @NotNull String boardCategoryKey
            ,@NotEmpty @NotNull String boardTitle
            ,String boardContext
            ,@AuthenticationPrincipal Account account
            , @NotEmpty @NotNull @RequestParam("file") MultipartFile file) {
        Board board = new Board();
        board.setBoardCategoryKey(boardCategoryKey);
        board.setBoardTitle(boardTitle);
        board.setBoardContext(boardContext);
        return boardService.saveBoard(board,file,account);
    }

    @RequestMapping(value = "/view",method = RequestMethod.GET)
    public String view(Model model,String idx){
        Board board = new Board();
        board.setBoardKey(idx);
        model.addAttribute("categoryList",boardService.getNoticeCategoryList());
        model.addAttribute("detail", boardService.getNoticeBoardDetail(board));
        return "thymeleaf/page/cms/post/view";
    }

    @ResponseBody
    @RequestMapping(value = "/update" , method = RequestMethod.POST)
    public ResponseDataDTO update(
            @NotEmpty @NotNull String boardKey
            ,@NotEmpty @NotNull String boardCategoryKey
            ,@NotEmpty @NotNull String boardTitle
            ,String boardContext
            ,@AuthenticationPrincipal Account account
            , @RequestParam(value = "file",required = false) MultipartFile file) {
        Board board = new Board();
        board.setBoardKey(boardKey);
        board.setBoardCategoryKey(boardCategoryKey);
        board.setBoardTitle(boardTitle);
        board.setBoardContext(boardContext);
        return boardService.updateBoard(board,file,account);
    }


    @ResponseBody
    @RequestMapping(value = "/delete" , method = RequestMethod.POST)
    public ResponseDataDTO delete(
            @NotEmpty @NotNull String boardKey
            ,@AuthenticationPrincipal Account account
            ) {
        Board board = new Board();
        board.setBoardKey(boardKey);

        return boardService.deleteBoard(board,account);
    }

}
