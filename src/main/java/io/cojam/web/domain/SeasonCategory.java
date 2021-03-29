package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonCategory {
    private String categoryKey;
    private String categoryName;
    private String orderNumber;
    private int cnt;
    private String limitation;
}
