package io.cojam.web.otp;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

public class TOTPTokenValidation {



    public static boolean validate(String inputCode,String secretKey) {
        String code = getTOTPCode(secretKey);
        return code.equals(inputCode);
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

}