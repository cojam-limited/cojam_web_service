package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class EventSendHistory {
    private String seq;
    private String memberKey;
    private String memberId;
    private String address;
    private String type;
    private String amount;
    private String desc;
    private String sendYn;
    private String transactionId;

}
