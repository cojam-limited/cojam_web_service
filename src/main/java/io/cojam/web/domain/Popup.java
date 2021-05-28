package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
public class Popup extends Common{

    private String popupKey;
    @NotNull @NotEmpty
    private String popupTitle;
    private Boolean actived;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    @NotNull @NotEmpty
    private String startUtcDateTime;
    @NotNull @NotEmpty
    private String endUtcDateTime;
    private String positionX;
    private String positionY;
    private String sizeX;
    private String sizeY;
    private String popupLink;
    private String popupFileKey;
    private String createMemberKey;
    private Timestamp createDateTime;
    private String updateMemberKey;
}
