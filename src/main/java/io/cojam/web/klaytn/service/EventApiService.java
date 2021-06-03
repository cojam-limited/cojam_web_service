package io.cojam.web.klaytn.service;

import io.cojam.web.domain.wallet.SendTokenTransferRequest;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.exception.HenesisWalletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;

@Service
public class EventApiService {

    @Value("${app.sdk-enclave.eventPassphrase}")
    private String eventPassphrase;

    @Qualifier("eventWalletClient")
    @Autowired
    private RestTemplate restTemplate;

    public TransactionReceipt sendMasterWalletTokenTransferTransaction(String to, BigInteger amount, String ticker) {
        return serviceTemplate(() -> {
            SendTokenTransferRequest sendTokenTransferRequest = SendTokenTransferRequest.builder()
                    .passphrase(eventPassphrase)
                    .to(to)
                    .amount(amount)
                    .ticker(ticker)
                    .build();
            return restTemplate.postForEntity(
                    "/transfer",
                    sendTokenTransferRequest,
                    TransactionReceipt.class
            ).getBody();
        });
    }

    private <T> T serviceTemplate(EventApiService.ServiceCallback<T> callback) {
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
