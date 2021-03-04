package io.cojam.web.service;

import io.cojam.web.dao.QuestionDao;
import io.cojam.web.domain.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    public List<Question> getHomeQuestionList(){
        return questionDao.getHomeQuestionList();
    }
}
