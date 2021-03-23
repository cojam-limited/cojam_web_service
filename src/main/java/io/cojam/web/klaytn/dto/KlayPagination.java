package io.cojam.web.klaytn.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KlayPagination<T> {
    private String nextUrl;
    private String previousUrl;
    private Long totalCount;
    private List<T> data;

    @Builder
    public KlayPagination(String nextUrl, String previousUrl, Long totalCount, List<T> results) {
        this.nextUrl = nextUrl;
        this.previousUrl = previousUrl;
        this.totalCount = totalCount;
        this.data = results;
    }
}
