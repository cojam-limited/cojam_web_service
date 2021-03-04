package io.cojam.web.domain;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Season {
    private String seasonKey;
    private Timestamp alphaDttm;
    private String charityFee;
    private String cojamFee;
    private Timestamp createdDttm;
    private String creatorFee;
    private String creatorPay;
    private String description;
    private Boolean isActive;
    private String minimumPay;
    private Timestamp omegaDttm;
    private String policy;
    private String title;
    private String maximumPay;
    private String transferPay;
    private String dDay;

}
