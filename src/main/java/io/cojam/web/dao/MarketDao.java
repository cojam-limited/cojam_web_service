package io.cojam.web.dao;

import io.cojam.web.domain.Market;
import io.cojam.web.domain.MarketAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MarketDao {

    int saveMarket(Market market);

    int saveMarketAnswer(MarketAnswer marketAnswer);
}
