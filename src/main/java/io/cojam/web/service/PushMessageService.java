package io.cojam.web.service;

import io.cojam.web.dao.PushMessageDao;
import io.cojam.web.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushMessageService {

    @Autowired
    private FcmRestService fcmRestService;

    @Autowired
    PushMessageDao pushMessageDao;

    @Autowired
    QuestService questService;

    @Autowired
    BoardService boardService;

    @Autowired
    MyConfig myConfig;

    public List<String> getBettingTokenList(String key){
        return pushMessageDao.getBettingTokenList(key);
    }

    public List<String> getNoticeTokenList(String key){
        return pushMessageDao.getNoticeTokenList(key);
    }

    public void sendPushMessage(String pushType,String key){
        if(pushType.equals("QT_S")){
            Quest detail = questService.getQuestDetail(key);
            if(detail != null){
                List<String> tokenList = this.getBettingTokenList(key);
                if(tokenList!=null){

                    String image = "";
                    image += String.format("%s/user/media/image?id=%s",myConfig.getHostUrl(),detail.getFileKey());

                    FcmData fcmData = FcmData.builder()
                            .title("Success Market")
                            .message(detail.getQuestTitle())
                            .pictureUrl(image)
                            .pushType(pushType)
                            .pushKey(key)
                            .build();
                    this.sendFcm(tokenList,fcmData,image);
                }
            }
        }else if(pushType.equals("QT_A")){
            Quest detail = questService.getQuestDetail(key);
            if(detail != null){
                List<String> tokenList = this.getBettingTokenList(key);
                if(tokenList!=null){
                    String image = "";
                    image += String.format("%s/user/media/image?id=%s",myConfig.getHostUrl(),detail.getFileKey());

                    FcmData fcmData = FcmData.builder()
                            .title("Adjourn Market")
                            .message(detail.getQuestTitle())
                            .pushType(pushType)
                            .pictureUrl(image)
                            .pushKey(key)
                            .build();
                    this.sendFcm(tokenList,fcmData,image);
                }
            }
        }else if(pushType.equals("NOTICE")){
            Board board = new Board();
            board.setBoardKey(key);
            Board detail = boardService.getNoticeBoardDetail(board);
            if(detail != null){
                List<String> tokenList = this.getNoticeTokenList(key);


                if(tokenList!=null){
                    String title="";
                    if(detail.getBoardCategoryKey().equals("BCT2021022500000002")){
                        title = "Results";
                    }else {
                        title = "Notice";
                    }

                    String image = "";
                    image += String.format("%s/user/media/image?id=%s",myConfig.getHostUrl(),detail.getBoardFile());



                    FcmData fcmData = FcmData.builder()
                            .title(title)
                            .message(detail.getBoardTitle())
                            .pushType(pushType)
                            .pictureUrl(image)
                            .pushKey(key)
                            .build();
                    this.sendFcm(tokenList,fcmData,image);
                }
            }
        }else if(pushType.equals("QT_ALL")){
            Quest detail = questService.getQuestDetail(key);
            if(detail != null){
                List<String> tokenList = this.getNoticeTokenList(key);
                if(tokenList!=null){
                    String image = "";
                    image += String.format("%s/user/media/image?id=%s",myConfig.getHostUrl(),detail.getFileKey());
                    FcmData fcmData = FcmData.builder()
                            .title("Approved Market")
                            .message(detail.getQuestTitle())
                            .pictureUrl(image)
                            .pushType(pushType)
                            .pushKey(key)
                            .build();
                    this.sendFcm(tokenList,fcmData,image);
                }
            }
        }
    }

    public String sendFcm(List<String> tokenList , FcmData fcmData, String image){
        String result="";
        try {

            int tokenListSize = tokenList.size();
            int maxListSize = 1000;
            int page = tokenListSize%maxListSize==0?tokenListSize/maxListSize:(tokenListSize/maxListSize)+1;

            if(page==0){
                page = 1;
            }else if(tokenListSize < maxListSize){
                page = 1;
            }

            for (int i=0;i<page;i++){
                int lastIndex= (i+1)*maxListSize;
                if(lastIndex > tokenListSize){
                    lastIndex = tokenListSize;
                }
                List<String> pageTokenList = tokenList.subList(i*maxListSize,lastIndex);

                result = fcmRestService.sendFcmService(
                        pageTokenList,
                        FcmNotification.builder()
                                .body(fcmData.getMessage())
                                .title(fcmData.getTitle())
                                .icon("notification_icon")
                                .image(StringUtils.isBlank(image)?null:image)
                                .build(),
                        fcmData
                );
            }
        }catch (Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }
}
