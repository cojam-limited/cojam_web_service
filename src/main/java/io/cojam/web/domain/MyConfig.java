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

}
