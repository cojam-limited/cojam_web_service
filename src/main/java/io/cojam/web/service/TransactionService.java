package io.cojam.web.service;


import io.cojam.web.dao.TransactionDao;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.domain.wallet.Transaction;
import io.cojam.web.klaytn.dto.TransactionStatus;
import io.cojam.web.klaytn.service.TransactionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    TransactionDao transactionDao;

    @Autowired
    TransactionApiService transactionApiService;

    public int saveTransaction(Transaction transaction){
        return transactionDao.saveTransaction(transaction);
    }

    public List<Transaction> getTransactionList(Transaction transaction){
        return transactionDao.getTransactionList(transaction);
    }

    public Integer getTransactionListCnt(Transaction transaction){
        return transactionDao.getTransactionListCnt(transaction);
    }

    public Transaction getTransaction(String transactionKey){
        return transactionDao.getTransaction(transactionKey);
    }

    @Transactional
    public ResponseDataDTO getTransactionStatus(Transaction transaction) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        try {
            TransactionStatus transactionStatus= transactionApiService.getTransactionStatusById(transaction.getTransactionId());
            if(transactionStatus == null || transactionStatus.getStatus() == null){
                responseDataDTO.setCheck(true);
                responseDataDTO.setMessage("success");
                responseDataDTO.setItem(transaction.getStatus());
            }else{
                if(!transaction.getStatus().equals(transactionStatus.getStatus())){
                    transaction.setStatus(transactionStatus.getStatus());
                    transactionDao.updateTransactionStatus(transaction);
                }
                responseDataDTO.setCheck(true);
                responseDataDTO.setMessage("success");
                responseDataDTO.setItem(transactionStatus.getStatus());
            }
        }catch (Exception e){
            e.printStackTrace();
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("wallet service failed.");
        }
        return responseDataDTO;
    }
}
