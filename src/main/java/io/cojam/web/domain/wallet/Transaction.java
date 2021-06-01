package io.cojam.web.domain.wallet;

import io.cojam.web.domain.Common;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Transaction extends Common {
    private String transactionKey;
    private String amount;
    //To 받는 사람
    private String recipientAddress;
    //From 보낸 사람
    private String spenderAddress;
    private String transactionId;
    private String transactionType;
    private String memberKey;
    private String address;
    private String originAddress;
    private String status;
    private String memberId;
    private Timestamp createdDateTime;
    private String walletName;
}
