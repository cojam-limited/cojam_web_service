package io.cojam.web.domain;


import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;

@Getter
@Setter
public class Market {

    private String marketKey;
    private String seasonCategoryKey;
    private Timestamp endDateTime;
    private String marketTitle;
    private Timestamp adjournDateTime;
    private String adjournTx;
    private Timestamp approveDateTime;
    private String approveTx;
    private String fileKey;
    private String snsUrl;
    private String snsId;
    private Timestamp createDateTime;
    private Boolean completed;
    private String creatorAddress;
    private String marketDesc;
    private Boolean hot;
    private Boolean pending;
    private String marketStatus;
    private Timestamp successDateTime;
    private String successTx;
    private String createMemberKey;
    private String seasonKey;
    private Timestamp finishDateTime;
    private String finishTx;
    private Timestamp retrieveDateTime;
    private String retrieveTx;
    private String answerTx;
    private Timestamp draftDateTime;
    private String draftTx;

}
