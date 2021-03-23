package io.cojam.web.auth;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class HMACService {
    public final String hmacSHA256 = "HmacSHA256";
    private final Mac mac;

    public HMACService() throws NoSuchAlgorithmException {
        this.mac = Mac.getInstance(hmacSHA256);
    }

    public String calculateHMAC(String key, String message) {
        try {
            mac.init(new SecretKeySpec(key.getBytes(), hmacSHA256));
        } catch (InvalidKeyException e) {
            throw new BadCredentialsException("failed to calculate hmac", e);
        }

        byte[] hash = mac.doFinal(message.getBytes());
        return Base64.encodeBase64String(hash);
    }

    public boolean verify(String key, String message, String signature) throws BadCredentialsException {
        String result = this.calculateHMAC(key, message);
        return result.equals(signature);
    }
}

