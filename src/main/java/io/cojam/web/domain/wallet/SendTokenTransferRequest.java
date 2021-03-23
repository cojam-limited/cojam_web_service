package io.cojam.web.domain.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.cojam.web.config.jackson.BigIntegerDeserializer;
import io.cojam.web.config.jackson.BigIntegerSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class SendTokenTransferRequest {
    private String masterWalletId;
    private String userWalletId;
    private String passphrase;
    private String to;
    @JsonSerialize(using = BigIntegerSerializer.class)
    @JsonDeserialize(using = BigIntegerDeserializer.class)
    private BigInteger amount;
    private String ticker;
}
