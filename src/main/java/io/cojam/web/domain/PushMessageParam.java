package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushMessageParam {
    private String key;
    private String pushType;
}
