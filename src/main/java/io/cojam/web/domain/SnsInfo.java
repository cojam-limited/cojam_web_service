package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SnsInfo {
    private String snsUrl;
    private String snsType;
    private String snsTitle;
    private String snsDesc;
    private String imageUrl;
    private String snsId;
    private boolean check;
}
