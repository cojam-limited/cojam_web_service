package io.cojam.web.dao;

import io.cojam.web.domain.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface WalletDao {

    int saveWallet(Wallet wallet);

    Wallet getWalletInfo(String memberKey);
}
