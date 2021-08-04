package io.cojam.web.service;

import io.cojam.web.dao.PushMessageDao;
import io.cojam.web.domain.FcmData;
import io.cojam.web.domain.FcmNotification;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.domain.Quest;
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
    MyConfig myConfig;

    public List<String> getBettingTokenList(String key){
        return pushMessageDao.getBettingTokenList(key);
    }

    public void sendPushMessage(String pushType,String key){
        if(pushType.equals("QT_S")){
            Quest detail = questService.getQuestDetail(key);
            if(detail != null){
                List<String> tokenList = this.getBettingTokenList(key);
                if(tokenList!=null){
                    FcmData fcmData = FcmData.builder()
                            .title("Success Market")
                            .message(detail.getQuestTitle())
                            .pushType(pushType)
                            .build();
                    String image = "";
                    image += String.format("%s/user/media/image?id=%s",myConfig.getHostUrl(),detail.getFileKey());
                    this.sendFcm(tokenList,fcmData,image);
                }
            }
        }else if(pushType.equals("QT_A")){
            Quest detail = questService.getQuestDetail(key);
            if(detail != null){
                List<String> tokenList = this.getBettingTokenList(key);
                if(tokenList!=null){
                    FcmData fcmData = FcmData.builder()
                            .title("Adjourn Market")
                            .message(detail.getQuestTitle())
                            .pushType(pushType)
                            .build();
                    String image = "";
                    image += String.format("%s/user/media/image?id=%s",myConfig.getHostUrl(),detail.getFileKey());
                    this.sendFcm(tokenList,fcmData,image);
                }
            }
        }else if(pushType.equals("NOTICE")){

        }
    }

    public String sendFcm(List<String> tokenList , FcmData fcmData, String image){
        String result="";
        try {
            result = fcmRestService.sendFcmService(
                    tokenList,
                    FcmNotification.builder()
                            .body(fcmData.getMessage())
                            .title(fcmData.getTitle())
                            .image(StringUtils.isBlank(image)?null:image)
                            .build(),
                    fcmData
            );
        }catch (Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }
}
