package io.cojam.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Getter
@Setter
public class Member extends Common{

    private String memberKey;
    @NotEmpty @NotBlank
    private String memberId;
    @NotEmpty @NotBlank
    private String memberName;
    @NotEmpty @NotBlank @Email
    private String memberEmail;
    @JsonIgnore
    private String delYn;
    @NotEmpty @NotBlank @JsonIgnore
    private String memberPass;
    @NotEmpty @NotBlank @JsonIgnore
    private String memberPassConfirm;
    @JsonIgnore
    private String fpNumber;
    private String memberRole;
    private String walletAddress;

    private Boolean walletLock;
    private String lockTransactionId;
    private Boolean certification;
    private Timestamp createdDateTime;
    private Boolean access;




}
