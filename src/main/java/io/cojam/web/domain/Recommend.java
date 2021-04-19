package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Recommend {
    private String memberKey;
    private String recommendKey;
    private String mTransactionId;
    private String rTransactionId;
    private Timestamp createDateTime;
    private Timestamp updateDateTime;
}
