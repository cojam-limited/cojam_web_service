package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email extends Common{

    private String emailName;
    private String useYn;
    private String searchWord;
}
