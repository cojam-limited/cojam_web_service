package io.cojam.web.domain.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionOnlyResultDTO {
    private String transactionId;
}
