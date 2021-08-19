package io.cojam.web.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FcmObject {
    private List<String> registration_ids;
    private FcmNotification notification;
    private FcmData data;
}
