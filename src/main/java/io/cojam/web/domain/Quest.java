package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class Quest extends Common{

    private String questKey;
    private Timestamp adjournDateTime;
    private String adjournTx;
    private Timestamp approveDateTime;
    private String approveTx;
    @NotEmpty @NotNull
    private String seasonCategoryKey;
    private Timestamp startDateTime;
    @NotEmpty @NotNull
    private Timestamp endDateTime;
    @NotEmpty @NotNull
    private String questTitle;
    private Boolean completed;
    private String creatorAddress;
    private String questDesc;
    private Boolean hot;
    private Boolean pending;
    private String questStatus;
    private Timestamp successDateTime;
    private String successTx;
    private String memberKey;
    private String seasonKey;
    private Timestamp finishDateTime;
    private String finishTx;
    private Timestamp retrieveDateTime;
    private String retrieveTx;
    private String answersTx;
    private Timestamp draftDateTime;
    private String draftTx;
    private String snsUrl;
    private String snsId;
    private String fileKey;
    private List<String> answers;
    private String createMemberKey;
    private Timestamp createDateTime;
    private String updateMemberKey;
    private Timestamp updateDateTime;
    private String snsType;
    private String snsDesc;
    private String snsTitle;
    private String categoryName;
    private String orderType;
    private String creatorFee;
    private String totalCreatorFee;
    private String cojamFee;
    private String creatorPay;
    private String charityFee;
    private String totalCharityFee;
    private Long maximumPay;
    private Long minimumPay;
    private String statusType;
    private String title;
    private String description;
    private Boolean isActive;
    private String totalAmount;
    private String answersStr;
    private String dDay;
    private Boolean finished;

}
