package io.cojam.web.klaytn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KlayTransactionDTO {
	private String id;
	private String blockchain;
	private String hash;
	private String error;
    private String status;
    private String keyId;
    private String fee;
    private Boolean isFeeDelegated;
    private String createdAt;
    private String transactionId;
    private String from;
    private String to;
    private String amount;
    private String transferType;
    private String coinSymbol;
}