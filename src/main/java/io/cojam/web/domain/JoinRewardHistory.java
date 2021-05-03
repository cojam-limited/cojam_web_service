package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRewardHistory {
    private String rewardHistoryKey;
    private String memberKey;
    private String transactionId;
}
