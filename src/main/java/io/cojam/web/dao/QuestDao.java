package io.cojam.web.dao;

import io.cojam.web.domain.Quest;
import io.cojam.web.domain.QuestAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuestDao {
    int saveQuest(Quest quest);
    int saveQuestAnswer(QuestAnswer answer);

    Integer getQuestListAdminCnt(Quest quest);

    List<Quest> getQuestListAdmin(Quest quest);

    List<Quest> getQuestListMypage(Quest quest);

    int getQuestListMypageCnt(Quest quest);
}
