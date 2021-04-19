package io.cojam.web.utils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

public class EthSigner {
    public EthSigner() {
    }

    public static Credentials getDummyCredentials() {
        ECKeyPair dummyKeyPair;
        try {
            dummyKeyPair = Keys.createEcKeyPair();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }

        return Credentials.create(dummyKeyPair);
    }
}