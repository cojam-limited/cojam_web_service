package io.cojam.web.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    //userId
    private @NonNull String userId;
    //password
    private @NonNull String userPass;
    //userIdText
    private @NonNull String userIdText;
}
