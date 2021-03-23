package io.cojam.web.domain.wallet;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TransferDTO extends TransactionOnlyResultDTO {
    @Builder
    public TransferDTO(String transactionId) {
        super(transactionId);
    }
}
