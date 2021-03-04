package io.cojam.web.service;

import io.cojam.web.dao.SequenceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLSyntaxErrorException;

@Service
public class SequenceService {
    @Autowired
    SequenceDao sequenceDao;

    @Transactional(noRollbackFor = {RuntimeException.class , SQLSyntaxErrorException.class, Exception.class,})
    public String getSequence(String seqType){
        sequenceDao.addSequence(seqType);
        return sequenceDao.getSequence(seqType);
    }

    @Transactional
    public String getSequenceTest(String seqType){
        sequenceDao.addSequenceTest(seqType);
        return sequenceDao.getSequence(seqType);
    }
}
