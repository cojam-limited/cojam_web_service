package io.cojam.web.dao;

import io.cojam.web.domain.AccountDto;
import io.cojam.web.domain.LoginDto;
import io.cojam.web.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MemberDao {

    AccountDto getLoginUserInfo(LoginDto loginDto);

    int saveMemberInfo(Member member);

    Member getMemberInfo(Member member);

    Member getMemberInfByEmail(Member member);

    Member getMemberInfoByEmailName(Member member);

    Integer getMemberPassResetCnt(Member member);

    int saveMemberPassReset(Member member);

    int updateMemberPass(Member member);

    int deletePassHistory(Member member);

    List<Member> getServiceUserList(Member member);

    Integer getServiceUserListCnt(Member member);

    List<Member> getMemberUserList(Member member);

    Integer getMemberUserListCnt(Member member);
}
