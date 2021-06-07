package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberOtp {
    private String memberKey;
    private String secretKey;
    private String useYn;

}
