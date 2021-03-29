package io.cojam.web.service.contract;


import io.cojam.web.contract.web3.CojamMarket;
import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.service.WalletApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ContractApplicationService {
    @Autowired
    WalletApiService walletApiService;

    CojamMarket cojamMarket;

    public TransactionReceipt draftMarket(BigInteger marketKey, String creator, String title, BigInteger creatorFee, BigInteger creatorPercentage, BigInteger cojamPercentage, BigInteger charityPercentage, List<BigInteger> answerKeys) {
        // contract 호출
        return null;
    }
}
