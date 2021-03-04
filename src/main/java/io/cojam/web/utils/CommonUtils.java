package io.cojam.web.utils;

import java.util.Random;

public class CommonUtils {

    public static String getAuthCode(int cnt){

        String authCode="";

        final char[] possibleCharacters =
                {'1','2','3','4','5','6','7','8','9','0','A','B','C','D','E','F',
                        'G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V',
                        'W','X','Y','Z'};

        final int possibleCharacterCount = possibleCharacters.length;

        Random rnd = new Random();

        int i = 0;

        StringBuffer buf = new StringBuffer(cnt*2);
        for (i= cnt; i > 0; i--) {
            buf.append(possibleCharacters[rnd.nextInt(possibleCharacterCount)]);
        }
        authCode = buf.toString();


        return authCode;
    }

}
