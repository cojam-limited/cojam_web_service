package io.cojam.web.klaytn.config;

import io.cojam.web.klaytn.dto.Address;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class BlockchainConfig {

    @Value("${app.klaytn.contracts.marketAddress}")
    private String marketAddress;

    @Qualifier("market")
    @Bean
    public Address cojamMarketAddress() {
        return new Address(this.marketAddress);
    }
}
