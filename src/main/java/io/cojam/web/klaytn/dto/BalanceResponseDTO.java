package io.cojam.web.klaytn.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.cojam.web.config.jackson.BigIntegerDeserializer;
import io.cojam.web.config.jackson.BigIntegerSerializer;
import lombok.Data;
import lombok.ToString;

import java.math.BigInteger;

@Data
@ToString
public class BalanceResponseDTO {
    private String coinType;
    @JsonSerialize(using = BigIntegerSerializer.class)
    @JsonDeserialize(using = BigIntegerDeserializer.class)
    private BigInteger amount;
    private String name;
    private String symbol;
}
