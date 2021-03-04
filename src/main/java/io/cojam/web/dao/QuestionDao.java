package io.cojam.web.dao;

import io.cojam.web.domain.Question;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuestionDao {

    List<Question> getHomeQuestionList();
}
