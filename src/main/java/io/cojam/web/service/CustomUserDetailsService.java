package io.cojam.web.service;

import io.cojam.web.account.Account;
import io.cojam.web.account.UserAccount;
import io.cojam.web.domain.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 인증 하는 부분
     */
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        AccountDto memberInfo = userService.getLoginUserInfo(memberId);
        if(memberInfo ==null){
            throw new UsernameNotFoundException(memberId);
        }

        if(!memberInfo.getAccess()){
            throw new UsernameNotFoundException(memberId);
        }

        Account account = new Account();
        account.setMemberKey(memberInfo.getMemberKey());
        account.setMemberPassword(memberInfo.getMemberPassword());
        account.setMemberId(memberInfo.getMemberId());
        account.setMemberName(memberInfo.getMemberName());
        account.setMemberAddress(memberInfo.getMemberAddress());
        account.setMemberPhoneNumber(memberInfo.getMemberPhoneNumber());
        account.setMemberRole(memberInfo.getMemberRole());
        account.setCertification(memberInfo.getCertification());
        return new UserAccount(account);
    }

    /**
     * 권한 받아오는 부분
     * @param memberInfo
     * @return
     */
    private Collection<? extends GrantedAuthority> getAuthorities(AccountDto memberInfo) {
        String[] userRoles =  {"USER","ADMIN"};
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
        return authorities;
    }



}
