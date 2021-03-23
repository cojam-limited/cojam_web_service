package io.cojam.web.klaytn.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserWalletRequest {
    private String name;
    private String passphrase;
}
