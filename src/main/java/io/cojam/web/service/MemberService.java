package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.ResponseDataCode;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.MemberDao;
import io.cojam.web.domain.*;
import io.cojam.web.domain.wallet.Transaction;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.encoder.PasswordEncoding;
import io.cojam.web.encoder.SHA256Util;
import io.cojam.web.klaytn.service.RecommendApiService;
import io.cojam.web.service.contract.ContractApplicationService;
import io.cojam.web.utils.AES256Util;
import io.cojam.web.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.utils.Convert;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.Aes256Sha2;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    ContractApplicationService contractApplicationService;


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

        String emailName = member.getMemberEmail().split("@")[1];
        if(memberDao.checkRejectEmailName(emailName.trim()) > 0){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("You can not use temporary email");
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


        try {
            AES256Util aes256Util = new AES256Util(myConfig.getJoinParameterKey());
            responseDataDTO.setItem(aes256Util.encrypt(member.getMemberKey()));
            //회원 가입 인증 이메일 전송
            //fpNumber 생성
            String fpNumber = CommonUtils.getAuthCode(13);

            member.setFpNumber(fpNumber);

            String parameter = String.format("%s**%s**%s",member.getMemberKey(),member.getMemberEmail(),fpNumber);

            //parameter 암호화
            parameter = aes256Util.encrypt(parameter);

            responseDataDTO.setItem(parameter);

            //이메일 전송
            Mail mail = new Mail();
            String message = "";
            message +="<div>Hi there,</div>";

            message +="<div>Thank you for joining COJAM!</div>";

            message +="<div>In this email you will find a link that, when clicked, will bring you back to a confirmation page.</div>";

            message +="<div>Once you've confirmed your email, you can start using COJAM.</div>";

            message += "<div>Join Confirm Code : ";
            message += "<b>"+fpNumber+"</b>";
            message += "</div>";
            message += "<div>COJAM LIMITED</div>";
            message += "<div>E-Mail : ask@cojam.io</div>";

            message += "<div>Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland</div>";

            message += "<div>Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea</div>";

            mail.setAddress(member.getMemberEmail());
            mail.setMessage(message);
            mail.setTitle("Link of Join confirm.");

            memberDao.saveMemberJoinCertification(member);

            mailService.mailSend(mail);

        }catch (Exception e){
            e.printStackTrace();
        }

        //지갑 생성
        //walletService.saveWallet(member.getMemberKey(),member.getMemberId());


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

        String message = "Change Password (Link) : <a target='_blank' href='";
        message+=myConfig.getHostUrl()+"/user/pass/change?";
        message+="fpNumber="+detail.getFpNumber()+"&";
        message+="memberKey="+detail.getMemberKey()+"&";
        message+="memberId="+detail.getMemberId();
        message+="'>LINK</a>";
        message +="<br/>";
        message += "COJAM LIMITED";
        message +="<br/>";
        message += "E-Mail : ask@cojam.io";
        message +="<br/>";
        message += "Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland";
        message +="<br/>";
        message += "Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea";
        message +="<br/>";
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

    @Transactional
    public ResponseDataDTO memberWalletLock(List<String> memberKeyList, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        List<String> addressList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        if(memberKeyList == null || memberKeyList.size() < 1){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("parameter is wrong!");
            return responseDataDTO;
        }
        for (String memberKey:memberKeyList
             ) {
            Member member = new Member();
            member.setMemberKey(memberKey);
            Member detail = memberDao.getMemberInfoForMemberKey(member);
            if(detail == null){
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage(String.format("'%s' is no users.",memberKey));
                return responseDataDTO;
            }

            if(detail.getWalletLock()){
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage(String.format("'%s' wallet is already locked.",detail.getMemberId()));
                return responseDataDTO;
            }
            Wallet wallet = walletService.getWalletInfo(memberKey);
            if(wallet!=null && !StringUtils.isBlank(wallet.getWalletAddress())){
                addressList.add(wallet.getWalletAddress());
                memberList.add(memberKey);
            }
        }

        if(addressList.size() > 0 && addressList.size() == memberList.size() ){
            TransactionReceipt transactionReceipt = contractApplicationService.lockWallet(addressList);
            if(transactionReceipt!=null && !StringUtils.isBlank(transactionReceipt.getTransactionId())){
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("lockTransactionId",transactionReceipt.getTransactionId());
                paramMap.put("walletLock",true);
                paramMap.put("memberList",memberList);
                memberDao.updateMemberLock(paramMap);
            }
        }

        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }


    @Transactional
    public ResponseDataDTO memberWalletUnLock(List<String> memberKeyList, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        List<String> addressList = new ArrayList<>();
        List<String> memberList = new ArrayList<>();
        if(memberKeyList == null || memberKeyList.size() < 1){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("parameter is wrong!");
            return responseDataDTO;
        }
        for (String memberKey:memberKeyList
        ) {
            Member member = new Member();
            member.setMemberKey(memberKey);
            Member detail = memberDao.getMemberInfoForMemberKey(member);
            if(detail == null){
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage(String.format("'%s' is no users.",memberKey));
                return responseDataDTO;
            }

            if(!detail.getWalletLock()){
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage(String.format("'%s' wallet is already unlocked.",detail.getMemberId()));
                return responseDataDTO;
            }
            Wallet wallet = walletService.getWalletInfo(memberKey);
            if(wallet!=null && !StringUtils.isBlank(wallet.getWalletAddress())){
                addressList.add(wallet.getWalletAddress());
                memberList.add(memberKey);
            }
        }

        if(addressList.size() > 0 && addressList.size() == memberList.size() ){
            TransactionReceipt transactionReceipt = contractApplicationService.lockWallet(addressList);
            if(transactionReceipt!=null && !StringUtils.isBlank(transactionReceipt.getTransactionId())){
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("lockTransactionId",transactionReceipt.getTransactionId());
                paramMap.put("walletLock",false);
                paramMap.put("memberList",memberList);
                memberDao.updateMemberLock(paramMap);
            }
        }

        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }



    @Transactional
    public ResponseDataDTO recommendMember(String recommendMemberId,Account account){
        ResponseDataDTO response = new ResponseDataDTO();
        int checkCount = memberDao.getRecommendCount(account.getMemberKey());
        if(checkCount > 0){
            response.setCheck(false);
            response.setMessage("You already have referral information.");
            return response;
        }else{
            Member param = new Member();
            param.setMemberId(recommendMemberId);
            Member detail = memberDao.getMemberInfo(param);
            if(detail==null){
                response.setCheck(false);
                response.setMessage("This user does not exist.");
                return response;
            }
            if(detail.getMemberKey().equals(account.getMemberKey())){
                response.setCheck(false);
                response.setMessage("You cannot recommend yourself.");
                return response;
            }

            Recommend recommend = new Recommend();
            recommend.setMemberKey(account.getMemberKey());
            recommend.setRecommendKey(detail.getMemberKey());
            memberDao.saveRecommend(recommend);

            //토큰 전송
            Wallet mWallet = walletService.getWalletInfo(account.getMemberKey());
            Wallet rWallet = walletService.getWalletInfo(detail.getMemberKey());
            if(mWallet== null || rWallet ==null){
                response.setCheck(false);
                response.setMessage("Your wallet has not been created.");
                return response;
            }

            if(mWallet!= null && !StringUtils.isBlank(mWallet.getWalletAddress())){
                BigInteger amount = Convert.toWei(WalletCode.RECOMMEND_M_AMOUNT, Convert.Unit.ETHER).toBigInteger();
                Transaction transaction =walletService.sendRecommendToken(mWallet,amount,WalletCode.TRANSACTION_TYPE_RECOMMEND_M);
                if(transaction != null && !StringUtils.isBlank(transaction.getTransactionId())){
                    recommend.setMTransactionId(transaction.getTransactionId());
                    memberDao.updateRecommend(recommend);
                }
            }

            if(rWallet!= null && !StringUtils.isBlank(rWallet.getWalletAddress())){
                BigInteger amount = Convert.toWei(WalletCode.RECOMMEND_R_AMOUNT, Convert.Unit.ETHER).toBigInteger();
                Transaction transaction =walletService.sendRecommendToken(rWallet,amount,WalletCode.TRANSACTION_TYPE_RECOMMEND_R);
                if(transaction != null && !StringUtils.isBlank(transaction.getTransactionId())){
                    recommend.setRTransactionId(transaction.getTransactionId());
                    memberDao.updateRecommend(recommend);
                }
            }

            response.setCheck(true);
            response.setItem(detail);
            response.setMessage("success");
        }

        return response;

    }


    public ResponseDataDTO checkRecommendMember(Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Map<String,Object> responseMap = new HashMap<>();

        int checkCount = memberDao.getRecommendCount(account.getMemberKey());

        if(checkCount > 0){
            responseMap.put("check",true);
            responseMap.put("recommend",memberDao.getMyRecommendInfo(account.getMemberKey()));
        }else{
            responseMap.put("check",false);

        }
        responseDataDTO.setItem(responseMap);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        return responseDataDTO;

    }

    @Transactional
    public ResponseDataDTO joinConfirmMember(String memberKey,String memberEmail,String fpNumber){

        ResponseDataDTO response = new ResponseDataDTO();
        Map<String,Object> responseMap = new HashMap<>();
        Member member = new Member();
        member.setMemberKey(memberKey);
        member.setMemberEmail(memberEmail);
        member.setFpNumber(fpNumber);
        Member detail =memberDao.getMemberInfoForMemberKey(member);
        if(detail == null || detail.getCertification()){
            response.setMessage("This is incorrect information.");
            response.setCheck(false);
            return response;
        }

        Member memberCertification = memberDao.getMemberJoinCertification(member);
        if(memberCertification == null
                || !memberEmail.equals(memberCertification.getMemberEmail())
                || !fpNumber.equals(memberCertification.getFpNumber())
        ){
            response.setMessage("This is incorrect information.");
            response.setCheck(false);
            return response;
        }

        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"));


        // ZonedDateTime => Timestamp 변환
        Timestamp nowTimestamp = Timestamp.valueOf(utcDateTime.toLocalDateTime());

        long lifetime = nowTimestamp.getTime() - memberCertification.getCreatedDateTime().getTime();
        long seconds = lifetime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if(hours > 6){
            response.setMessage("Your authentication information has expired.");
            response.setCheck(false);
            return response;
        }

        memberDao.removeMemberJoinCertification(member);
        memberDao.updateMemberJoinCertification(member);

        Wallet wallet = walletService.getWalletInfo(memberKey);

        if(wallet == null){
            walletService.saveWallet(detail.getMemberKey(),detail.getMemberId());
        }

        response.setItem(responseMap);
        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO joinConfirmMemberNew(String memberKey,String memberEmail,String fpNumber,String emailCode){

        ResponseDataDTO response = new ResponseDataDTO();
        Map<String,Object> responseMap = new HashMap<>();
        Member member = new Member();
        member.setMemberKey(memberKey);
        member.setMemberEmail(memberEmail);
        member.setFpNumber(fpNumber);
        Member detail =memberDao.getMemberInfoForMemberKey(member);
        if(detail == null || detail.getCertification()){
            response.setMessage("This is incorrect information.");
            response.setCheck(false);
            return response;
        }

        Member memberCertification = memberDao.getMemberJoinCertification(member);
        if(memberCertification == null
                || !memberEmail.equals(memberCertification.getMemberEmail())
                || !fpNumber.equals(memberCertification.getFpNumber())
        ){
            response.setMessage("This is incorrect information.");
            response.setCheck(false);
            return response;
        }

        if(!fpNumber.equals(emailCode)){
            response.setMessage("This is incorrect information.");
            response.setCheck(false);
            return response;
        }

        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"));


        // ZonedDateTime => Timestamp 변환
        Timestamp nowTimestamp = Timestamp.valueOf(utcDateTime.toLocalDateTime());

        long lifetime = nowTimestamp.getTime() - memberCertification.getCreatedDateTime().getTime();
        long seconds = lifetime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if(hours > 6){
            response.setMessage("Your authentication information has expired.");
            response.setCheck(false);
            return response;
        }

        memberDao.removeMemberJoinCertification(member);
        memberDao.updateMemberJoinCertification(member);

        Wallet wallet = walletService.getWalletInfo(memberKey);

        if(wallet == null){
            walletService.saveWallet(detail.getMemberKey(),detail.getMemberId());
        }

        response.setItem(responseMap);
        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO resendEMail(String memberKey){

        ResponseDataDTO response = new ResponseDataDTO();
        Map<String,Object> responseMap = new HashMap<>();
        Member member = new Member();
        member.setMemberKey(memberKey);
        Member detail =memberDao.getMemberInfoForMemberKey(member);
        if(detail == null || detail.getCertification()){
            response.setMessage("This is incorrect information.");
            response.setCheck(false);
            return response;
        }

        if(detail.getCertification()){
            response.setMessage("Email verification is complete.");
            response.setCheck(false);
            return response;
        }

        try {
            AES256Util aes256Util = new AES256Util(myConfig.getJoinParameterKey());
            //회원 가입 인증 이메일 전송
            //fpNumber 생성
            String fpNumber = CommonUtils.getAuthCode(13);

            detail.setFpNumber(fpNumber);

            String parameter = String.format("%s**%s**%s",detail.getMemberKey(),detail.getMemberEmail(),fpNumber);

            //parameter 암호화
            parameter = aes256Util.encrypt(parameter);

            responseMap.put("parameter",parameter);

            //이메일 전송
            Mail mail = new Mail();
            String message = "";
            message +="<div>Hi there,</div>";

            message +="<div>Thank you for joining COJAM!</div>";

            message +="<div>In this email you will find a link that, when clicked, will bring you back to a confirmation page.</div>";

            message +="<div>Once you've confirmed your email, you can start using COJAM.</div>";

            message += "<div>Join Confirm Code : ";
            message += "<b>"+fpNumber+"</b>";
            message +="</div>";
            message += "<div>COJAM LIMITED</div>";
            message += "<div>E-Mail : ask@cojam.io</div>";

            message += "<div>Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland</div>";

            message += "<div>Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea</div>";

            mail.setAddress(detail.getMemberEmail());
            mail.setMessage(message);
            mail.setTitle("Link of Join confirm.");
            memberDao.saveMemberJoinCertification(detail);
            mailService.mailSend(mail);

        }catch (Exception e){
            e.printStackTrace();
        }


        response.setItem(responseMap);
        response.setCheck(true);
        response.setMessage("success");
        return response;
    }




    @Transactional
    public ResponseDataDTO memberAccess(List<String> memberKeyList, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();

        if(memberKeyList == null || memberKeyList.size() < 1){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("parameter is wrong!");
            return responseDataDTO;
        }
        for (String memberKey:memberKeyList
        ) {
            Member member = new Member();
            member.setMemberKey(memberKey);
            member.setAccess(true);
            memberDao.updateMemberAccess(member);
        }



        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }

    @Transactional
    public ResponseDataDTO memberReject(List<String> memberKeyList, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();

        if(memberKeyList == null || memberKeyList.size() < 1){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("parameter is wrong!");
            return responseDataDTO;
        }
        for (String memberKey:memberKeyList
        ) {
            Member member = new Member();
            member.setMemberKey(memberKey);
            member.setAccess(false);
            memberDao.updateMemberAccess(member);
        }



        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success.");
        return responseDataDTO;
    }
}
