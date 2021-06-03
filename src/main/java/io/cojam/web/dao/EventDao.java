package io.cojam.web.dao;


import io.cojam.web.domain.EventSendHistory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface EventDao {

    Integer checkSendEvent(String type);

    int updateSendYn(String type);

    List<EventSendHistory> getEventRewardList();

    int updateEventRewardHistory(EventSendHistory eventSendHistory);
}
