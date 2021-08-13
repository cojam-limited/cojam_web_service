package io.cojam.web.domain;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class BoardCategory {
    private String boardCategoryKey;
    private String categoryName;
    private int orderNumber;
    private String resultYn;
}
