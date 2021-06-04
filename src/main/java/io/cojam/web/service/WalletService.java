package io.cojam.web.service;

import com.google.common.base.Ticker;
import com.klaytn.caver.utils.Convert;
import io.cojam.web.constant.WalletCode;
import io.cojam.web.dao.WalletDao;
import io.cojam.web.domain.*;
import io.cojam.web.domain.wallet.*;
import io.cojam.web.klaytn.service.EventApiService;
import io.cojam.web.klaytn.service.RecommendApiService;
import io.cojam.web.klaytn.service.WalletApiService;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    RecommendApiService recommendApiService;

    @Autowired
    EventApiService eventApiService;

    @Autowired
    MyConfig myConfig;

    @Autowired
    MemberService memberService;

    @Autowired
    SeasonService seasonService;

    public int saveWallet(String memberKey,String memberId){
        Wallet wallet =walletApiService.createUserWallet(memberKey,memberId);
        if(wallet != null && wallet.getWalletAddress()!= null){
            return walletDao.saveWallet(wallet);
        }
        return 0;
    }


    public Wallet getWalletInfo(Wallet wallet){
        return walletApiService.getWalletInfo(wallet);
    }

    public String getWalletBalance(String memberKey){
        String amount="0";
        Wallet wallet = walletDao.getWalletInfo(memberKey);
        if(wallet!=null){
            BigInteger balance = walletApiService.getBalance(wallet);
            amount =balance.toString();
            amount = Convert.fromPeb(amount, Convert.Unit.KLAY).toString();

        }

        return amount;
    }

    @Transactional
    public ResponseDataDTO sendToken(String memberKey, TokenSendRequest request) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        try {
            Season season = seasonService.getSeasonInfo();
            if(season ==null || StringUtils.isBlank(season.getTransferPay())){
                responseDataDTO.setMessage("Season is not active.");
                responseDataDTO.setCheck(false);
                return responseDataDTO;
            }



            Wallet wallet = this.getWalletInfo(memberKey);
            BigInteger balance = walletApiService.getBalance(wallet);

            if(wallet.getWalletAddress().equals(request.getTo())){
                responseDataDTO.setMessage("You cannot send it to your own wallet.");
                responseDataDTO.setCheck(false);
                return responseDataDTO;
            }

            BigInteger minimumAmount = Convert.toPeb(season.getTransferPay(),Convert.Unit.KLAY).toBigInteger();

            if(request.getAmount().compareTo(minimumAmount) == -1){
                responseDataDTO.setMessage("The minimum transfer amount is "+season.getTransferPay());
                responseDataDTO.setCheck(false);
                return responseDataDTO;
            }

            if(balance.compareTo(request.getAmount()) == -1){
                responseDataDTO.setMessage("Please check your balance.");
                responseDataDTO.setCheck(false);
                return responseDataDTO;
            }

            Member param = new Member();
            param.setMemberKey(memberKey);
            Member member = memberService.getMemberInfoForMemberKey(param);
            if(member.getWalletLock()){
                responseDataDTO.setCheck(false);
                responseDataDTO.setMessage("Your Wallet is Lock!");
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
            responseDataDTO.setItem(receipt.getTransactionId());
            responseDataDTO.setCheck(true);
        }catch (Exception e){
            e.printStackTrace();
            responseDataDTO.setMessage("token send fail!.");
            responseDataDTO.setCheck(false);
        }
        return responseDataDTO;
    }

    public Wallet getWalletInfo(String memberKey){
        return walletDao.getWalletInfo(memberKey);
    }


    @Transactional
    public Transaction sendRecommendToken(Wallet wallet,BigInteger amount,String transactionType) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Transaction transaction = null;
        try {
            TransactionReceipt receipt = recommendApiService.sendMasterWalletTokenTransferTransaction(wallet.getWalletAddress(),amount,Token.TICKER)
                    .assertThenReturn("sendToken", wallet.getMemberKey());
            if(receipt ==null || StringUtils.isBlank(receipt.getTransactionId())){
                return null;
            }else {
                //SAVE TRANSACTION
                transaction = new Transaction();
                transaction.setAmount(amount.toString());
                transaction.setRecipientAddress(wallet.getWalletAddress());
                transaction.setSpenderAddress(myConfig.getRecommendAddress());
                transaction.setTransactionId(receipt.getTransactionId());
                transaction.setTransactionType(transactionType);
                transactionService.saveTransaction(transaction);
                return transaction;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Transaction sendJoinRewardToken(Wallet wallet) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Transaction transaction = null;
        BigInteger amount = org.web3j.utils.Convert.toWei(WalletCode.JOIN_REWARD_AMOUNT, org.web3j.utils.Convert.Unit.ETHER).toBigInteger();
        try {
            TransactionReceipt receipt = recommendApiService.sendMasterWalletTokenTransferTransaction(wallet.getWalletAddress(),amount,Token.TICKER)
                    .assertThenReturn("sendToken", wallet.getMemberKey());
            if(receipt ==null || StringUtils.isBlank(receipt.getTransactionId())){
                return null;
            }else {
                //SAVE TRANSACTION
                transaction = new Transaction();
                transaction.setAmount(amount.toString());
                transaction.setRecipientAddress(wallet.getWalletAddress());
                transaction.setSpenderAddress(myConfig.getRecommendAddress());
                transaction.setTransactionId(receipt.getTransactionId());
                transaction.setTransactionType(WalletCode.TRANSACTION_TYPE_JOIN_REWARD);
                transactionService.saveTransaction(transaction);
                return transaction;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Transactional
    public Transaction sendLoginRewardToken(Wallet wallet) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Transaction transaction = null;
        BigInteger amount = org.web3j.utils.Convert.toWei(WalletCode.LOGIN_REWARD_AMOUNT, org.web3j.utils.Convert.Unit.ETHER).toBigInteger();
        try {
            TransactionReceipt receipt = recommendApiService.sendMasterWalletTokenTransferTransaction(wallet.getWalletAddress(),amount,Token.TICKER)
                    .assertThenReturn("sendToken", wallet.getMemberKey());
            if(receipt ==null || StringUtils.isBlank(receipt.getTransactionId())){
                return null;
            }else {
                //SAVE TRANSACTION
                transaction = new Transaction();
                transaction.setAmount(amount.toString());
                transaction.setRecipientAddress(wallet.getWalletAddress());
                transaction.setSpenderAddress(myConfig.getRecommendAddress());
                transaction.setTransactionId(receipt.getTransactionId());
                transaction.setTransactionType(WalletCode.TRANSACTION_TYPE_LOGIN_REWARD);
                transactionService.saveTransaction(transaction);
                return transaction;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    @Transactional
    public Transaction sendEventRewardToken(Wallet wallet,BigInteger amount,String transactionType) {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        Transaction transaction = null;
        try {
            TransactionReceipt receipt = eventApiService.sendMasterWalletTokenTransferTransaction(wallet.getWalletAddress(),amount,Token.TICKER)
                    .assertThenReturn("sendToken", wallet.getMemberKey());
            if(receipt ==null || StringUtils.isBlank(receipt.getTransactionId())){
                return null;
            }else {
                //SAVE TRANSACTION
                transaction = new Transaction();
                transaction.setAmount(amount.toString());
                transaction.setRecipientAddress(wallet.getWalletAddress());
                transaction.setSpenderAddress(myConfig.getRecommendAddress());
                transaction.setTransactionId(receipt.getTransactionId());
                transaction.setTransactionType(transactionType);
                transactionService.saveTransaction(transaction);
                return transaction;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public int updateWalletInfo(Wallet wallet){
        return walletDao.updateWalletInfo(wallet);
    }
}
