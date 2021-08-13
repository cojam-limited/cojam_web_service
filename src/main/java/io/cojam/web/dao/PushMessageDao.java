package io.cojam.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface PushMessageDao {

    List<String> getBettingTokenList(String key);

    List<String> getNoticeTokenList(String key);
}
