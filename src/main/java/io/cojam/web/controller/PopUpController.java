package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.Pagination;
import io.cojam.web.domain.Popup;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.PopupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class PopUpController {

    @Autowired
    PopupService popupService;

    @RequestMapping(value = "/cms/popup" , method = RequestMethod.GET)
    public String list(Popup popup,Model model) {
        return "thymeleaf/page/cms/popup/list";
    }

    @RequestMapping(value = "/cms/popup/list",method = RequestMethod.POST)
    public String getList(Model model , @RequestParam(defaultValue = "1") int page , Popup popup){

        // 총 게시물 수
        int totalListCnt = popupService.getPopupListCnt(popup);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        popup.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        popup.setPageSize(pagination.getPageSize());
        List<Popup> popupList = popupService.getPopupList(popup);

        model.addAttribute("popupList", popupList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/popup/list :: #popupList";
    }


    @RequestMapping(value = "/cms/popup/register" , method = RequestMethod.GET)
    public String register() {

        return "thymeleaf/page/cms/popup/register";
    }

    @ResponseBody
    @RequestMapping(value = "/cms/popup/register" , method = RequestMethod.POST)
    public ResponseDataDTO register(
            @Valid  Popup popup
            ,@AuthenticationPrincipal Account account
            , @NotEmpty @NotNull @RequestParam("file") MultipartFile file) {
        return popupService.savePopup(popup,file,account);
    }

    @RequestMapping(value = "/cms/popup/view" , method = RequestMethod.GET)
    public String view(Model model,String idx){
        Popup popup = new Popup();
        popup.setPopupKey(idx);
        model.addAttribute("detail", popupService.getPopupInfo(popup));
        return "thymeleaf/page/cms/popup/view";
    }

    @ResponseBody
    @RequestMapping(value = "/cms/popup/update" , method = RequestMethod.POST)
    public ResponseDataDTO update(
            @Valid  Popup popup
            ,@AuthenticationPrincipal Account account
            , @NotEmpty @NotNull @RequestParam(value = "file",required = false) MultipartFile file) {
        return popupService.updatePopup(popup,file,account);
    }
}
