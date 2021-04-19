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
import io.cojam.web.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.utils.Convert;

import java.math.BigInteger;
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
}
