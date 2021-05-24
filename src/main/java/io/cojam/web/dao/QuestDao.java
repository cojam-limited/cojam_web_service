package io.cojam.web.dao;

import io.cojam.web.domain.*;
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

    List<Betting> getBettingChartList(Betting betting);

    int updateBetting(Betting betting);

    int updateQuestAnswer(QuestAnswer answer);

    int updateQuestTotalAmount(Quest quest);

    List<MyVoting> getMyVotingList(MyVoting myVoting);

    Integer getMyVotingListCnt(MyVoting myVoting);

    Betting getBettingDetail(Betting betting);

    int updateBettingSuccess(Betting betting);

    List<Question> getHomeQuestList(Question question);
}
