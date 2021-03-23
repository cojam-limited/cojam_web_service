package io.cojam.web.domain.wallet;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class Token {
    public static final String TICKER = "CT";
    private BigInteger amount;
    private BigInteger tokenPrice;

}
