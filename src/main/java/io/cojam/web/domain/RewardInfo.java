package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RewardInfo {
    @NotNull @NotEmpty
    private String rewardType;
    @NotNull @NotEmpty
    private String rewardAmount;
    @NotNull @NotEmpty
    private String useYn;
}
