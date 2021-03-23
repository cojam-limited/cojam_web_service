package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.QuestionDao;
import io.cojam.web.domain.Question;
import io.cojam.web.domain.ResponseDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    SequenceService sequenceService;

    public List<Question> getHomeQuestionList(){
        return questionDao.getHomeQuestionList();
    }

    public List<Question> getQuestionList(Question question){
        return questionDao.getQuestionList(question);
    }

    public Integer getQuestionListCnt(Question question){
        return questionDao.getQuestionListCnt(question);
    }

    public int saveQuestionInfo(Question question){
        return questionDao.saveQuestionInfo(question);
    }

    public int updateQuestionInfo(Question question){
        return questionDao.updateQuestionInfo(question);
    }

    public int deleteQuestionInfo(Question question){
        return questionDao.deleteQuestionInfo(question);
    }

    public Question getQuestionInfo(Question question){
        return questionDao.getQuestionInfo(question);
    }

    @Transactional
    public ResponseDataDTO saveQuestion(Question question, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        question.setCreateMemberKey(account.getMemberKey());
        question.setUpdateMemberKey(account.getMemberKey());

        question.setQuestionKey(sequenceService.getSequence(SequenceCode.TB_QUESTION));

        questionDao.saveQuestionInfo(question);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(question.getQuestionKey());
        return responseDataDTO;
    }

    @Transactional
    public ResponseDataDTO updateQuestion(Question question, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        question.setCreateMemberKey(account.getMemberKey());
        question.setUpdateMemberKey(account.getMemberKey());

        Question detail = this.getQuestionInfo(question);
        if(detail==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
        }

        questionDao.updateQuestionInfo(question);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        responseDataDTO.setItem(question.getQuestionKey());
        return responseDataDTO;
    }

    @Transactional
    public ResponseDataDTO deleteQuestion(Question question, Account account){
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        question.setCreateMemberKey(account.getMemberKey());
        question.setUpdateMemberKey(account.getMemberKey());

        Question detail = questionDao.getQuestionInfo(question);
        if(detail==null){
            responseDataDTO.setCheck(false);
            responseDataDTO.setMessage("no data.");
        }

        questionDao.deleteQuestionInfo(question);
        responseDataDTO.setCheck(true);
        responseDataDTO.setMessage("success");
        return responseDataDTO;
    }
}
