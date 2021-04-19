package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class MemberLock {

    @NotEmpty @NotNull
    List<String> memberKeys;
}
