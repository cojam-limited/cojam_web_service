package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.QuestCode;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.QuestDao;
import io.cojam.web.domain.*;
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

import java.net.URLDecoder;
import java.util.List;


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

    public Integer getQuestListAdminCnt(Quest quest){
        return questDao.getQuestListAdminCnt(quest);
    }

    public List<Quest> getQuestListAdmin(Quest quest){
        return questDao.getQuestListAdmin(quest);
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
        if(!StringUtils.isBlank(quest.getSnsUrl())){
            SnsInfo snsInfo =getSocialMediaCheck(quest.getSnsUrl());
            if(snsInfo.isCheck()){
                //유튜브일 경우 썸네일 저장
                if("Y".equals(snsInfo.getSnsType())){
                    FileInfo fileInfo =fileService.fileUrlUploadYoutube(account.getMemberKey(),snsInfo.getSnsId(),SequenceCode.TB_QUEST,quest.getQuestKey());
                    if(fileInfo!= null && fileInfo.getFileKey()!=null){
                        quest.setFileKey(fileInfo.getFileKey());
                    }
                }else if(StringUtils.isBlank(snsInfo.getImageUrl())){
                    FileInfo fileInfo =fileService.fileUrlUpload(account.getMemberKey(),snsInfo.getSnsId(),SequenceCode.TB_QUEST,quest.getQuestKey());
                    if(fileInfo!= null && fileInfo.getFileKey()!=null){
                        quest.setFileKey(fileInfo.getFileKey());
                    }
                }
                quest.setSnsTitle(snsInfo.getSnsTitle());
                quest.setSnsDesc(snsInfo.getSnsDesc());
            }else{
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage("This is an invalid url.");
                return responseDataDTO;
            }
        }else if(file != null){
            FileInfo fileInfo =fileService.fileUpload(account.getMemberKey(),file, SequenceCode.TB_QUEST,quest.getQuestKey());
            if(fileInfo!= null && fileInfo.getFileKey()!=null){
                quest.setFileKey(fileInfo.getFileKey());
            }
        }
        quest.setCompleted(false);
        quest.setQuestStatus(QuestCode.QUEST_STATUS_ONGOING);
        questDao.saveQuest(quest);
        if(quest.getAnswers() != null){
            QuestAnswer answer = new QuestAnswer();
            answer.setQuestKey(quest.getQuestKey());
            for (String title:quest.getAnswers()
                 ) {
                answer.setQuestAnswerKey(sequenceService.getSequence(SequenceCode.TB_QUEST_ANSWER));
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

}
