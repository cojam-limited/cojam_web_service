package io.cojam.web.dao;

import io.cojam.web.domain.RewardInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface RewardDao {

    RewardInfo getRewardInfo(String rewardType);

    List<RewardInfo> getRewardInfoList();

    int updateRewardInfo(RewardInfo rewardInfo);

    int updateRewardInfoRecUseYn(RewardInfo rewardInfo);
}
