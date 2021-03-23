package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
public class Question extends Common{
    private String questionKey;
    @NotEmpty
    @NotNull
    private String question;
    @NotEmpty
    @NotNull
    private String answer;
    @NotEmpty
    @NotNull
    private String opYn;
    @NotEmpty
    @NotNull
    private String orderNumber;
    private String createMemberKey;
    private Timestamp createDateTime;
    private String updateMemberKey;
    private Timestamp updateDateTime;

}
