package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
public class SeasonInput {
    private String seasonKey;

    private String title;

    private String description;

    private Long charityFee;
    private Long cojamFee;
    private Long creatorFee;
    private Long creatorPay;
    private Long minimumPay;
    private Long maximumPay;
    private Long transferPay;

    @NotEmpty
    @NotNull
    @DateTimeFormat
    private String alphaDttm;
    @NotEmpty
    @NotNull
    @DateTimeFormat
    private String omegaDttm;

    private Boolean isActive;




}
