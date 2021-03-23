package io.cojam.web.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getYoutubeVideoId(String youtubeUrl) {
        // TODO Auto-generated method stub

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);

        if (matcher.find()) {
            return matcher.group().split("\\\"")[0];
        } else {
            return null;
        }
    }

}
