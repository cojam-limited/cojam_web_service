package io.cojam.web.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {

    private int code=200;

    private String message;

    private Boolean check=true;

    private T response;

    private String messageLocaleCode;
}
