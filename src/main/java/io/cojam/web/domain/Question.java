package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Question {
    private String questionKey;
    private String question;
    private String answer;
    private String opYn;
    private String orderNumber;
}
