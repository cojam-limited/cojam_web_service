package io.cojam.web.account;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Data
@Getter
@Setter
@ToString
public class Account implements Serializable {

    private String memberKey;
    private String memberId;
    private String memberAddress;
    private String memberName;
    private String memberPhoneNumber;
    private String memberPassword;
    private String memberRole;
    private Boolean certification;
}
