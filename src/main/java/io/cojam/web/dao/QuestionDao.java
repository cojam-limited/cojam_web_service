package io.cojam.web.dao;

import io.cojam.web.domain.Question;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuestionDao {

    /**
     * qna 메인 목록
     * @return
     */
    List<Question> getHomeQuestionList();

    /**
     * qna 목록
     * @param question
     * @return
     */
    List<Question> getQuestionList(Question question);

    Integer getQuestionListCnt(Question question);

    int saveQuestionInfo(Question question);

    int updateQuestionInfo(Question question);

    int deleteQuestionInfo(Question question);

    Question getQuestionInfo(Question question);
}
