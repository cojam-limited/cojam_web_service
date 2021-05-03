package io.cojam.web.dao;

import io.cojam.web.domain.JoinRewardHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface JoinRewardHistoryDao {

    Integer getJoinRewardInfo(String memberKey);

    int saveJoinRewardHistory(JoinRewardHistory joinRewardHistory);

}
