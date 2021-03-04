package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;

@Getter
@Setter
public class MarketAnswer {


    private String marketAnswerKey;
    private String marketKey;
    private String marketExample;
    private String answerTitle;
    private Boolean selected;
    private Timestamp createDateTime;
    private Timestamp deleteDateTime;

}
