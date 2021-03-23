package io.cojam.web.domain.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenSendRequest {
    @NotNull(message = "to 필드가 존재해야 합니다.")
    @NotEmpty(message = "to 값이 존재해야 합니다.")
    private String to;
    @NotNull(message = "amount 필드가 존재해야 합니다.")
    private BigInteger amount;
}
