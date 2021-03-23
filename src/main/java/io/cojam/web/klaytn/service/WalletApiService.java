package io.cojam.web.klaytn.service;

import io.cojam.web.constant.WalletCode;
import io.cojam.web.domain.Wallet;
import io.cojam.web.domain.wallet.SendTokenTransferRequest;
import io.cojam.web.domain.wallet.Token;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.dto.BalanceResponseDTO;
import io.cojam.web.klaytn.dto.CreateUserWalletRequest;
import io.cojam.web.klaytn.dto.WalletDataDTO;
import io.cojam.web.klaytn.exception.HenesisWalletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Arrays;

@Service
public class WalletApiService {
    @Value("${app.sdk-enclave.passphrase}")
    private String sdkPassphrase;
    @Value("${app.sdk-enclave.masterWalletId")
    private String sdkMasterWalletId;

    @Qualifier("walletClient")
    @Autowired
    private RestTemplate restTemplate;



    public Wallet createUserWallet(String memberId) {
        return serviceTemplate(() -> {
            WalletDataDTO walletData = restTemplate.postForEntity(
                    "/user-wallets",
                    CreateUserWalletRequest.builder()
                            .name(memberId)
                            .passphrase(sdkPassphrase)
                            .build(),
                    WalletDataDTO.class).getBody();
            return Wallet.builder()
                    .walletAddress(walletData.getAddress())
                    .walletId(walletData.getId())
                    .memberKey(memberId)
                    .walletName(walletData.getName())
                    .transactionId(walletData.getTransactionId())
                    .build();
        });
    }

    public BigInteger getBalance(Wallet wallet) {
        return serviceTemplate(() -> {
            BalanceResponseDTO[] balanceResponseDTO = restTemplate.getForEntity(
                    String.format("/user-wallets/%s/balance",
                            wallet.getWalletId()),
                    BalanceResponseDTO[].class).getBody();
            BalanceResponseDTO hibBalance = Arrays.stream(balanceResponseDTO)
                    .filter( dto -> dto.getSymbol().equals(Token.TICKER)).toArray(BalanceResponseDTO[]::new)[0];
            return hibBalance.getAmount();
        });
    }


    public TransactionReceipt sendTokenTransferTransaction(Wallet wallet, String to, BigInteger amount, String ticker) {
        return serviceTemplate(() -> {
            SendTokenTransferRequest sendTokenTransferRequest = SendTokenTransferRequest.builder()
                    .masterWalletId(sdkMasterWalletId)
                    .userWalletId(wallet.getWalletId())
                    .passphrase(sdkPassphrase)
                    .to(to)
                    .amount(amount)
                    .ticker(ticker)
                    .build();
            return restTemplate.postForEntity(
                    String.format("/user-wallets/%s/transfer", wallet.getWalletId()),
                    sendTokenTransferRequest,
                    TransactionReceipt.class
            ).getBody();
        });
    }


    private <T> T serviceTemplate(ServiceCallback<T> callback) {
        try {
            return callback.sendRequest();
        } catch (Exception e) {
            throw new HenesisWalletException("internal wallet service failed: " + e.getMessage(), e);
        }
    }

    private interface ServiceCallback<T> {
        T sendRequest() throws Exception;
    }

}
