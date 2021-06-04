package io.cojam.web.dao;

import io.cojam.web.domain.AccountDto;
import io.cojam.web.domain.LoginDto;
import io.cojam.web.domain.Member;
import io.cojam.web.domain.Recommend;
import jnr.a64asm.Mem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    int updateMemberRole(Member member);

    Member getMemberInfoForMemberKey(Member member);

    int saveRecommend(Recommend recommend);

    int updateRecommend(Recommend recommend);

    Integer getRecommendCount(String memberKey);

    Member getMyRecommendInfo(String memberKey);

    int updateMemberLock(Map<String ,Object> paramMap);

    int saveMemberJoinCertification(Member member);

    Member getMemberJoinCertification(Member member);

    int removeMemberJoinCertification(Member member);

    int updateMemberJoinCertification(Member member);

    Integer checkRejectEmailName(String emailName);

    int updateMemberAccess(Member member);

    Integer checkEnableEmailName(String emailName);

    String getEnableEmailName();
}
