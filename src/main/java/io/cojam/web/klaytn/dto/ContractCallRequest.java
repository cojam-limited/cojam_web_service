package io.cojam.web.klaytn.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.cojam.web.config.jackson.BigIntegerDeserializer;
import io.cojam.web.config.jackson.BigIntegerSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class ContractCallRequest {
    private String passphrase;
    private String contractAddress;
    @JsonSerialize(using = BigIntegerSerializer.class)
    @JsonDeserialize(using = BigIntegerDeserializer.class)
    private BigInteger value;
    private String data;
}
