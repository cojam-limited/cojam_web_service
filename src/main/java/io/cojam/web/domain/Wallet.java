package io.cojam.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Wallet {

    private String memberKey;
    private String walletId;
    private String walletName;
    private String walletAddress;
    private String blockChain;
    private String transactionId;
    private String walletStatus;

    @Builder
    public Wallet(String memberKey, String walletId, String walletName, String walletAddress, String blockChain, String transactionId, String walletStatus) {
        this.memberKey = memberKey;
        this.walletId = walletId;
        this.walletName = walletName;
        this.walletAddress = walletAddress;
        this.blockChain = blockChain;
        this.transactionId = transactionId;
        this.walletStatus = walletStatus;
    }
}
