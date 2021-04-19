package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MyVoting extends Common{
    private String bettingCoin;
    private String bettingKey;
    private String bettingStatus;
    private String spenderAddress;
    private String transactionId;
    private String questKey;
    private String questAnswerKey;
    private String memberKey;
    private Timestamp createdDateTime;
    private String answerTitle;
    private String answerTotalAmount;
    private String questStatus;
    private String questTitle;
    private String seasonCategoryKey;
    private String questTotalAmount;
    private String categoryName;
    private String magnification;
    private String magnificationCoin;


}
