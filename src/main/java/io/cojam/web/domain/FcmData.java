package io.cojam.web.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FcmData {
    private String title;
    private String message;
    private String pushType;
}
