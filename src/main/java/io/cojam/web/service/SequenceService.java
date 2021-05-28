package io.cojam.web.service;

import io.cojam.web.constant.SequenceCode;
import io.cojam.web.dao.SequenceDao;
import io.cojam.web.domain.MyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.SQLSyntaxErrorException;

@Service
public class SequenceService {
    @Autowired
    SequenceDao sequenceDao;

    @Autowired
    MyConfig myConfig;

    @Transactional(noRollbackFor = {RuntimeException.class , SQLSyntaxErrorException.class, Exception.class,})
    public String getSequence(String seqType){
        String profile = myConfig.getProfile(); // local
        if(profile.equals("local")){
            seqType = seqType +"L";
        }else if(profile.equals("dev")){
            seqType = seqType +"D";
        }
        sequenceDao.addSequence(seqType);
        return sequenceDao.getSequence(seqType);
    }

    public BigInteger changeSequenceStringToBigInteger(String key,String seqType){
        String profile = myConfig.getProfile(); // local
        if(profile.equals("local")){
            seqType = seqType +"L";
        }else if(profile.equals("dev")){
            seqType = seqType +"D";
        }
        return new BigInteger(key.replace(seqType,""));
    }

    @Transactional
    public String getSequenceTest(String seqType){
        sequenceDao.addSequenceTest(seqType);
        return sequenceDao.getSequence(seqType);
    }
}
