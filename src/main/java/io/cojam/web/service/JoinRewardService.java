package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.JoinRewardHistoryDao;
import io.cojam.web.domain.*;
import io.cojam.web.domain.wallet.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.utils.Convert;

import java.math.BigInteger;

@Service
public class JoinRewardService {

    @Autowired
    JoinRewardHistoryDao joinRewardHistoryDao;

    @Autowired
    WalletService walletService;

    public Integer getJoinRewardInfo(String memberKey){
        return joinRewardHistoryDao.getJoinRewardInfo(memberKey);
    }


    public int saveJoinRewardHistory(JoinRewardHistory joinRewardHistory){
        return joinRewardHistoryDao.saveJoinRewardHistory(joinRewardHistory);
    }


    @Transactional
    public ResponseDataDTO joinRewardMember(String memberKey){
        ResponseDataDTO response = new ResponseDataDTO();
        Integer checkCount = this.getJoinRewardInfo(memberKey);
        if(checkCount > 0){
            response.setCheck(false);
            response.setMessage("You already have referral information.");
            return response;
        }else{


            //토큰 전송
            Wallet wallet = walletService.getWalletInfo(memberKey);


            if(wallet!= null){

                Transaction transaction =walletService.sendJoinRewardToken(wallet);
                if(transaction != null && !StringUtils.isBlank(transaction.getTransactionId())){
                    JoinRewardHistory joinRewardHistory = new JoinRewardHistory();
                    joinRewardHistory.setMemberKey(memberKey);
                    joinRewardHistory.setTransactionId(transaction.getTransactionId());
                    joinRewardHistoryDao.saveJoinRewardHistory(joinRewardHistory);
                }
            }
            response.setCheck(true);
            response.setMessage("success");
        }

        return response;

    }
}
