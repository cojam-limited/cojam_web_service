package io.cojam.web.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLSyntaxErrorException;

@Repository
@Mapper
public interface SequenceDao {

    int addSequence(String seqType);

    int addSequenceTest(String seqType);

    String getSequence(String seqType);
}
