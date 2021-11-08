package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInfo {

    //Sequence
    private String seq;
    //유저 키
    private String memberKey;
    //FCM token
    private String token;
    //Device 종류(I-IOS , A-Android)
    private String deviceType;
    //생성일자
    private String createDateTime;
    //수정일자
    private String updateDateTime;

}
