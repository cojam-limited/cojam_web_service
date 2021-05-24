package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.*;
import io.cojam.web.service.MemberService;
import io.cojam.web.utils.AES256Util;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {


    @Autowired
    MemberService memberService;

    @Autowired
    MyConfig myConfig;

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
    public String joinCompleted(String a4,Model model) {
        model.addAttribute("a4",a4);
        return "thymeleaf/page/member/joinCompleted";
    }

    @RequestMapping(value = "/user/join/joinConfirm", method= RequestMethod.GET)
    public String joinConfirm() {
        return "thymeleaf/page/member/joinCompleted";
    }

    @RequestMapping(value = "/user/join/confirm", method= RequestMethod.GET)
    @ResponseBody
    public String joinConfirm(@NotNull @NotEmpty @Length(min = 10) String a7) throws Exception {

        AES256Util aes256Util = new AES256Util(myConfig.getJoinParameterKey());
        a7 = aes256Util.decrypt(a7);
        String memberKey = a7.split("[*][*]")[0];
        String memberEmail = a7.split("[*][*]")[1];
        String fpNumber = a7.split("[*][*]")[2];
        ResponseDataDTO result = memberService.joinConfirmMember(memberKey,memberEmail,fpNumber);

        if(result.getCheck()){
            return "<script>alert('" + result.getMessage() + "'); location.replace('/user/join/joinConfirm');</script> ";
        }else{
            return "<script>alert('" + result.getMessage() + "'); location.replace('/user/home');</script> ";
        }
    }

    @RequestMapping(value = "/user/join/resend", method= RequestMethod.POST)
    @ResponseBody
    public ResponseDataDTO resendEmail(@NotNull @NotEmpty @Length(min = 10) String a4) throws Exception {

        AES256Util aes256Util = new AES256Util(myConfig.getJoinParameterKey());
        a4 = aes256Util.decrypt(a4);
        return memberService.resendEMail(a4);
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


    @ResponseBody
    @RequestMapping(value = "/cms/member/changeAuth", method= RequestMethod.POST)
    public ResponseDataDTO changeAuth(
            @NotNull @NotEmpty String memberRole
            ,@NotNull @NotEmpty String memberKey
            ,@AuthenticationPrincipal Account account
            , HttpServletResponse response
    ) throws Exception {
        Member member = new Member();
        member.setMemberRole(memberRole);
        member.setMemberKey(memberKey);
        return memberService.changeMemberAuth(member,account);
    }


    @ResponseBody
    @RequestMapping(value = "/cms/member/lockWallet", method= RequestMethod.POST)
    public ResponseDataDTO lockWallet(
            @Valid MemberLock memberLock
            ,@AuthenticationPrincipal Account account
            , HttpServletResponse response
    ) throws Exception {
        return memberService.memberWalletLock(memberLock.getMemberKeys(),account);
    }

    @ResponseBody
    @RequestMapping(value = "/cms/member/unLockWallet", method= RequestMethod.POST)
    public ResponseDataDTO unLockWallet(
            @Valid MemberLock memberLock
            ,@AuthenticationPrincipal Account account
            , HttpServletResponse response
    ) throws Exception {
        return memberService.memberWalletUnLock(memberLock.getMemberKeys(),account);
    }

}
