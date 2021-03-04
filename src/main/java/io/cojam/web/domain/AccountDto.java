package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDto{
    private String memberKey;
    private String memberId;
    private String memberAddress;
    private String memberName;
    private String memberPhoneNumber;
    private String memberPassword;
    private String memberRole;
}
