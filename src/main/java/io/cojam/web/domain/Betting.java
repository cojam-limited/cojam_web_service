package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
public class Betting {

    private String bettingKey;
    @NotEmpty @NotNull
    private String bettingCoin;

    private String spenderAddress;
    private String transactionId;
    private String bettingStatus;
    @NotEmpty @NotNull
    private String questKey;
    @NotEmpty @NotNull
    private String questAnswerKey;
    private String memberKey;
    private String receiveAddress;
    private String answerTitle;
    private int rowNum;
    private Timestamp createdDateTime;
    private String bettingAvg;
}
