package io.cojam.web.service;


import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.TransactionDao;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.domain.wallet.Transaction;
import io.cojam.web.klaytn.dto.TransactionStatus;
import io.cojam.web.klaytn.service.TransactionApiService;
import org.apache.commons.lang3.StringUtils;
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

    public List<Transaction> getTransactionStatusCheckList(Transaction transaction){
        return transactionDao.getTransactionStatusCheckList(transaction);
    }

    public int updateTransactionStatusCheck(Transaction transaction){
        return transactionDao.updateTransactionStatusCheck(transaction);
    }


    @Transactional
    public ResponseDataDTO transactionIdStatusCheckProc() {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        try {
            List<Transaction> list = transactionDao.getTransactionStatusCheckList(null);
            if(list !=null){
                System.out.println(String.format("Total size : %s",list.size()));
                int i=1;
                for (Transaction item:list) {

                    String transactionId = item.getTransactionId();
                    System.out.println(String.format("%s. Transaction ID : %s",i,transactionId));
                    if(!StringUtils.isBlank(transactionId)){
                        System.out.println("통신 시작");
                        TransactionStatus transactionStatus = transactionApiService.getTransactionStatusById(transactionId);
                        System.out.println("통신 성공");
                        if(transactionStatus !=null || !WalletCode.TRANSACTION_STATUS_REQUESTED.equals(transactionStatus.getStatus())){
                            item.setStatus(transactionStatus.getStatus());
                            transactionDao.updateTransactionStatusCheck(item);
                            System.out.println(String.format("%s. UPDATED STATUS==> %s",i,transactionStatus.getStatus()));
                        }else{
                            System.out.println(String.format("%s. NOT UPDATED",i));
                        }
                    }
                    i++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("wallet service failed.");
        }
        return responseDataDTO;
    }



}
