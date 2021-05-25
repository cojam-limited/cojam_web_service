package io.cojam.web.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserAccount extends User {
    private Account account;

    public UserAccount(Account account) {

        super(account.getMemberId(), account.getMemberPassword(),
                getAuthorities(account));
        this.account = account;
    }

    /**
     * 권한 받아오는 부분
     * @param account
     * @return
     */
    private static Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        String[] userRoles = new String[1];
        userRoles[0] = account.getMemberRole();
        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
        return authorities;
    }
}
