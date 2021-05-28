package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestAnswer {
    private String questAnswerKey;
    private String questKey;
    private String questExample;
    private String answerTitle;
    private Boolean selected;
    private String totalAmount;
    private String userCnt;
    private String rowNum;
    private Boolean myAnswer;
}
