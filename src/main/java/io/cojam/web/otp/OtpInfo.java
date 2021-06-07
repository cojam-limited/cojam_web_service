package io.cojam.web.otp;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OtpInfo {
    private String secretKey;
    private String barcodeUrl;
    private String memberKey;

    @Builder
    public OtpInfo(String secretKey, String barcodeUrl,String memberKey){
        this.secretKey = secretKey;
        this.barcodeUrl = barcodeUrl;
        this.memberKey = memberKey;
    }
}
