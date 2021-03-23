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
    @NotNull @NotEmpty
    private String startDateTime;
    @NotNull @NotEmpty
    private String endDateTime;
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
