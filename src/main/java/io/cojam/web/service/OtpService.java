package io.cojam.web.service;

import io.cojam.web.dao.MemberDao;
import io.cojam.web.domain.Member;
import io.cojam.web.domain.MemberOtp;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.otp.OtpInfo;
import io.cojam.web.otp.TOTPTokenGenerator;
import io.cojam.web.otp.TOTPTokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class OtpService {

    @Autowired
    MemberDao memberDao;


    public OtpInfo generateSecurityKey(String memberId) {
        String secretKey = TOTPTokenGenerator.generateSecretKey();
        System.out.println(secretKey);
        String email = memberId+"@cojam.io";
        String company = "cojam";
        String barcodeUrl = TOTPTokenGenerator.getGoogleAuthenticatorBarCode(secretKey, email, company);
        System.out.println(barcodeUrl);
        return OtpInfo.builder()
                .secretKey(secretKey)
                .barcodeUrl(barcodeUrl)
                .build();
    }

    public ResponseDataDTO validate(String memberKey,String code){
        ResponseDataDTO response = new ResponseDataDTO();

        MemberOtp otp = memberDao.getOtpInfo(memberKey);
        if(otp == null){
            response.setCheck(false);
            response.setMessage("Invalid 2FA Code");
        }


        if (TOTPTokenValidation.validate(code,otp.getSecretKey())) {
            response.setCheck(true);
            response.setMessage("Logged in successfully");
        } else {
            response.setCheck(false);
            response.setMessage("Invalid 2FA Code");
        }
        return response;
    }


    public String getTOTPCode(String secretCode){
        return TOTPTokenValidation.getTOTPCode(secretCode);
    }
}
