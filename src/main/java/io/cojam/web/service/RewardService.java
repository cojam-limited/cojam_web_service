package io.cojam.web.service;

import io.cojam.web.dao.RewardDao;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.domain.RewardInfo;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RewardService {

    @Autowired
    RewardDao rewardDao;

    public RewardInfo getRewardInfo(String rewardType){
        return rewardDao.getRewardInfo(rewardType);
    }

    public List<RewardInfo> getRewardInfoList(){
        return rewardDao.getRewardInfoList();
    }

    @Transactional
    public ResponseDataDTO updateRewardInfo(RewardInfo rewardInfo){
        ResponseDataDTO response = new ResponseDataDTO();
        rewardDao.updateRewardInfo(rewardInfo);

        if(rewardInfo.getRewardType().equals("REC_M") || rewardInfo.getRewardType().equals("REC_R")){
            rewardDao.updateRewardInfoRecUseYn(rewardInfo);
        }

        response.setCheck(true);
        response.setMessage("success");

        return response;
    }
}
