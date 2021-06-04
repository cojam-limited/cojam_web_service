package io.cojam.web.dao;

import io.cojam.web.domain.Email;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface EmailDao {

    List<Email> getEnableEmailList(Email email);

    Integer getEnableEmailListCnt(Email email);

    int updateEnableEmail(Email email);

    int deleteEnableEmail(Email email);

    int saveEnableEmail(Email email);

    Email getEnableEmailInfo(Email email);
}
