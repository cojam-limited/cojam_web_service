package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInfo {
    private String seq;
    private String memberKey;
    private String token;
    private String deviceType;
    private String createDateTime;
    private String updateDateTime;

}
