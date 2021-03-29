package io.cojam.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    @JsonIgnore
    private int startIndex = 0;
    @JsonIgnore
    private int pageSize = 10;
    private String memberName;
    @JsonIgnore
    private String searchText;
    @JsonIgnore
    private String orderType;
}
