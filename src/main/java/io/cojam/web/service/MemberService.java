package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.ResponseDataCode;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.MemberDao;
import io.cojam.web.domain.*;
import io.cojam.web.encoder.PasswordEncoding;
import io.cojam.web.encoder.SHA256Util;
import io.cojam.web.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;


@Service
@Transactional
public class MemberService {

    @Autowired
    SequenceService sequenceService;

    @Autowired
    MemberDao memberDao;

    @Autowired
    MailService mailService;

    @Autowired
    WalletService walletService;

    @Autowired
    MyConfig myConfig;


    @Transactional
    public ResponseDataDTO saveMember(Member member){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        //정규식 체크 아이디
        if(!Pattern.compile("^[a-z0-9_\\-]{5,20}$").matcher(member.getMemberId()).find()) {
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("ID format is incorrect.");
            return responseDataDTO;
        }

        //정규식 체크 비밀번호
        if(!Pattern.compile("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^*()\\-_=+\\\\\\|\\[\\]{};:\\'\",.<>\\/?]).{8,16}$").matcher(member.getMemberPass()).find()) {
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("password format is incorrect.");
            return responseDataDTO;
        }

        //정규식 체크 이메일
        if(!Pattern.compile("^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\\.[A-Za-z0-9\\-]+").matcher(member.getMemberEmail()).find()) {
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("Email format is incorrect.");
            return responseDataDTO;
        }

        //아이디 중복 체크
        if(memberDao.getMemberInfo(member) != null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("This ID is already taken.");
            return responseDataDTO;
        }

        //이메일 중복 체크
        if(memberDao.getMemberInfByEmail(member) != null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("This email is already signed up.");
            return responseDataDTO;
        }

        //멤버 시퀀스 채번
        member.setMemberKey(sequenceService.getSequence(SequenceCode.TB_MEMBER));
        //비밀번호 암호화
        PasswordEncoding passwordEncoding = new PasswordEncoding();
        String password = SHA256Util.getEncrypt(member.getMemberPass(),"");
        password = passwordEncoding.encode(password);
        member.setMemberPass(password);
        //멤버 정보 저장
        memberDao.saveMemberInfo(member);

        //지갑 생성
        walletService.saveWallet(member.getMemberKey());


        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;

    }

    public ResponseDataDTO searchId(String memberName,String memberEmail){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();

        //아이디 존재 여부 체크
        Member member = new Member();
        member.setMemberName(memberName);
        member.setMemberEmail(memberEmail);
        Member detail = memberDao.getMemberInfoByEmailName(member);
        if(detail == null ){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("No matching information.");
            return responseDataDTO;
        }
        responseDataDTO.setItem(detail);
        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }

    public ResponseDataDTO changePassword(String memberEmail){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();

        //아이디 존재 여부 체크
        Member member = new Member();
        member.setMemberEmail(memberEmail);
        Member detail = memberDao.getMemberInfByEmail(member);
        if(detail == null ){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("No matching information.");
            return responseDataDTO;
        }

        //fpNumber 생성
        detail.setFpNumber(CommonUtils.getAuthCode(13));
        //정보 저장
        memberDao.deletePassHistory(detail);
        memberDao.saveMemberPassReset(detail);
        //이메일 전송
        Mail mail = new Mail();
        String message = "Change Password (Link) : ";
        message+=myConfig.getHostUrl()+"/user/pass/change?";
        message+="fpNumber="+detail.getFpNumber()+"&";
        message+="memberKey="+detail.getMemberKey()+"&";
        message+="memberId="+detail.getMemberId();
        mail.setAddress(detail.getMemberEmail());
        mail.setMessage(message);
        mail.setTitle("Link of Password Management.");
        try {
            mailService.mailSend(mail);
        }catch (Exception e){
            e.printStackTrace();
        }


        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }


    public Integer getMemberPassResetCnt(String fpNumber, String memberKey, String memberId){
        Member member = new Member();
        member.setFpNumber(fpNumber);
        member.setMemberKey(memberKey);
        member.setMemberId(memberId);
        return memberDao.getMemberPassResetCnt(member);
    }




    public ResponseDataDTO changePasswordProc(PassChange passChange){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();

        Member member = new Member();
        member.setMemberId(passChange.getMemberId());
        member.setMemberKey(passChange.getMemberKey());
        member.setFpNumber(passChange.getFpNumber());

        if(memberDao.getMemberPassResetCnt(member) < 1){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("This page has expired.");
            return responseDataDTO;
        }

        //정규식 체크 비밀번호
        if(!Pattern.compile("^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^*()\\-_=+\\\\\\|\\[\\]{};:\\'\",.<>\\/?]).{8,16}$").matcher(passChange.getMemberPass()).find()) {
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("password format is incorrect.");
            return responseDataDTO;
        }

        //비밀번호 암호화
        PasswordEncoding passwordEncoding = new PasswordEncoding();
        String password = SHA256Util.getEncrypt(passChange.getMemberPass(),"");
        password = passwordEncoding.encode(password);
        member.setMemberPass(password);
        //비밀번호 업데이트
        memberDao.updateMemberPass(member);
        //페스워드 변경 이력 삭제
        memberDao.deletePassHistory(member);

        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }


    public List<Member> getServiceUserList(Member member){
        return memberDao.getServiceUserList(member);
    }

    public Integer getServiceUserListCnt(Member member){
        return memberDao.getServiceUserListCnt(member);
    }

    public List<Member> getMemberUserList(Member member){
        return memberDao.getMemberUserList(member);
    }

    public Integer getMemberUserListCnt(Member member){
        return memberDao.getMemberUserListCnt(member);
    }

    public Member getMemberInfoForMemberKey(Member member){
        return memberDao.getMemberInfoForMemberKey(member);
    }


    @Transactional
    public ResponseDataDTO changeMemberAuth(Member member , Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Member regParam = new Member();
        regParam.setMemberKey(account.getMemberKey());
        Member regUser = memberDao.getMemberInfoForMemberKey(regParam);
        if(!"VIP".equals(regUser.getMemberRole())){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("You do not have permission.");
            return responseDataDTO;
        }

        Member changeUser = memberDao.getMemberInfoForMemberKey(member);
        if(changeUser == null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("There are no users.");
            return responseDataDTO;
        }

        memberDao.updateMemberRole(member);

        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }
}
