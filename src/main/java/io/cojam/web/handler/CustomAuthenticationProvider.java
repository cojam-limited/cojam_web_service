package io.cojam.web.handler;


import io.cojam.web.account.Account;
import io.cojam.web.account.UserAccount;
import io.cojam.web.dao.MemberDao;
import io.cojam.web.domain.Member;
import io.cojam.web.encoder.SHA256Util;
import io.cojam.web.service.CustomUserDetailsService;
import io.cojam.web.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    MemberDao memberDao;

    @Autowired
    MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        UserAccount userAccount = (UserAccount) userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(SHA256Util.getEncrypt(password,""), userAccount.getPassword())) {
            throw new BadCredentialsException("Password not match");
        }
        Account account = userAccount.getAccount();
        account.setMemberPassword("");

        if (!passwordEncoder.matches(SHA256Util.getEncrypt(password,""), userAccount.getPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        Member member = new Member();
        member.setMemberKey(account.getMemberKey());
        Member detail = memberService.getMemberInfoForMemberKey(member);



        if(detail!=null && !StringUtils.isBlank(detail.getMemberEmail())){
            String emailName = detail.getMemberEmail().split("@")[1];
            if(memberDao.checkEnableEmailName(emailName.trim()) < 1){
                throw new DisabledException("temporary email");
            }

        }

        if (!account.getCertification()) {
            throw new DisabledException("not CertificationTT"+account.getMemberKey());
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                account,
                null,
                userAccount.getAuthorities());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
