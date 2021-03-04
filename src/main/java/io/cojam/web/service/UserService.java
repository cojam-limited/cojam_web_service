package io.cojam.web.service;

import io.cojam.web.domain.AccountDto;
import io.cojam.web.domain.LoginDto;
import io.cojam.web.dao.MemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    MemberDao memberDao;

    public AccountDto getLoginUserInfo(String userId){
        LoginDto loginDto = new LoginDto();
        loginDto.setUserId(userId);
        return memberDao.getLoginUserInfo(loginDto);
    }
}
