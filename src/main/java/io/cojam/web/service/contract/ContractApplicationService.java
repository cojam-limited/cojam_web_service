package io.cojam.web.service.contract;


import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.KlayTransactionReceipt;
import com.klaytn.caver.utils.ChainId;
import com.klaytn.caver.utils.Convert;
import com.klaytn.caver.wallet.KlayWalletUtils;
import io.cojam.web.contract.web3.CojamMarket;
import io.cojam.web.contract.web3.CojamToken;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.domain.Wallet;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.dto.Data;
import io.cojam.web.klaytn.service.WalletApiService;
import io.cojam.web.utils.EthSigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.tuples.generated.Tuple11;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
public class ContractApplicationService {
    @Autowired
    WalletApiService walletApiService;

    CojamMarket cojamMarket;

    io.cojam.web.contract.caver.CojamMarket cojamMarketCaver;

    CojamToken cojamToken;

    io.cojam.web.contract.caver.CojamToken cojamTokenCaver;

    @Value("${app.sdk-enclave.marketAddress}")
    private String marketAddress;

    @Value("${app.sdk-enclave.tokenAddress}")
    private String tokenAddress;


    @Value("${app.sdk-enclave.feeAddress}")
    private String feeAddress;

    @Value("${app.sdk-enclave.charityAddress}")
    private String charityAddress;

    @Value("${app.sdk-enclave.remainAddress}")
    private String remainAddress;

    @Autowired
    MyConfig myConfig;


    public TransactionReceipt draftMarket(BigInteger marketKey, String creator, String title, BigInteger creatorFee, BigInteger creatorPercentage, BigInteger cojamPercentage, BigInteger charityPercentage, List<BigInteger> answerKeys) {
        // contract 호출
        try {
            Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
            this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());

            String payload = this.cojamMarket.draftMarket(marketKey, creator, title, creatorFee, creatorPercentage, cojamPercentage, charityPercentage).encodeFunctionCall();
            return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public TransactionReceipt answer(BigInteger marketKey, List<BigInteger> answerKeys) {
        // contract 호출
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.addAnswerKeys(marketKey,answerKeys).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }


    public TransactionReceipt approveMarket(BigInteger marketKey) {
        // contract 호출
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.approveMarket(marketKey).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }

    public TransactionReceipt bet(Wallet wallet,BigInteger marketKey, BigInteger answerKey, BigInteger bettingKey, BigInteger tokens) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.bet(marketKey, answerKey, bettingKey, tokens).encodeFunctionCall();

        return walletApiService.contractCall(wallet,marketAddress, BigInteger.ZERO, new Data(payload));
    }

    public String getMarketInfo(BigInteger marketKey) throws Exception {

        String dummyWalletKey = "0x6f993629f0d3836153141053f314286d555b4ac21f14057004c7e900413aa1a30x000x02c3d28f9d2618f03f8a499774ac28332471ae6a";

        this.cojamMarketCaver = io.cojam.web.contract.caver.CojamMarket.load(marketAddress,Caver.build(Caver.BAOBAB_URL),KlayWalletUtils.loadCredentials(dummyWalletKey),ChainId.BAOBAB_TESTNET,new com.klaytn.caver.tx.gas.DefaultGasProvider());
        Tuple11<BigInteger, String, String, String, List<BigInteger>, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = this.cojamMarketCaver.getMarket(marketKey).send();
        if (result == null){
            return "No result";
        }else{
            String resultString = "";
            resultString +=String.format("00.KEY : %s",result.component1()==null?"NULL":result.component1())+"\n";
            resultString +=String.format("01.TITLE : %s",result.component2()==null?"NULL":result.component2())+"\n";
            resultString +=String.format("02.CREATOR : %s",result.component3()==null?"NULL":result.component3())+"\n";
            resultString +=String.format("03.STATUS : %s",result.component4()==null?"NULL":result.component4())+"\n";
            resultString +=String.format("04.ANSWER KEYS : %s",result.component5()==null?"NULL":result.component5())+"\n";
            resultString +=String.format("05.APPROVE TIME : %s",result.component6()==null?"NULL":result.component6())+"\n";
            resultString +=String.format("06.FINISH TIME : %s",result.component7()==null?"NULL":result.component7())+"\n";
            resultString +=String.format("07.SUCCESS TIME : %s",result.component8()==null?"NULL":result.component8())+"\n";
            resultString +=String.format("08.ADJOURN TIME : %s",result.component9()==null?"NULL":result.component9())+"\n";
            resultString +=String.format("9.TOTAL TOKENS : %s",result.component10()==null?"NULL":result.component10())+"\n";
            resultString +=String.format("10.REMAIN TOKENS : %s",result.component11()==null?"NULL":result.component11())+"\n";


            return resultString;
        }
    }

    public TransactionReceipt approve(Wallet wallet,String approve, BigInteger amount) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamToken = cojamToken.load(tokenAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamToken.approve(approve, amount).encodeFunctionCall();
        return walletApiService.contractCall(wallet,tokenAddress, BigInteger.ZERO, new Data(payload));
    }

    public TransactionReceipt approveMaster(BigInteger amount) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamToken = cojamToken.load(tokenAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamToken.approve(marketAddress, amount).encodeFunctionCall();
        return walletApiService.contractCallMaster(tokenAddress, BigInteger.ZERO, new Data(payload));
    }

    public TransactionReceipt transferFrom(Wallet wallet,String fromAddress,String toAddress, BigInteger amount) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamToken = cojamToken.load(tokenAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamToken.transferFrom(fromAddress,toAddress, amount).encodeFunctionCall();
        return walletApiService.contractCall(wallet,tokenAddress, BigInteger.ZERO, new Data(payload));
    }

    public TransactionReceipt transfer(Wallet wallet,String toAddress, BigInteger amount) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamToken = cojamToken.load(tokenAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamToken.transfer(toAddress, amount).encodeFunctionCall();
        return walletApiService.contractCall(wallet,tokenAddress, BigInteger.ZERO, new Data(payload));
    }

    public Boolean availableBet(BigInteger marketKey, BigInteger answerKey, BigInteger bettingKey, BigInteger amount) throws Exception {
        String dummyWalletKey = "0x6f993629f0d3836153141053f314286d555b4ac21f14057004c7e900413aa1a30x000x02c3d28f9d2618f03f8a499774ac28332471ae6a";
        this.cojamMarketCaver = io.cojam.web.contract.caver.CojamMarket.load(marketAddress,Caver.build(Caver.BAOBAB_URL),KlayWalletUtils.loadCredentials(dummyWalletKey),ChainId.BAOBAB_TESTNET,new com.klaytn.caver.tx.gas.DefaultGasProvider());

        return this.cojamMarketCaver.availableBet(marketKey,answerKey,bettingKey,amount).send();
    }

    public String getAnswer(BigInteger answerKey) throws Exception {
        String dummyWalletKey = "0x6f993629f0d3836153141053f314286d555b4ac21f14057004c7e900413aa1a30x000x02c3d28f9d2618f03f8a499774ac28332471ae6a";
        this.cojamMarketCaver = io.cojam.web.contract.caver.CojamMarket.load(tokenAddress,Caver.build(Caver.BAOBAB_URL),KlayWalletUtils.loadCredentials(dummyWalletKey),ChainId.BAOBAB_TESTNET,new com.klaytn.caver.tx.gas.DefaultGasProvider());
        Tuple3<BigInteger, BigInteger, List<BigInteger>> result =this.cojamMarketCaver.getAnswer(answerKey).send();
        System.out.println(result.component1());
        System.out.println(result.component2());
        System.out.println(result.component3());
        return result.toString();
    }


    public TransactionReceipt balanceOf(String owner) throws Exception {

        String dummyWalletKey = "0x6f993629f0d3836153141053f314286d555b4ac21f14057004c7e900413aa1a30x000x02c3d28f9d2618f03f8a499774ac28332471ae6a";

        this.cojamTokenCaver = io.cojam.web.contract.caver.CojamToken.load(tokenAddress,Caver.build(Caver.BAOBAB_URL),KlayWalletUtils.loadCredentials(dummyWalletKey),ChainId.BAOBAB_TESTNET,new com.klaytn.caver.tx.gas.DefaultGasProvider());
        BigInteger result =this.cojamTokenCaver.balanceOf(owner).send();
        System.out.println("result=>"+result.toString());
        return null;
    }

    public void initMaster() {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = cojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        setAccount("cojamFeeAccount");
        setAccount("charityFeeAccount");
        setAccount("remainAccount");
        cojamMarket.setContractAddress(marketAddress);
    }

    public TransactionReceipt setAccount(String key){
        String address = "";
        if("cojamFeeAccount".equals(key)){
            address = feeAddress;
        }else if("charityFeeAccount".equals(key)){
            address = charityAddress;
        } else if("remainAccount".equals(key)){
            address = remainAddress;
        }
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = cojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload1 = cojamMarket.setAccount(key,address).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload1));
    }

    public TransactionReceipt finishMarket(BigInteger marketKey) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.finishMarket(marketKey).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }


    public TransactionReceipt successMarket(BigInteger marketKey,BigInteger selectedAnswerKey) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.successMarket(marketKey,selectedAnswerKey).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }

    public TransactionReceipt adjournMarket(BigInteger marketKey) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.adjournMarket(marketKey).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }

    public TransactionReceipt retrieveMarket(BigInteger marketKey) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.retrieveTokens(marketKey).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }

    public TransactionReceipt receiveToken(Wallet wallet,BigInteger marketKey,BigInteger bettingKey) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.receiveToken(marketKey,bettingKey).encodeFunctionCall();
        return walletApiService.contractCall(wallet,marketAddress,BigInteger.ZERO,new Data(payload));
    }

    public TransactionReceipt lockWallet(List<String> addressList) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.lock(addressList).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }

    public TransactionReceipt unLockWallet(List<String> addressList) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamMarket = CojamMarket.load(marketAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamMarket.unlock(addressList).encodeFunctionCall();
        return walletApiService.contractCallMaster(marketAddress,BigInteger.ZERO,new Data(payload));
    }


    public String getTotalSupply() throws Exception {
        String dummyWalletKey = "0x6f993629f0d3836153141053f314286d555b4ac21f14057004c7e900413aa1a30x000x02c3d28f9d2618f03f8a499774ac28332471ae6a";
        String caverUrl = "";
        int chinId;
        if(myConfig.getProfile().equals("prod")){
            caverUrl = "https://tn.henesis.io/klaytn/mainnet?clientId=30c53153a44235aa2d799189911edec5";
            chinId = ChainId.MAINNET;
        }else{
            caverUrl = Caver.BAOBAB_URL;
            chinId = ChainId.BAOBAB_TESTNET;
        }




        this.cojamTokenCaver = io.cojam.web.contract.caver.CojamToken.load(tokenAddress,Caver.build(caverUrl),KlayWalletUtils.loadCredentials(dummyWalletKey),chinId,new com.klaytn.caver.tx.gas.DefaultGasProvider());
        BigInteger amount =this.cojamTokenCaver.totalSupply().send();
        return Convert.fromPeb(String.valueOf(amount), Convert.Unit.KLAY).toString();
    }


    public TransactionReceipt burnAddress(BigInteger amount) {
        Web3j web3j = Web3j.build(new org.web3j.protocol.http.HttpService());
        this.cojamToken = CojamToken.load(tokenAddress, web3j, EthSigner.getDummyCredentials(), new DefaultGasProvider());
        String payload = this.cojamToken.burn(amount).encodeFunctionCall();
        return walletApiService.contractCallMasterBurn(tokenAddress,BigInteger.ZERO,new Data(payload));
    }

}
