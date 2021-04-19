package io.cojam.web.klaytn.service;



import io.cojam.web.domain.wallet.TransactionReceipt;
import io.cojam.web.klaytn.dto.Address;
import io.cojam.web.klaytn.dto.Data;
import io.cojam.web.klaytn.exception.KlayFunctionCallException;

import java.math.BigInteger;

abstract class ContractService {
    private WalletApiService walletService;
    private final Address address;

    public ContractService(WalletApiService walletService, Address address) {
        this.walletService = walletService;
        this.address = address;
    }

    <T> T viewFunctionCallTemplate(ViewFunctionCallback<T> callback) {
        try {
            return callback.send();
        } catch (Exception e) {
            throw new KlayFunctionCallException(e);
        }
    }

    interface ViewFunctionCallback<T> {
        T send() throws Exception;
    }


    protected TransactionReceipt sendTransactionMaster(String payload) {
        return walletService.contractCallMaster(address.getValue(), BigInteger.ZERO, new Data(payload));
    }
}
