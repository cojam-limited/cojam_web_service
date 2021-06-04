package io.cojam.web.klaytn.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class WalletDataDTO {
    private String id;
    private String name;
    private String address;
    private String transactionId;
    private String blockchain;
    private Timestamp createdAt;
    private String status;
}
