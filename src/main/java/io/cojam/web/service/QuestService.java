package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.QuestCode;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.QuestDao;
import io.cojam.web.domain.*;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.dto.TransactionStatus;
import io.cojam.web.klaytn.service.TransactionApiService;
import io.cojam.web.service.contract.ContractApplicationService;
import io.cojam.web.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class QuestService {

    @Autowired
    QuestDao questDao;

    @Autowired
    SequenceService sequenceService;

    @Autowired
    FileService fileService;

    @Autowired
    SeasonService seasonService;

    @Autowired
    WalletService walletService;

    @Autowired
    ContractApplicationService contractApplicationService;

    @Autowired
    MailService mailService;

    @Autowired
    MemberService memberService;

    @Autowired
    MyConfig myConfig;

    @Autowired
    TransactionApiService transactionApiService;

    public Integer getQuestListAdminCnt(Quest quest){
        return questDao.getQuestListAdminCnt(quest);
    }

    public List<Quest> getQuestListAdmin(Quest quest){
        return questDao.getQuestListAdmin(quest);
    }

    public Quest getQuestDetail(String questKey){
        return questDao.getQuestDetail(questKey);
    }

    @Transactional
    public ResponseDataDTO saveQuest(Quest quest, MultipartFile file, Account account) throws Exception{
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        //현재 season 정보
        Season season = seasonService.getSeasonInfo();
        if(season == null || season.getSeasonKey()==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("not season active.");
            return responseDataDTO;
        }
        quest.setSeasonKey(season.getSeasonKey());
        Wallet wallet =walletService.getWalletInfo(account.getMemberKey());
        if(wallet == null || wallet.getWalletAddress() ==null){

            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("You do not have a wallet.");
            return responseDataDTO;
        }
        quest.setCreatorAddress(wallet.getWalletAddress());
        quest.setMemberKey(account.getMemberKey());
        //퀘스크  시퀀스 채번
        quest.setQuestKey(sequenceService.getSequence(SequenceCode.TB_QUEST));

        //파일 업로드
        if(file != null && file.getSize() > 0){
            FileInfo fileInfo =fileService.fileUpload(account.getMemberKey(),file, SequenceCode.TB_QUEST,quest.getQuestKey());
            if(fileInfo!= null && fileInfo.getFileKey()!=null){
                quest.setFileKey(fileInfo.getFileKey());
                quest.setSnsUrl(null);
            }
        }else if(!StringUtils.isBlank(quest.getSnsUrl())){
            SnsInfo snsInfo =getSocialMediaCheck(quest.getSnsUrl());
            if(snsInfo.isCheck()){
                //유튜브일 경우 썸네일 저장
                if("Y".equals(snsInfo.getSnsType())){
                    FileInfo fileInfo =fileService.fileUrlUploadYoutube(account.getMemberKey(),snsInfo.getSnsId(),SequenceCode.TB_QUEST,quest.getQuestKey());
                    if(fileInfo!= null && fileInfo.getFileKey()!=null){
                        quest.setFileKey(fileInfo.getFileKey());
                    }
                }else if(!StringUtils.isBlank(snsInfo.getImageUrl())){
                    FileInfo fileInfo =fileService.fileUrlUpload(account.getMemberKey(),snsInfo.getImageUrl(),SequenceCode.TB_QUEST,quest.getQuestKey());
                    if(fileInfo!= null && fileInfo.getFileKey()!=null){
                        quest.setFileKey(fileInfo.getFileKey());
                    }
                }
                quest.setSnsType(snsInfo.getSnsType());
                quest.setSnsId(snsInfo.getSnsId());
                quest.setSnsTitle(snsInfo.getSnsTitle());
                quest.setSnsDesc(snsInfo.getSnsDesc());
            }else{
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage("This is an invalid url.");
                return responseDataDTO;
            }
        }
        quest.setCompleted(false);
        quest.setHot(false);
        quest.setPending(false);
        quest.setQuestStatus(QuestCode.QUEST_STATUS_ONGOING);
        questDao.saveQuest(quest);
        if(quest.getAnswers() != null){
            QuestAnswer answer = new QuestAnswer();
            answer.setQuestKey(quest.getQuestKey());
            for (String title:quest.getAnswers()
                 ) {
                //answer.setQuestAnswerKey(sequenceService.getSequence(SequenceCode.TB_QUEST_ANSWER));
                answer.setAnswerTitle(title);
                questDao.saveQuestAnswer(answer);
            }
        }
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(quest.getQuestKey());

        // 생성 후 메일 발송 !!
        return responseDataDTO;
    }


    public static SnsInfo getSocialMediaCheck(String snsUrl){
        SnsInfo snsInfo = new SnsInfo();
        try {
            snsUrl = URLDecoder.decode(snsUrl,"utf-8").trim();
            snsInfo.setSnsUrl(snsUrl);
            snsInfo.setCheck(true);
            if (snsUrl.contains("youtube") || snsUrl.contains("youtu.be")) {
                snsInfo.setSnsType("Y");
                String youtubeId = CommonUtils.getYoutubeVideoId(snsUrl);
                if(StringUtils.isBlank(youtubeId)){
                    snsInfo.setCheck(false);
                    return snsInfo;
                }
                snsInfo.setSnsId(youtubeId);
            }else{
                snsInfo.setSnsType("O");
            }
            Connection.Response responseT = Jsoup.connect(snsUrl)
                    .method(Connection.Method.GET)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .execute();

            Document document = responseT.parse();
            if (responseT.statusCode() == HttpStatus.SC_OK) {
                if (document.select("meta[property=og:title]").first() != null) {
                    snsInfo.setSnsTitle(document.select("meta[property=og:title]").first().attr("content"));
                }

                if (document.select("meta[property=og:image]").first() != null) {
                    snsInfo.setImageUrl(document.select("meta[property=og:image]").first().attr("content"));
                }

                if (document.select("meta[property=og:description]").first() != null) {
                    snsInfo.setSnsDesc(document.select("meta[property=og:description]").first().attr("content"));
                }
            }else{
                snsInfo.setCheck(false);
                return snsInfo;
            }
        }catch (Exception e){
            snsInfo.setCheck(false);
        }
        return snsInfo;
    }

    public List<Quest> getQuestListMypage(Quest quest){
        return questDao.getQuestListMypage(quest);
    }

    public int getQuestListMypageCnt(Quest quest){
        return questDao.getQuestListMypageCnt(quest);
    }

    @Transactional
    public ResponseDataDTO draftMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();


        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            Season param = new Season();
            param.setSeasonKey(detail.getSeasonKey());
            Season season = seasonService.getSeasonInfo();
            if(season!=null){
                if (!StringUtils.isBlank(detail.getDraftTx())) {
                    response.setCheck(false);
                    response.setMessage("Draft is already Registerd!");
                    return response;
                }else{
                    /*
                    * contract 호출
                    *
                    */
                    BigInteger questKeyBigInteger = new BigInteger(detail.getQuestKey().replace(SequenceCode.TB_QUEST,""));
                    BigInteger creator_pay = Convert.toWei(String.valueOf(season.getCreatorPay()), Convert.Unit.ETHER).toBigInteger();

                    TransactionReceipt transactionReceipt = contractApplicationService.draftMarket(questKeyBigInteger,detail.getCreatorAddress(),detail.getQuestTitle(),creator_pay,new BigInteger(season.getCreatorFee()), new BigInteger(season.getCojamFee()), new BigInteger(season.getCharityFee()), new ArrayList<>());


                    if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                        detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_DRAFT);
                        detail.setDraftTx(transactionReceipt.getTransactionId());
                        detail.setUpdateMemberKey(account.getMemberKey());
                        questDao.updateQuestStatus(detail);
                        response.setCheck(true);
                        response.setMessage("success");
                    }else{
                        response.setCheck(false);
                        response.setMessage("Draft is fail.");
                    }
                }

            }else{
                response.setCheck(false);
                response.setMessage("No data.");
                return response;
            }

        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        return response;
    }


    @Transactional
    public ResponseDataDTO pendingMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(detail.getPending()){
                response.setCheck(false);
                response.setMessage("It is already pended.");
                return response;
            }else{
                detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_PEND);
                detail.setUpdateMemberKey(account.getMemberKey());
                questDao.updateQuestStatus(detail);
                response.setCheck(true);
                response.setMessage("success");
            }
        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }

    @Transactional
    public ResponseDataDTO invalidMarket(String questKey,String description,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(QuestCode.QUEST_STATUS_INVALID.equals(detail.getQuestStatus())){
                response.setCheck(false);
                response.setMessage("It is already invalid.");
                return response;
            }else{
                detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_INVALID);
                detail.setUpdateMemberKey(account.getMemberKey());
                detail.setQuestStatus(QuestCode.QUEST_STATUS_INVALID);
                detail.setQuestDesc(description);
                questDao.updateQuestStatus(detail);
                response.setCheck(true);
                response.setMessage("success");
                //메일 전송
                Member member = new Member();
                member.setMemberKey(detail.getMemberKey());
                member = memberService.getMemberInfoForMemberKey(member);
                if(!StringUtils.isBlank(member.getMemberEmail())){
                    Mail mail = new Mail();
                    String message = String.format("[%s] is invalid. <br>",detail.getQuestTitle());
                    message +=String.format("Look for another chance! %s",description);
                    mail.setMessage(message);
                    mail.setTitle("Your Market is Invalid");
                    try {
                        mailService.mailSend(mail);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO answerApprove(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(!detail.getIsActive()){
                response.setCheck(false);
                response.setMessage("Don't active Season.");
                return response;
            }

            if(StringUtils.isBlank(detail.getDraftTx())){
                response.setCheck(false);
                response.setMessage("Draft is Null!");
                return response;
            }

            /*
             * draft transactionId  status 확인
             */
            TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(detail.getDraftTx());
            if(transactionStatus ==null || !WalletCode.TRANSACTION_STATUS_CONFIRM.equals(transactionStatus.getStatus())){
                response.setCheck(false);

                response.setMessage(String.format("Draft status is %s",transactionStatus ==null?"null":transactionStatus.getStatus()));
                return response;
            }


            if(!StringUtils.isBlank(detail.getAnswersTx())){
                response.setCheck(false);
                response.setMessage("Answers is already Registerd!");
                return response;
            }

            /*
             * AnswerApprove Contract 호출
             */
            List<QuestAnswer> list = this.getQuestAnswerList(questKey);
            if(list==null || list.size() < 1){
                response.setCheck(false);
                response.setMessage("No answer data!.");
            }

            List<BigInteger> bigIntegerList = new ArrayList<>();

            for (QuestAnswer answer:list
                 ) {
                BigInteger answerKey = new BigInteger(answer.getQuestAnswerKey());
                System.out.println(String.format("answerKey : s",answerKey));
                bigIntegerList.add(new BigInteger(answer.getQuestAnswerKey()));
            }
            BigInteger questKeyBigInteger = new BigInteger(detail.getQuestKey().replace(SequenceCode.TB_QUEST,""));
            int maxCount = 15;
            TransactionReceipt transactionReceipt = null;
            if (bigIntegerList.size() > maxCount) {
                int maxIndex = (int) Math.ceil(bigIntegerList.size() / maxCount);

                for (int i = 0; i < maxIndex; i++) {
                    List<BigInteger> bigIntegerList_copy = new ArrayList<>();

                    if (i > maxIndex - 1) {
                        for (int a = 0; a < maxCount; a++) {
                            bigIntegerList_copy.add(bigIntegerList.get((i * maxCount) + a));
                        }
                    } else {
                        for (int a = 0; a < bigIntegerList.size() % maxCount; a++) {
                            bigIntegerList_copy.add(bigIntegerList.get((i * maxCount) + a));
                        }
                    }

                    System.out.println("BigIntegerList Copy : {}" + bigIntegerList_copy);
                    transactionReceipt = contractApplicationService.answer(questKeyBigInteger,bigIntegerList_copy);
                }
            } else {
                transactionReceipt = contractApplicationService.answer(questKeyBigInteger,bigIntegerList);
            }




            if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_ANSWER);
                detail.setAnswersTx(transactionReceipt.getTransactionId());
                detail.setUpdateMemberKey(account.getMemberKey());
                questDao.updateQuestStatus(detail);
                response.setCheck(true);
                response.setMessage("success");
            }else{
                response.setCheck(false);
                response.setMessage("Answer approve fail.");
                return response;
            }

        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }

    @Transactional
    public ResponseDataDTO approveMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(!detail.getIsActive()){
                response.setCheck(false);
                response.setMessage("Don't active Season.");
                return response;
            }

            if(StringUtils.isBlank(detail.getAnswersTx())){
                response.setCheck(false);
                response.setMessage("Answers is not Confiremd!");
                return response;
            }

            /*
             * answer transactionId  status 확인
             */
            TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(detail.getAnswersTx());
            if(transactionStatus ==null || !WalletCode.TRANSACTION_STATUS_CONFIRM.equals(transactionStatus.getStatus())){
                response.setCheck(false);

                response.setMessage(String.format("Answers transaction status is %s",transactionStatus ==null?"null":transactionStatus.getStatus()));
                return response;
            }


            if(!StringUtils.isBlank(detail.getApproveTx())){
                response.setCheck(false);
                response.setMessage("Approve is already Registerd!");
                return response;
            }

            /*
             * approveMarket Contract 호출
             */
            BigInteger questKeyBigInteger = new BigInteger(detail.getQuestKey().replace(SequenceCode.TB_QUEST,""));
            TransactionReceipt transactionReceipt = contractApplicationService.approveMarket(questKeyBigInteger);

            if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_APPROVE);
                detail.setQuestStatus(QuestCode.QUEST_STATUS_APPROVE);
                detail.setApproveTx(transactionReceipt.getTransactionId());
                detail.setUpdateMemberKey(account.getMemberKey());
                questDao.updateQuestStatus(detail);
                response.setCheck(true);
                response.setMessage("success");
                //메일 전송
                Member member = new Member();
                member.setMemberKey(detail.getMemberKey());
                member = memberService.getMemberInfoForMemberKey(member);
                if(!StringUtils.isBlank(member.getMemberEmail())){
                    Mail mail = new Mail();
                    String message = String.format("[%s] is approved. <br>",detail.getQuestTitle());
                    message += "Congratulations! <br>";
                    message +=String.format("Approve Transaction Address <a href ='%s'>%s</a>",myConfig.getKlaytnScpe()+"/account/"+detail.getApproveTx(),detail.getApproveTx());
                    mail.setMessage(message);
                    mail.setTitle("Your Market is Approved!");
                    try {
                        mailService.mailSend(mail);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }else{
                response.setCheck(false);
                response.setMessage("Approve fail.");
                return response;
            }

        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO hotMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(!detail.getIsActive()){
                response.setCheck(false);
                response.setMessage("Don't active Season.");
                return response;
            }

            if(detail.getHot()){
                detail.setHot(false);
            }else{
                detail.setHot(true);
            }

            detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_HOT);
            detail.setUpdateMemberKey(account.getMemberKey());
            questDao.updateQuestStatus(detail);
            response.setCheck(true);
            response.setMessage("success");

        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }

    @Transactional
    public ResponseDataDTO finishMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(detail.getCompleted()){
                response.setCheck(false);
                response.setMessage("Already Finished!");
                return response;
            }

            if(!detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_APPROVE)){
                response.setCheck(false);
                response.setMessage("Market is not approved.");
                return response;
            }

            if(detail.getPending()){
                response.setCheck(false);
                response.setMessage("Market is pended.");
                return response;
            }

            /*
             * finishMarket Contract 호출
             */

            TransactionReceipt transactionReceipt= new TransactionReceipt();
            transactionReceipt.setTransactionId(String.format("Finish_%s",detail.getQuestKey()));
            if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_FINISH);
                detail.setCompleted(true);
                detail.setFinishTx(transactionReceipt.getTransactionId());
                detail.setUpdateMemberKey(account.getMemberKey());
                questDao.updateQuestStatus(detail);
                response.setCheck(true);
                response.setMessage("success");
            }else{
                response.setCheck(false);
                response.setMessage("Finish fail.");
                return response;
            }

        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO betting(Betting betting,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(betting.getQuestKey());
        if(detail != null){
            QuestAnswer aParam = new QuestAnswer();
            aParam.setQuestAnswerKey(betting.getQuestAnswerKey());
            QuestAnswer answer = questDao.getQuestAnswerDetail(aParam);
            if(answer == null){
                response.setCheck(false);
                response.setMessage("No data.");
                return response;
            }

            Wallet wallet = walletService.getWalletInfo(account.getMemberKey());
            if(wallet == null){
                response.setCheck(false);
                response.setMessage("No data.");
                return response;
            }

            if(!StringUtils.isBlank(detail.getFinishTx())){
                response.setCheck(false);
                response.setMessage("Already Finished!");
                return response;
            }

            if(!detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_APPROVE)){
                response.setCheck(false);
                response.setMessage("Market is not approved.");
                return response;
            }

            if(detail.getPending()){
                response.setCheck(false);
                response.setMessage("Market is pended.");
                return response;
            }

            String ballance = walletService.getWalletBalance(account.getMemberKey());

            if(Integer.parseInt(ballance)<Integer.parseInt(betting.getBettingCoin())){
                response.setCheck(false);
                response.setMessage("Not enough 'CT' Charge it, please!");
                return response;
            }

            Long l_minimum = detail.getMinimumPay();
            Long l_maximum = detail.getMaximumPay();
            Long bettingCoin = Long.parseLong(betting.getBettingCoin());

            if (bettingCoin < l_minimum) {
                response.setCheck(false);
                response.setMessage("You have to pay more CT than the minimum number of Voting. (Minimum : " + l_minimum + "CT)");
                return response;
            }
            if (bettingCoin > l_maximum) {
                response.setCheck(false);
                response.setMessage("You have to pay less CT than the maximum number of Voting. (Maximum : " + l_maximum + "CT)");
                return response;
            }

            betting.setBettingStatus(QuestCode.BETTING_STATUS_ONGOING);
            betting.setMemberKey(account.getMemberKey());
            questDao.saveBetting(betting);

            //Contract 호출
            BigInteger questKeyBigInteger = new BigInteger(detail.getQuestKey().replace(SequenceCode.TB_QUEST,""));

            BigInteger questAnswerKeyBigInteger = new BigInteger(answer.getQuestAnswerKey().replace(SequenceCode.TB_QUEST_ANSWER,""));

            TransactionReceipt resutApprove = contractApplicationService.approve(wallet,wallet.getWalletAddress(),Convert.toWei(String.valueOf(betting.getBettingCoin()), Convert.Unit.ETHER).toBigInteger());
            TransactionReceipt resutApproveMaster = contractApplicationService.approveMaster(Convert.toWei("5000000000", Convert.Unit.ETHER).toBigInteger());
            System.out.println("resutApprove==>"+resutApprove.getTransactionId());
            System.out.println("resutApproveMaster==>"+resutApproveMaster.getTransactionId());

            if(contractApplicationService.availableBet(questKeyBigInteger,questAnswerKeyBigInteger,new BigInteger(betting.getBettingKey()),Convert.toWei(String.valueOf(betting.getBettingCoin()), Convert.Unit.ETHER).toBigInteger())){
                System.out.println("MARKET IS BET AVAILABLE");
            }else {
                System.out.println("NOT BET AVAILABLE");
            }
            //contractApplicationService.balanceOf(wallet.getWalletAddress());
            //contractApplicationService.transfer(wallet,"0xd210b918A26d7dc444Edae3ed076B3797d31f710",Convert.toWei(String.valueOf(betting.getBettingCoin()), Convert.Unit.ETHER).toBigInteger());
            //contractApplicationService.transferFrom(wallet,wallet.getWalletAddress(),"0xd210b918A26d7dc444Edae3ed076B3797d31f710",Convert.toWei(String.valueOf(betting.getBettingCoin()), Convert.Unit.ETHER).toBigInteger());

            TransactionReceipt transactionReceipt = contractApplicationService.bet(wallet,questKeyBigInteger,questAnswerKeyBigInteger,new BigInteger(betting.getBettingKey()),Convert.toWei(String.valueOf(betting.getBettingCoin()), Convert.Unit.ETHER).toBigInteger());
            //TransactionReceipt transactionReceipt =null;
            if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                betting.setSpenderAddress(wallet.getWalletAddress());
                betting.setTransactionId(transactionReceipt.getTransactionId());
                questDao.updateBetting(betting);
                answer.setTotalAmount("0");
                questDao.updateQuestAnswer(answer);
                questDao.updateQuestTotalAmount(detail);
            }else{
                throw new Exception("Betting fail");
            }

        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }

        response.setCheck(true);
        response.setMessage("success");
        return response;
    }

    public List<QuestAnswer> getQuestAnswerList(String questKey){
        return questDao.getQuestAnswerList(questKey);
    }

    public List<Quest> getQuestListUser(Quest quest){
        return questDao.getQuestListUser(quest);
    }

    public Integer getQuestListUserCnt(Quest quest){
        return questDao.getQuestListUserCnt(quest);
    }

    public Quest getQuestDetailUser(Quest quest){
        return questDao.getQuestDetailUser(quest);
    }

    public List<Betting> getBettingList(Betting betting){
        return questDao.getBettingList(betting);
    }

    public ResponseDataDTO getSuccessInfo(String selectedQuestKey,String selectedAnswerKey) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        if (!StringUtils.isBlank(selectedQuestKey) && !StringUtils.isBlank(selectedAnswerKey)) {
            Quest detail = questDao.getQuestDetail(selectedQuestKey);
            if (detail != null) {
                QuestAnswer aParam = new QuestAnswer();
                aParam.setQuestAnswerKey(selectedAnswerKey);
                QuestAnswer selectedAnswer =questDao.getQuestAnswerDetail(aParam);
                if(selectedAnswer !=null){
                    // 1. Get Total CT
                    float market_total_ct = Float.parseFloat(detail.getTotalAmount());
                    // 2. Get Answer Total CT
                    float answer_total_ct = Float.parseFloat(selectedAnswer.getTotalAmount());

                    // 3. Get Fee
                    double cojam_ct = Math.floor(market_total_ct * Long.parseLong(detail.getCojamFee()) / 100);
                    double creator_ct = Math.floor(market_total_ct * Long.parseLong(detail.getCreatorFee()) / 100 + Long.parseLong(detail.getCreatorPay()));
                    double charity_ct = Math.floor(market_total_ct * Long.parseLong(detail.getCharityFee()) / 100);
                    double real_total_ct = market_total_ct - cojam_ct - creator_ct - charity_ct;
                    double remain_ct = real_total_ct;

                    // 4. Set Attributes
                    Map<String,Object> json = new HashMap<>();
                    json.put("detail", detail);
                    json.put("cojam_ct", cojam_ct);
                    json.put("creator_ct", creator_ct);
                    json.put("charity_ct", charity_ct);
                    json.put("real_total_ct", real_total_ct);
                    json.put("answer_total_ct", answer_total_ct);
                    json.put("market_total_ct", market_total_ct);
                    json.put("COJAM_CHARITY_ADDRESS", "COJAM_CHARITY_ADDRESS");

                    // 5. Get Scale of Betting
                    double magnification = Math.floor(real_total_ct / answer_total_ct * 100);

                    // 6. Get/Set List of Address with CT
                    Betting bparam = new Betting();
                    bparam.setQuestKey(selectedQuestKey);
                    bparam.setQuestAnswerKey(selectedAnswerKey);
                    List<Betting> bettingList = questDao.getBettingList(bparam);

                    Map<String,Object> gospel = new HashMap<>();
                    for (Betting betting : bettingList
                         ) {
                        String address = betting.getSpenderAddress();
                        float b_coin = Long.parseLong(betting.getBettingCoin());
                        double r_coin = Math.floor(b_coin * magnification / 100);

                        if (address != null) {
                            Object old = gospel.get(address);

                            remain_ct -= r_coin;

                            if(old != null) {
                                double old_coin = (double) old;
                                r_coin = r_coin + old_coin;

                                gospel.put(address, r_coin);
                            } else {
                                gospel.put(address, r_coin);
                            }
                        }
                    }

                    if (detail.getCreatorAddress() != null) {
                        Object old = gospel.get(detail.getCreatorAddress());

                        if (old != null) {
                            double old_coin = (double) old;
                            double r_coin = creator_ct + old_coin;
                            gospel.put(detail.getCreatorAddress(), r_coin);
                        } else {
                            gospel.put(detail.getCreatorAddress(), creator_ct);
                        }

                        json.put("CREATOR_ADDRESS", detail.getCreatorAddress());

                    } else {
                        json.put("CREATOR_ADDRESS", "is Null");

                        Object old = gospel.get("COJAM_OWNER_ADDRESS");

                        if (old != null) {
                            double old_coin = (double) old;
                            double r_coin = creator_ct + old_coin;

                            gospel.put("COJAM_OWNER_ADDRESS", r_coin);
                        } else {
                            gospel.put("COJAM_OWNER_ADDRESS", creator_ct);
                        }
                    }

                    // 7. set COJAM Salary
                    gospel.put("COJAM_FEE_ADDRESS", cojam_ct);

                    // 8. set CHARITY Salary
                    gospel.put("COJAM_CHARITY_ADDRESS", charity_ct);

                    // 9. set Remain CT
                    json.put("remain_ct", remain_ct);

                    json.put("transfer_gospel", gospel);

                    response.setCheck(true);
                    response.setItem(json);
                }else {
                    response.setCheck(false);
                    response.setMessage("answer is no data!");
                }
            }else {
                response.setCheck(false);
                response.setMessage("Season is Null!");
            }

        }else{
            response.setCheck(false);
            response.setMessage("Parameter is wrong!");
        }




        return response;
    }

    @Transactional
    public ResponseDataDTO successMarket(String selectedQuestKey,String selectedAnswerKey,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        if (!StringUtils.isBlank(selectedQuestKey) && !StringUtils.isBlank(selectedAnswerKey)) {
            Quest detail = questDao.getQuestDetail(selectedQuestKey);
            if (detail != null) {
                QuestAnswer aParam = new QuestAnswer();
                aParam.setQuestAnswerKey(selectedAnswerKey);
                QuestAnswer selectedAnswer =questDao.getQuestAnswerDetail(aParam);
                if(selectedAnswer !=null){
                    if (detail.getCompleted() == null || !detail.getCompleted()) {
                        response.setCheck(false);
                        response.setMessage("Market is not Finished!");
                    }else{
                        //finish status confirm 확인
                        //cnotract successMarket 호출
                        TransactionReceipt transactionReceipt= new TransactionReceipt();
                        transactionReceipt.setTransactionId(String.format("SUCCESS_%s",detail.getQuestKey()));
                        if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                            detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_SUCCESS);
                            detail.setQuestStatus(QuestCode.QUEST_STATUS_SUCCESS);
                            detail.setSuccessTx(transactionReceipt.getTransactionId());
                            detail.setUpdateMemberKey(account.getMemberKey());
                            questDao.updateQuestStatus(detail);
                            selectedAnswer.setSelected(true);
                            questDao.updateQuestAnswer(selectedAnswer);
                            response.setCheck(true);
                            Member member = new Member();
                            member.setMemberKey(detail.getMemberKey());
                            member = memberService.getMemberInfoForMemberKey(member);

                            if(!StringUtils.isBlank(member.getMemberEmail())){
                                Mail mail = new Mail();
                                String message = String.format("[%s] is success. <br>",detail.getQuestTitle());
                                message +="Congratulations! <br>";
                                message +=String.format("Selected Answer is [%s] <br>",selectedAnswer.getAnswerTitle());
                                message +=String.format("Success Transaction Address <a href ='%s'>%s</a>",myConfig.getKlaytnScpe()+"/account/"+detail.getSuccessTx(),detail.getSuccessTx());
                                mail.setMessage(message);
                                mail.setTitle("Your Market is Success!");
                                try {
                                    mailService.mailSend(mail);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            response.setMessage("Success if Fail!");
                            response.setCheck(false);
                        }

                    }

                }else {
                    response.setCheck(false);
                    response.setMessage("answer is no data!");
                }
            }else {
                response.setCheck(false);
                response.setMessage("Season is Null!");
            }

        }else{
            response.setCheck(false);
            response.setMessage("Parameter is wrong!");
        }




        return response;
    }


    @Transactional
    public ResponseDataDTO adjournMarket(String adjournQuestKey,String adjournDesc,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        if (!StringUtils.isBlank(adjournDesc) && !StringUtils.isBlank(adjournQuestKey)) {
            Quest detail = questDao.getQuestDetail(adjournQuestKey);
            if (detail != null) {
                if (detail.getCompleted() == null || !detail.getCompleted()) {
                    response.setCheck(false);
                    response.setMessage("Market is not Finished!");
                }else{
                    //finish status confirm 확인
                    //cnotract adjournMarket 호출
                    TransactionReceipt transactionReceipt= new TransactionReceipt();
                    transactionReceipt.setTransactionId(String.format("ADJOURN_%s",detail.getQuestKey()));
                    if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                        detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_ADJOURN);
                        detail.setQuestStatus(QuestCode.QUEST_STATUS_ADJOURN);
                        detail.setAdjournTx(transactionReceipt.getTransactionId());
                        detail.setQuestDesc(adjournDesc);
                        detail.setUpdateMemberKey(account.getMemberKey());
                        questDao.updateQuestStatus(detail);
                        response.setCheck(true);
                        Member member = new Member();
                        member.setMemberKey(detail.getMemberKey());
                        member = memberService.getMemberInfoForMemberKey(member);

                        if(!StringUtils.isBlank(member.getMemberEmail())){
                            Mail mail = new Mail();
                            String message = String.format("[%s] is adjourn!. <br>",detail.getQuestTitle());
                            message +="Look for another chance! <br>"+adjournDesc;
                            mail.setMessage(message);
                            mail.setTitle("Your Market is Adjourn!");
                            try {
                                mailService.mailSend(mail);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }else{
                        response.setMessage("Success if Fail!");
                        response.setCheck(false);
                    }

                }


            }else {
                response.setCheck(false);
                response.setMessage("Season is Null!");
            }

        }else{
            response.setCheck(false);
            response.setMessage("Parameter is wrong!");
        }




        return response;
    }

    public ResponseDataDTO getAdjournInfo(String adjournQuestKey) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        if (!StringUtils.isBlank(adjournQuestKey)) {
            Quest detail = questDao.getQuestDetail(adjournQuestKey);
            Map<String,Object> json = new HashMap<>();
            if (detail != null) {
                float market_total_ct = 0;
                Map<String,Object> gospel = new HashMap<>();

                // 1. Get Total CT and transfer list
                Betting param = new Betting();
                param.setQuestKey(adjournQuestKey);
                List<Betting> mBettingList = questDao.getBettingList(param);
                for (Betting betting : mBettingList) {
                    Long bc = Long.parseLong(betting.getBettingCoin());
                    market_total_ct += bc;

                    String address = betting.getSpenderAddress();

                    if (address != null) {
                        Object old = gospel.get(address);

                        if (old != null) {
                            Long old_bc = (Long) old;
                            bc = bc + old_bc;

                            gospel.put(address, bc);
                        } else {
                            gospel.put(address, bc);
                        }
                    }
                }

                // 2. Get Fee
                double cojam_ct = market_total_ct * Long.parseLong(detail.getCojamFee()) / 100;
                double creator_ct = Math.floor(market_total_ct * Long.parseLong(detail.getCreatorFee()) / 100 + Long.parseLong(detail.getCreatorPay()));
                double charity_ct = Math.floor(market_total_ct * Long.parseLong(detail.getCharityFee()) / 100);
                double real_total_ct = market_total_ct - cojam_ct - creator_ct - charity_ct;

                json.put("cjm_key", detail.getQuestKey());
                json.put("cojam_ct", cojam_ct);
                json.put("creator_ct", creator_ct);
                json.put("charity_ct", charity_ct);
                json.put("real_total_ct", real_total_ct);
                json.put("market_total_ct", market_total_ct);
                json.put("COJAM_CHARITY_ADDRESS", "COJAM_CHARITY_ADDRESS");
                json.put("transfer_gospel", gospel);

                response.setCheck(true);
                response.setItem(json);
            }else {
                response.setCheck(false);
                response.setMessage("Season is Null!");
            }

        }else{
            response.setCheck(false);
            response.setMessage("Parameter is wrong!");
        }




        return response;
    }

    public List<MyVoting> getMyVotingList(MyVoting myVoting){
        return questDao.getMyVotingList(myVoting);
    }

    public Integer getMyVotingListCnt(MyVoting myVoting){
        return questDao.getMyVotingListCnt(myVoting);
    }
}
