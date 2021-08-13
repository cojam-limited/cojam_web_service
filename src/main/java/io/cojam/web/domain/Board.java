package io.cojam.web.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class Board extends Common{
    private String boardKey;
    private String boardCategoryKey;
    private String boardTitle;
    private String boardContext;
    private String boardFile;
    @JsonIgnore
    private String createMemberKey;
    private Timestamp createDateTime;
    @JsonIgnore
    private String updateMemberKey;
    @JsonIgnore
    private Timestamp updateDateTime;
    private String categoryName;
    private String memberName;
    private String searchWord;
    private String resultYn;
}
