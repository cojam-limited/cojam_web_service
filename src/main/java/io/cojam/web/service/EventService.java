package io.cojam.web.service;

import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.EventDao;
import io.cojam.web.domain.EventSendHistory;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.domain.Wallet;
import io.cojam.web.domain.wallet.Transaction;
import io.cojam.web.klaytn.service.EventApiService;
import io.cojam.web.klaytn.service.WalletApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.utils.Convert;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventDao eventDao;

    @Autowired
    WalletApiService walletApiService;

    @Autowired
    WalletService walletService;

    @Transactional
    public ResponseDataDTO sendReward(){
        ResponseDataDTO response = new ResponseDataDTO();


        if(eventDao.checkSendEvent("EVENT") > 0){
            response.setCheck(false);
            response.setMessage("fail");
            return response;
        }
        eventDao.updateSendYn("EVENT");
        List<EventSendHistory> list = eventDao.getEventRewardList();

        for (EventSendHistory param : list ) {
            Wallet wallet = walletService.getWalletInfo(param.getMemberKey());
            if(wallet != null){
                BigInteger amount = Convert.toWei(param.getAmount(), Convert.Unit.ETHER).toBigInteger();
                Transaction transaction =walletService.sendEventRewardToken(wallet,amount,WalletCode.TRANSACTION_TYPE_EVNET_M);
                if(transaction != null && !StringUtils.isBlank(transaction.getTransactionId())){
                    param.setTransactionId(transaction.getTransactionId());
                    param.setSendYn("Y");
                    eventDao.updateEventRewardHistory(param);
                }
            }
        }

        eventDao.updateSendYn("EVENT");
        response.setCheck(true);
        response.setMessage("SUCCESS");
        return response;
    }
}
