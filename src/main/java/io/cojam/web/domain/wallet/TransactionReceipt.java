package io.cojam.web.domain.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionReceipt {
    @JsonProperty("id")
    private String transactionId;
    private String toAddress;

    public static TransactionReceipt ofEmpty() {
        return new TransactionReceipt(null, null);
    }

    public TransactionReceipt assertThenReturn(String callerMethodName, Object args) {
        if (transactionId == null) {
            throw new RuntimeException(String.format("%s 결과 트랜잭션 아이디를 찾을 수 없습니다. 인자 정보: %s", callerMethodName, args));
        }
        return this;
    }

    public TransactionReceipt assertThenReturn(String callerMethodName, String memberId, Object args) {
        if (transactionId == null) {
            throw new RuntimeException(String.format("%s 결과 트랜잭션 아이디를 찾을 수 없습니다. 멤버 정보: %s, 인자 정보: %s", callerMethodName, memberId, args));
        }
        return this;
    }

    public TransactionReceipt assertThenReturn(String callerMethodName, String memberId, String spaceId, Object args) {
        if (transactionId == null) {
            throw new RuntimeException(String.format("%s 결과 트랜잭션 아이디를 찾을 수 없습니다. 멤버 정보: %s, 스페이스 정보: %s, 인자 정보: %s", callerMethodName, memberId, spaceId, args));
        }
        return this;
    }
}
