package io.cojam.web.service;

import com.klaytn.caver.utils.Convert;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.WalletDao;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.domain.Wallet;
import io.cojam.web.domain.wallet.*;
import io.cojam.web.klaytn.service.WalletApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class WalletService {

    @Autowired
    WalletDao walletDao;

    @Autowired
    WalletApiService walletApiService;

    @Autowired
    TransactionService transactionService;

    public int saveWallet(String memberKey){
        Wallet wallet =walletApiService.createUserWallet(memberKey);
        if(wallet != null && wallet.getWalletAddress()!= null){
            return walletDao.saveWallet(wallet);
        }
        return 0;
    }

    public String getWalletBalance(String memberKey){
        String amount="0";
        Wallet wallet = walletDao.getWalletInfo(memberKey);
        if(wallet!=null){
            BigInteger balance = walletApiService.getBalance(wallet);
            amount =balance.toString();
            amount = Convert.fromPeb(amount, Convert.Unit.KLAY).toBigInteger().toString();

        }

        return amount;
    }

    @Transactional
    public ResponseDataDTO sendToken(String memberKey, TokenSendRequest request) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        try {
            Wallet wallet = this.getWalletInfo(memberKey);
            BigInteger balance = walletApiService.getBalance(wallet);

            if(wallet.getWalletAddress().equals(request.getTo())){
                responseDataDTO.setMessage("TYou cannot send it to your own wallet.");
                responseDataDTO.setCheck(false);
                return responseDataDTO;
            }

            if(balance.compareTo(request.getAmount()) == -1){
                responseDataDTO.setMessage("There is not enough balance.");
                responseDataDTO.setCheck(false);
                return responseDataDTO;
            }

            TransactionReceipt receipt = walletApiService
                    .sendTokenTransferTransaction(wallet, request.getTo(), request.getAmount(), Token.TICKER)
                    .assertThenReturn("sendToken", memberKey, request);
            //SAVE TRANSACTION
            Transaction transaction = new Transaction();
            transaction.setAmount(request.getAmount().toString());
            transaction.setRecipientAddress(request.getTo());
            transaction.setSpenderAddress(wallet.getWalletAddress());
            transaction.setTransactionId(receipt.getTransactionId());
            transaction.setTransactionType(WalletCode.TRANSACTION_TYPE_TRANSFER);
            transactionService.saveTransaction(transaction);
            responseDataDTO.setMessage("success");
            responseDataDTO.setItem(new TransferDTO(receipt.getTransactionId()));
            responseDataDTO.setCheck(true);
        }catch (Exception e){
            e.printStackTrace();
            responseDataDTO.setMessage("token send fail!.");
            responseDataDTO.setCheck(false);
        }
        return responseDataDTO;
    }

    @Cacheable(value = "memberWalletInfo",key = "#memberKey",cacheManager = "userCacheManager")
    public Wallet getWalletInfo(String memberKey){
        return walletDao.getWalletInfo(memberKey);
    }

}
