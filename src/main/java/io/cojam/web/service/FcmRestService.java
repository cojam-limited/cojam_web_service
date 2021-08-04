package io.cojam.web.service;

import io.cojam.web.domain.FcmData;
import io.cojam.web.domain.FcmNotification;
import io.cojam.web.domain.FcmObject;
import io.cojam.web.domain.Wallet;
import io.cojam.web.klaytn.dto.CreateUserWalletRequest;
import io.cojam.web.klaytn.dto.WalletDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FcmRestService {

    @Qualifier("fcmClient")
    @Autowired
    private RestTemplate restTemplate;



    public String sendFcmService(List<String> tokenList, FcmNotification fcmNotification, FcmData fcmData) {
        String result = restTemplate.postForEntity(
                "",
                FcmObject.builder()
                        .registration_ids(tokenList)
                        .notification(fcmNotification)
                        .data(fcmData)
                .build(),
                String.class).getBody();
        return result;
    }
}
