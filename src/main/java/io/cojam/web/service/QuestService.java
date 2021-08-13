package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.QuestCode;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.QuestDao;
import io.cojam.web.domain.*;
import io.cojam.web.domain.wallet.Transaction;
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
import java.text.DecimalFormat;
import java.util.*;


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

    @Autowired
    TransactionService transactionService;

    @Autowired
    PushMessageService pushMessageService;



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
        quest.setEndDateTime(Timestamp.valueOf(quest.getEndUtcDateTime()));
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
                    BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);
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
    public ResponseDataDTO unPendingMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(!detail.getPending()){
                response.setCheck(false);
                response.setMessage("It is already unpended.");
                return response;
            }else{
                detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_UNPEND);
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
                    message +="\n";
                    message += "COJAM LIMITED";
                    message +="\n";
                    message += "E-Mail : ask@cojam.io";
                    message +="\n";
                    message += "Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland";
                    message +="\n";
                    message += "Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea";
                    message +="\n";
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
                bigIntegerList.add(new BigInteger(answer.getQuestAnswerKey()));
            }
            BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);

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
            BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);
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
                    message +="\n";
                    message += "COJAM LIMITED";
                    message +="\n";
                    message += "E-Mail : ask@cojam.io";
                    message +="\n";
                    message += "Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland";
                    message +="\n";
                    message += "Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea";
                    message +="\n";
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
    public ResponseDataDTO pushMarket(String questKey,Account account){
        ResponseDataDTO response = new ResponseDataDTO();

        Quest detail = questDao.getQuestDetail(questKey);

        if(detail != null){
            if(!detail.getIsActive()){
                response.setCheck(false);
                response.setMessage("Don't active Season.");
                return response;
            }

            if(detail.getPush()){
                detail.setPush(false);
            }else{
                detail.setPush(true);
            }

            detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_PUSH);
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
            BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);
            TransactionReceipt transactionReceipt = contractApplicationService.finishMarket(questKeyBigInteger);

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

            if(detail.getMarketClosed()){
                response.setCheck(false);
                response.setMessage("Voting is closed.");
                return response;
            }

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

            Member param = new Member();
            param.setMemberKey(account.getMemberKey());
            Member member = memberService.getMemberInfoForMemberKey(param);
            if(member.getWalletLock()){
                response.setCheck(false);
                response.setMessage("Your Wallet is Lock!");
                return response;
            }

            String ballance = walletService.getWalletBalance(account.getMemberKey());

            if(Float.parseFloat(ballance)<Float.parseFloat(betting.getBettingCoin())){
                response.setCheck(false);
                response.setMessage("Please check your balance.");
                return response;
            }

            Long l_minimum = detail.getMinimumPay();
            Long l_maximum = detail.getMaximumPay();
            Long bettingCoin = Long.parseLong(betting.getBettingCoin());

            if (bettingCoin < l_minimum) {
                response.setCheck(false);
                response.setMessage("You have to vote more CT than the minimum number of voting. (Minimum : " + l_minimum + "CT)");
                return response;
            }
            if (bettingCoin > l_maximum) {
                response.setCheck(false);
                response.setMessage("You have to vote more CT than the maximum number of voting. (Minimum : " + l_maximum + "CT)");
                return response;
            }

            betting.setBettingStatus(QuestCode.BETTING_STATUS_ONGOING);
            betting.setMemberKey(account.getMemberKey());
            questDao.saveBetting(betting);

            answer.setTotalAmount("0");
            questDao.updateQuestAnswer(answer);
            questDao.updateQuestTotalAmount(detail);
            //Contract 호출
            BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);


            BigInteger questAnswerKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(answer.getQuestAnswerKey(),SequenceCode.TB_QUEST_ANSWER);


            BigInteger amount = Convert.toWei(String.valueOf(betting.getBettingCoin()), Convert.Unit.ETHER).toBigInteger();

            TransactionReceipt resultApprove = contractApplicationService.approve(wallet,myConfig.getMarketAddress(),amount);


            TransactionReceipt transactionReceipt = contractApplicationService.bet(wallet,questKeyBigInteger,questAnswerKeyBigInteger,new BigInteger(betting.getBettingKey()),amount);

            if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                betting.setSpenderAddress(wallet.getWalletAddress());
                betting.setTransactionId(transactionReceipt.getTransactionId());
                questDao.updateBetting(betting);
                //SAVE TRANSACTION
                Transaction transaction = new Transaction();
                transaction.setAmount(amount+"");
                transaction.setRecipientAddress(transactionReceipt.getToAddress());
                transaction.setSpenderAddress(wallet.getWalletAddress());
                transaction.setTransactionId(transactionReceipt.getTransactionId());
                transaction.setTransactionType(WalletCode.TRANSACTION_TYPE_BETTION_SEND);
                transactionService.saveTransaction(transaction);
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


    @Transactional
    public ResponseDataDTO successBetting(Betting betting,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();
        if(StringUtils.isBlank(betting.getBettingKey())){
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        Betting bettingDetail = questDao.getBettingDetail(betting);
        if(bettingDetail == null){
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        betting = bettingDetail;

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

            if (QuestCode.QUEST_STATUS_TYPE_SUCCESS.equals(detail.getQuestStatus())) {
                if(!answer.getSelected()){
                    response.setCheck(false);
                    response.setMessage("No selected answer.");
                    return response;
                }
            }


            Wallet wallet = walletService.getWalletInfo(account.getMemberKey());
            if(wallet == null){
                response.setCheck(false);
                response.setMessage("No data.");
                return response;
            }

            if(!detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_SUCCESS) && !detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_ADJOURN)){
                response.setCheck(false);
                response.setMessage("Market is not finished.");
                return response;
            }

            TransactionStatus transactionStatus;
            String status;



            if (QuestCode.QUEST_STATUS_TYPE_ADJOURN.equals(detail.getQuestStatus())) {
                transactionStatus = transactionApiService.getTransactionStatusById(detail.getAdjournTx());
                status = transactionStatus.getStatus();
            } else if (QuestCode.QUEST_STATUS_TYPE_SUCCESS.equals(detail.getQuestStatus())) {
                transactionStatus = transactionApiService.getTransactionStatusById(detail.getSuccessTx());
                status = transactionStatus.getStatus();
            } else {
                status = "";
            }

            if (!WalletCode.TRANSACTION_STATUS_CONFIRM.equals(status)) {
                response.setCheck(false);
                response.setMessage(String.format("transaction status is %s",StringUtils.isBlank(status)?"null":status));
                return response;
            }

            if(!StringUtils.isBlank(betting.getReceiveAddress())){
                TransactionStatus bettingStatus = transactionApiService.getTransactionStatusById(betting.getReceiveAddress());
                String bStatus = bettingStatus.getStatus();
                if (WalletCode.TRANSACTION_STATUS_CONFIRM.equals(bStatus)) {
                    response.setCheck(false);
                    response.setMessage("You've already been rewarded!");
                    return response;
                }else if(WalletCode.TRANSACTION_STATUS_REQUESTED.equals(bStatus) || WalletCode.TRANSACTION_STATUS_PENDING.equals(bStatus)){
                    response.setCheck(false);
                    response.setMessage("Reward is in progress.");
                    return response;
                }
            }

            //Contract 호출
            BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);



            TransactionReceipt transactionReceipt = contractApplicationService.receiveToken(wallet,questKeyBigInteger,new BigInteger(betting.getBettingKey()));

            if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                double r_coin = 0;
                if (QuestCode.QUEST_STATUS_TYPE_SUCCESS.equals(detail.getQuestStatus())) {
                    // 1. Get Total CT
                    float market_total_ct = Float.parseFloat(detail.getTotalAmount());
                    // 2. Get Answer Total CT
                    float answer_total_ct = Float.parseFloat(answer.getTotalAmount());

                    // 3. Get Fee
                    double cojam_ct = market_total_ct * Long.parseLong(detail.getCojamFee()) / 100;
                    double creator_ct = market_total_ct * Long.parseLong(detail.getCreatorFee()) / 100 + Long.parseLong(detail.getCreatorPay());
                    double charity_ct = market_total_ct * Long.parseLong(detail.getCharityFee()) / 100;
                    double real_total_ct = market_total_ct - cojam_ct - creator_ct - charity_ct;
                    double magnification = real_total_ct / answer_total_ct * 100;


                    float b_coin = Long.parseLong(betting.getBettingCoin());
                    r_coin = b_coin * magnification / 100;
                }else if (QuestCode.QUEST_STATUS_TYPE_ADJOURN.equals(detail.getQuestStatus())) {
                    r_coin = Long.parseLong(betting.getBettingCoin());
                }


                betting.setReceiveAddress(transactionReceipt.getTransactionId());
                betting.setBettingStatus(QuestCode.BETTING_STATUS_SUCCESS);
                betting.setReceiveAmount(String.valueOf(r_coin));
                questDao.updateBettingSuccess(betting);

                //SAVE TRANSACTION
                Transaction transaction = new Transaction();
                transaction.setAmount(Convert.toWei(String.valueOf(r_coin), Convert.Unit.ETHER).toString());
                transaction.setRecipientAddress(wallet.getWalletAddress());
                transaction.setSpenderAddress(myConfig.getMarketAddress());
                transaction.setTransactionId(transactionReceipt.getTransactionId());
                transaction.setTransactionType(WalletCode.TRANSACTION_TYPE_BETTION_RECEIVE);
                transactionService.saveTransaction(transaction);

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

    @Transactional
    public ResponseDataDTO rewardInfo(Betting betting,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();
        Map<String,Object> responseMap = new HashMap<>();
        if(StringUtils.isBlank(betting.getBettingKey())){
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        Betting bettingDetail = questDao.getBettingDetail(betting);
        if(bettingDetail == null){
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        betting = bettingDetail;

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


            if(!detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_SUCCESS) && !detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_ADJOURN)){
                response.setCheck(false);
                response.setMessage("Market is not finished.");
                return response;
            }


            double r_coin = 0;
            if (QuestCode.QUEST_STATUS_TYPE_SUCCESS.equals(detail.getQuestStatus())) {
                // 1. Get Total CT
                float market_total_ct = Float.parseFloat(detail.getTotalAmount());
                // 2. Get Answer Total CT
                float answer_total_ct = Float.parseFloat(answer.getTotalAmount());

                // 3. Get Fee
                double cojam_ct = market_total_ct * Long.parseLong(detail.getCojamFee()) / 100;
                double creator_ct = market_total_ct * Long.parseLong(detail.getCreatorFee()) / 100 + Long.parseLong(detail.getCreatorPay());
                double charity_ct = market_total_ct * Long.parseLong(detail.getCharityFee()) / 100;
                double real_total_ct = market_total_ct - cojam_ct - creator_ct - charity_ct;
                double magnification = real_total_ct / answer_total_ct * 100;


                float b_coin = Long.parseLong(betting.getBettingCoin());
                r_coin = b_coin * magnification / 100;
                responseMap.put("type","S");

            }else if (QuestCode.QUEST_STATUS_TYPE_ADJOURN.equals(detail.getQuestStatus())) {
                r_coin = Long.parseLong(betting.getBettingCoin());
                responseMap.put("type","A");
            }
            responseMap.put("betQuestKey",detail.getQuestKey());
            responseMap.put("rewardCoin",r_coin);



        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        response.setItem(responseMap);
        response.setCheck(true);
        response.setMessage("success");
        return response;
    }


    @Transactional
    public ResponseDataDTO noRewardInfo(Betting betting,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();
        Map<String,Object> responseMap = new HashMap<>();
        if(StringUtils.isBlank(betting.getBettingKey())){
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        Betting bettingDetail = questDao.getBettingDetail(betting);
        if(bettingDetail == null){
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        betting = bettingDetail;

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


            if(!detail.getQuestStatus().equals(QuestCode.QUEST_STATUS_SUCCESS)){
                response.setCheck(false);
                response.setMessage("Market is not finished.");
                return response;
            }



            responseMap.put("betQuestKey",detail.getQuestKey());
            responseMap.put("answerList",questDao.getMyBettingAnswerList(answer));



        }else {
            response.setCheck(false);
            response.setMessage("No data.");
            return response;
        }
        response.setItem(responseMap);
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

    public List<Betting> getBettingChartList(Betting betting){
        return questDao.getBettingChartList(betting);
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
                    float cojam_ct = market_total_ct * Float.parseFloat(detail.getCojamFee()) / 100;
                    cojam_ct = (float) ((int)(cojam_ct * 100) / 100.0);

                    float creator_ct = market_total_ct * Float.parseFloat(detail.getCreatorFee()) / 100 + Float.parseFloat(detail.getCreatorPay());
                    creator_ct = (float) ((int)(creator_ct * 100) / 100.0);

                    float charity_ct = market_total_ct * Float.parseFloat(detail.getCharityFee()) / 100;
                    charity_ct = (float) ((int)(charity_ct * 100) / 100.0);

                    float real_total_ct = market_total_ct - cojam_ct - creator_ct - charity_ct;
                    float remain_ct = real_total_ct;

                    // 4. Set Attributes
                    Map<String,Object> json = new HashMap<>();
                    json.put("detail", detail);
                    json.put("cojam_ct", cojam_ct);
                    json.put("creator_ct", creator_ct);
                    json.put("charity_ct", charity_ct);
                    json.put("real_total_ct", real_total_ct);
                    json.put("answer_total_ct", answer_total_ct);
                    json.put("market_total_ct", market_total_ct);
                    json.put("COJAM_CHARITY_ADDRESS", myConfig.getCharityAddress());

                    // 5. Get Scale of Betting
                    float magnification = real_total_ct / answer_total_ct * 100;
                    //magnification = (float) ((int)(magnification * 100) / 100.0);
                    // 6. Get/Set List of Address with CT
                    Betting bparam = new Betting();
                    bparam.setQuestKey(selectedQuestKey);
                    bparam.setQuestAnswerKey(selectedAnswerKey);
                    List<Betting> bettingList = questDao.getBettingList(bparam);

                    Map<String,Object> gospel = new HashMap<>();
                    for (Betting betting : bettingList
                         ) {
                        String address = betting.getSpenderAddress();
                        float b_coin = Float.parseFloat(betting.getBettingCoin());
                        float r_coin = b_coin * magnification / 100;

                        if (address != null) {
                            Object old = gospel.get(address);
                            remain_ct -= r_coin;
                            if(old != null) {
                                float old_coin = (float) old;
                                r_coin = r_coin + old_coin;
                                r_coin = (float) ((int)(r_coin * 100) / 100.0);
                                gospel.put(address, r_coin);
                            } else {
                                r_coin = (float) ((int)(r_coin * 100) / 100.0);
                                gospel.put(address, r_coin);
                            }
                        }
                    }

                    if (detail.getCreatorAddress() != null) {
                        Object old = gospel.get(detail.getCreatorAddress());

                        if (old != null) {
                            float old_coin = (float) old;
                            float r_coin = creator_ct + old_coin;
                            gospel.put(detail.getCreatorAddress(), r_coin);
                        } else {
                            gospel.put(detail.getCreatorAddress(), creator_ct);
                        }

                        json.put("CREATOR_ADDRESS", detail.getCreatorAddress());

                    } else {
                        json.put("CREATOR_ADDRESS", "is Null");

                        Object old = gospel.get("COJAM_OWNER_ADDRESS");

                        if (old != null) {
                            float old_coin = (float) old;
                            float r_coin = creator_ct + old_coin;

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
                    remain_ct = (float) ((int)(remain_ct * 100) / 100.0);
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
                        TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(detail.getFinishTx());
                        if(transactionStatus ==null || !WalletCode.TRANSACTION_STATUS_CONFIRM.equals(transactionStatus.getStatus())){
                            response.setCheck(false);
                            response.setMessage(String.format("Finish transaction status is %s",transactionStatus ==null?"null":transactionStatus.getStatus()));
                            return response;
                        }

                        //
                        if(!StringUtils.isBlank(detail.getSuccessTx())){
                            TransactionStatus successStatus = transactionApiService.getTransactionStatusById(detail.getSuccessTx());
                            String bStatus = successStatus.getStatus();
                            if (WalletCode.TRANSACTION_STATUS_CONFIRM.equals(bStatus)) {
                                response.setCheck(false);
                                response.setMessage("You've already been rewarded!");
                                return response;
                            }else if(WalletCode.TRANSACTION_STATUS_REQUESTED.equals(bStatus) || WalletCode.TRANSACTION_STATUS_PENDING.equals(bStatus)){
                                response.setCheck(false);
                                response.setMessage("Reward is in progress.");
                                return response;
                            }
                        }



                        //cnotract successMarket 호출
                        BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);
                        BigInteger questAnswerKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(selectedAnswerKey,SequenceCode.TB_QUEST_ANSWER);

                        TransactionReceipt transactionReceipt= contractApplicationService.successMarket(questKeyBigInteger,questAnswerKeyBigInteger);
                        if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null && !StringUtils.isBlank(transactionReceipt.getTransactionId())){
                            detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_SUCCESS);
                            detail.setQuestStatus(QuestCode.QUEST_STATUS_SUCCESS);
                            detail.setSuccessTx(transactionReceipt.getTransactionId());
                            detail.setUpdateMemberKey(account.getMemberKey());
                            questDao.updateQuestStatus(detail);
                            selectedAnswer.setSelected(true);
                            questDao.updateQuestAnswer(selectedAnswer);

                            // 1. Get Total CT
                            float market_total_ct = Float.parseFloat(detail.getTotalAmount());

                            // 3. Get Fee
                            double creator_ct = market_total_ct * Long.parseLong(detail.getCreatorFee()) / 100 + Long.parseLong(detail.getCreatorPay());

                            //SAVE TRANSACTION
                            Transaction transaction = new Transaction();
                            transaction.setAmount(Convert.toWei(String.valueOf(creator_ct), Convert.Unit.ETHER).toString());
                            transaction.setRecipientAddress(detail.getCreatorAddress());
                            transaction.setSpenderAddress(myConfig.getMarketAddress());
                            transaction.setTransactionId(transactionReceipt.getTransactionId());
                            transaction.setTransactionType(WalletCode.TRANSACTION_TYPE_CREATOR_FEE);
                            transactionService.saveTransaction(transaction);


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
                                message +="\n";
                                message += "COJAM LIMITED";
                                message +="\n";
                                message += "E-Mail : ask@cojam.io";
                                message +="\n";
                                message += "Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland";
                                message +="\n";
                                message += "Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea";
                                message +="\n";
                                mail.setMessage(message);
                                mail.setTitle("Your Market is Success!");
                                try {
                                    mailService.mailSend(mail);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            //PUSH 발송
                            if(detail.getPush()){
                                pushMessageService.sendPushMessage("QT_S",detail.getQuestKey());
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
                    return response;
                }else{
                    //finish status confirm 확인
                    TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(detail.getFinishTx());
                    if(transactionStatus ==null || !WalletCode.TRANSACTION_STATUS_CONFIRM.equals(transactionStatus.getStatus())){
                        response.setCheck(false);
                        response.setMessage(String.format("Finish transaction status is %s",transactionStatus ==null?"null":transactionStatus.getStatus()));
                        return response;
                    }

                    if(!StringUtils.isBlank(detail.getAdjournTx())){
                        response.setCheck(false);
                        response.setMessage("It is already adjourn.");
                        return response;
                    }

                    //cnotract adjournMarket 호출
                    BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);


                    TransactionReceipt transactionReceipt= contractApplicationService.adjournMarket(questKeyBigInteger);
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

                        //PUSH 발송
                        if(detail.getPush()){
                            pushMessageService.sendPushMessage("QT_A",detail.getQuestKey());
                        }


                        if(!StringUtils.isBlank(member.getMemberEmail())){
                            Mail mail = new Mail();
                            String message = String.format("[%s] is adjourn!. <br>",detail.getQuestTitle());
                            message +="Look for another chance! <br>"+adjournDesc;
                            message +="\n";
                            message += "COJAM LIMITED";
                            message +="\n";
                            message += "E-Mail : ask@cojam.io";
                            message +="\n";
                            message += "Address (Ireland) : The Tara Building, Tara street, Dublin 2, Ireland";
                            message +="\n";
                            message += "Address (Korea) : 373 Gangnam-daero, Seocho-gu, Seoul, Republic of Korea";
                            message +="\n";
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


    @Transactional
    public ResponseDataDTO retrieveMarket(String retrieveQuestKey,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        if (!StringUtils.isBlank(retrieveQuestKey)) {
            Quest detail = questDao.getQuestDetail(retrieveQuestKey);
            if (detail != null) {
                if (detail.getCompleted() == null || !detail.getCompleted()) {
                    response.setCheck(false);
                    response.setMessage("Market is not Finished!");
                    return response;
                }else{
                    if (!StringUtils.isBlank(detail.getRetrieveTx())) {
                        response.setCheck(false);
                        response.setMessage("Market is already retrieve!");
                        return response;
                    }

                    if (StringUtils.isBlank(detail.getSuccessTx())) {
                        response.setCheck(false);
                        response.setMessage("Market is not Success!");
                        return response;
                    }

                    Calendar getToday = Calendar.getInstance();
                    getToday.setTime(new Date()); //금일 날짜

                    Date date = detail.getSuccessDateTime();
                    Calendar cmpDate = Calendar.getInstance();
                    cmpDate.setTime(date); //특정 일자

                    long diffSec = (getToday.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
                    long diffDays = diffSec / (24*60*60); //일자수 차이


                    System.out.println(diffDays + "일 차이");
                    if(diffDays <= 180){
                        response.setCheck(false);
                        response.setMessage("Market can be retrieved later 180 days from success!");
                        return response;
                    }

                    //success status confirm 확인
                    TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(detail.getSuccessTx());

                    if(transactionStatus ==null || !WalletCode.TRANSACTION_STATUS_CONFIRM.equals(transactionStatus.getStatus())){
                        response.setCheck(false);
                        response.setMessage(String.format("Success transaction status is %s",transactionStatus ==null?"null":transactionStatus.getStatus()));
                        return response;
                    }
                    //cnotract retrieveMarket 호출
                    BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);


                    contractApplicationService.setAccount("remainAccount");
                    TransactionReceipt transactionReceipt= contractApplicationService.retrieveMarket(questKeyBigInteger);
                    if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                        detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_RETRIEVE);
                        detail.setRetrieveTx(transactionReceipt.getTransactionId());
                        detail.setUpdateMemberKey(account.getMemberKey());
                        questDao.updateQuestStatus(detail);
                        response.setCheck(true);
                    }else{
                        response.setMessage("Retrieve is Fail!");
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

    @Transactional
    public ResponseDataDTO adjournRetrieveMarket(String retrieveQuestKey,Account account) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();

        if (!StringUtils.isBlank(retrieveQuestKey)) {
            Quest detail = questDao.getQuestDetail(retrieveQuestKey);
            if (detail != null) {
                if (detail.getCompleted() == null || !detail.getCompleted()) {
                    response.setCheck(false);
                    response.setMessage("Market is not Finished!");
                    return response;
                }else{
                    if (!StringUtils.isBlank(detail.getRetrieveTx())) {
                        response.setCheck(false);
                        response.setMessage("Market is already retrieve!");
                        return response;
                    }

                    if (StringUtils.isBlank(detail.getAdjournTx())) {
                        response.setCheck(false);
                        response.setMessage("Market is not Adjourn!");
                        return response;
                    }

                    Calendar getToday = Calendar.getInstance();
                    getToday.setTime(new Date()); //금일 날짜

                    Date date = detail.getAdjournDateTime();
                    Calendar cmpDate = Calendar.getInstance();
                    cmpDate.setTime(date); //특정 일자

                    long diffSec = (getToday.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
                    long diffDays = diffSec / (24*60*60); //일자수 차이


                    System.out.println(diffDays + "일 차이");
                    if(diffDays <= 180){
                        response.setCheck(false);
                        response.setMessage("Market can be retrieved later 180 days from adjourn!");
                        return response;
                    }

                    //success status confirm 확인
                    TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(detail.getAdjournTx());

                    if(transactionStatus ==null || !WalletCode.TRANSACTION_STATUS_CONFIRM.equals(transactionStatus.getStatus())){
                        response.setCheck(false);
                        response.setMessage(String.format("Adjourn transaction status is %s",transactionStatus ==null?"null":transactionStatus.getStatus()));
                        return response;
                    }
                    //cnotract retrieveMarket 호출
                    BigInteger questKeyBigInteger = sequenceService.changeSequenceStringToBigInteger(detail.getQuestKey(),SequenceCode.TB_QUEST);

                    contractApplicationService.setAccount("remainAccount");
                    TransactionReceipt transactionReceipt= contractApplicationService.retrieveMarket(questKeyBigInteger);
                    if(transactionReceipt!=null && transactionReceipt.getTransactionId() !=null){
                        detail.setStatusType(QuestCode.QUEST_STATUS_TYPE_RETRIEVE);
                        detail.setRetrieveTx(transactionReceipt.getTransactionId());
                        detail.setUpdateMemberKey(account.getMemberKey());
                        questDao.updateQuestStatus(detail);
                        response.setCheck(true);
                    }else{
                        response.setMessage("Retrieve is Fail!");
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

    public List<Question> getHomeQuestList(Question question){
        return questDao.getHomeQuestList(question);
    }
}
