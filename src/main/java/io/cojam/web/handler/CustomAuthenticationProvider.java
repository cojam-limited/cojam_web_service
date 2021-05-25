package io.cojam.web.handler;


import io.cojam.web.account.Account;
import io.cojam.web.account.UserAccount;
import io.cojam.web.encoder.SHA256Util;
import io.cojam.web.service.CustomUserDetailsService;
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
