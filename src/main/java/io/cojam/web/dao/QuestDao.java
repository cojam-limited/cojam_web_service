package io.cojam.web.dao;

import io.cojam.web.domain.Betting;
import io.cojam.web.domain.MyVoting;
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

    Quest getQuestDetail(String questKey);

    int updateQuestStatus(Quest quest);

    List<QuestAnswer> getQuestAnswerList(String questKey);

    List<Quest> getQuestListUser(Quest quest);

    Integer getQuestListUserCnt(Quest quest);

    Quest getQuestDetailUser(Quest quest);

    QuestAnswer getQuestAnswerDetail(QuestAnswer answer);

    int saveBetting(Betting betting);

    List<Betting> getBettingList(Betting betting);

    int updateBetting(Betting betting);

    int updateQuestAnswer(QuestAnswer answer);

    int updateQuestTotalAmount(Quest quest);

    List<MyVoting> getMyVotingList(MyVoting myVoting);

    Integer getMyVotingListCnt(MyVoting myVoting);
}
