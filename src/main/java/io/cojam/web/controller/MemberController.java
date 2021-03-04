package io.cojam.web.controller;

import io.cojam.web.domain.*;
import io.cojam.web.service.MemberService;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {


    @Autowired
    MemberService memberService;

    @RequestMapping(value = "/user/join", method= RequestMethod.GET)
    public String joinPage() {
        return "thymeleaf/page/member/join";
    }

    @ResponseBody
    @RequestMapping(value = "/user/join", method= RequestMethod.POST)
    public ResponseDataDTO joinProc(@Valid Member member , HttpServletResponse response) throws Exception {
        return memberService.saveMember(member);
    }

    @RequestMapping(value = "/user/join/completed", method= RequestMethod.GET)
    public String joinCompleted() {
        return "thymeleaf/page/member/joinCompleted";
    }

    @RequestMapping(value = "/user/idFind", method= RequestMethod.GET)
    public String idSearch() {
        return "thymeleaf/page/member/idFind";
    }

    @ResponseBody
    @RequestMapping(value = "/user/idFind", method= RequestMethod.POST)
    public ResponseDataDTO idSearchProc(@Valid @NonNull @NotEmpty String memberName , @Valid @NonNull @NotEmpty @Email String memberEmail , HttpServletResponse response) throws Exception {
        return memberService.searchId(memberName,memberEmail);
    }

    @RequestMapping(value = "/user/pass/change", method= RequestMethod.GET)
    public String passChange(String fpNumber, String memberKey, String memberId, Model model) {
        if(StringUtils.isBlank(fpNumber)
                || StringUtils.isBlank(memberKey)
                || StringUtils.isBlank(memberId)
                || memberService.getMemberPassResetCnt(fpNumber,memberKey,memberId) < 1
        ){
            return "thymeleaf/page/error/expired";
        }
        model.addAttribute("fpNumber",fpNumber);
        model.addAttribute("memberKey",memberKey);
        model.addAttribute("memberId",memberId);
        return "thymeleaf/page/member/pwChange";
    }

    @ResponseBody
    @RequestMapping(value = "/user/pass/change", method= RequestMethod.POST)
    public ResponseDataDTO passChange(
            PassChange passChange
            , HttpServletResponse response
    ) throws Exception {
        return memberService.changePasswordProc(passChange);
    }


    @RequestMapping(value = "/user/pass/email", method= RequestMethod.GET)
    public String passEmail() {
        return "thymeleaf/page/member/pwEmail";
    }


    @RequestMapping(value = "/user/pass/email", method= RequestMethod.POST)
    @ResponseBody
    public ResponseDataDTO passEmailProc(@Valid @NonNull @NotEmpty @Email String memberEmail) {
        return memberService.changePassword(memberEmail);
    }

    @RequestMapping(value = "/cms/users", method= RequestMethod.GET)
    public String serviceUser(Model model,Member member) {
        return "thymeleaf/page/cms/users/users";
    }

    @RequestMapping(value = "/cms/users", method= RequestMethod.POST)
    public String serviceUserPost(Model model,Member member, @RequestParam(defaultValue = "1") int page ) {

        // 총 게시물 수
        int totalListCnt = memberService.getServiceUserListCnt(member);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        member.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        member.setPageSize(pagination.getPageSize());
        List<Member> memberList = memberService.getServiceUserList(member);
        model.addAttribute("authList", new AuthInfo().getAuthMapList());
        model.addAttribute("memberList", memberList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/users/users :: #memberList";
    }

    @RequestMapping(value = "/cms/members", method= RequestMethod.GET)
    public String members(Model model) {

        return "thymeleaf/page/cms/members/members";

    }

    @RequestMapping(value = "/cms/members", method= RequestMethod.POST)
    public String membersPost(Model model,Member member, @RequestParam(defaultValue = "1") int page ) {

        // 총 게시물 수
        int totalListCnt = memberService.getMemberUserListCnt(member);

        // 생성인자로  총 게시물 수, 현재 페이지를 전달
        Pagination pagination = new Pagination(totalListCnt, page,5,10);
        // DB select start index
        member.setStartIndex(pagination.getStartIndex());
        // 페이지 당 보여지는 게시글의 최대 개수
        member.setPageSize(pagination.getPageSize());
        List<Member> memberList = memberService.getMemberUserList(member);

        model.addAttribute("authList", new AuthInfo().getAuthMapList());
        model.addAttribute("memberList", memberList);
        model.addAttribute("pagination", pagination);

        return "thymeleaf/page/cms/members/members :: #memberList";
    }

}
