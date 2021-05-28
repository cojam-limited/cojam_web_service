package io.cojam.web.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class MyConfig {

    @Value("${myConfig.uploadPath}")
    private String uploadPath;

    @Value("${myConfig.hostUrl}")
    private String hostUrl;

    @Value("${myConfig.klaytnScpe}")
    private String klaytnScpe;

    @Value("${app.sdk-enclave.marketAddress}")
    private String marketAddress;

    @Value("${app.sdk-enclave.tokenAddress}")
    private String tokenAddress;

    @Value("${app.sdk-enclave.recommendAddress}")
    private String recommendAddress;

    @Value("${app.sdk-enclave.charityAddress}")
    private String charityAddress;


    @Value("${myConfig.joinParameterKey}")
    private String joinParameterKey;

    @Value("${spring.profiles.active}")
    private String profile;

}
