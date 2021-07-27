package io.cojam.web.config;

import io.cojam.web.handler.*;
import io.cojam.web.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private CustomSuccessLogoutHandler customSuccessLogoutHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
        /* auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
         */
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/assets/css/**"
                , "/assets/js/**"
                , "/assets/image/**"
                , "/assets/image/**/**"
                , "/assets/fonts/**"
                , "/admin/assets/css/**"
                , "/admin/assets/js/**"
                , "/admin/assets/image/**"
                , "/admin/assets/fonts/**"
                , "/**.html"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().and()
                .authorizeRequests()
                .antMatchers(
                        "/login"
                        ,"/logout"
                        ,"/user/join/**"
                        ,"/user/home/**"
                        ,"/user/quest"
                        ,"/user/quest/list"
                        ,"/user/quest/seasonInfo"
                        ,"/user/community/**"
                        ,"/user/about/**"
                        ,"/user/notice/**"
                        ,"/user/idFind/**"
                        ,"/user/pass/**"
                        ,"/user/media/**"
                        ,"/api/v1/totalSupply"
                        ,"/getSuccessInfo"
                ).permitAll()
                .antMatchers("/user/**").hasAnyAuthority("USER","ADMIN","VIP") // /user/ 로 시작하는 URL은 MEMBER라는 권한을 가진 사용자만 접근 가능
                .antMatchers("/cms/**").hasAnyAuthority("ADMIN","VIP") // /member/ 로 시작하는 URL은 MEMBER라는 권한을 가진 사용자만 접근 가능
                //	만약 권한이 없으면 LoginPage로 이동한다.
                // 스프링 시큐리티에서는 ROLE_ 접두어로 붙여주기에 ROLE_MEMBER 를 가진 사람이다.
                .and()
                .formLogin()	//로그인 폼 사용
                .loginPage("/login")	//커스텀 로그인 페이지
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .usernameParameter("memberId")	//스프링 시큐리티에서는 username을 기본 아이디 매핑 값으로 사용하는데 이거 쓰면 변경
                .passwordParameter("memberPassword")	//이건 password를 변경
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(customSuccessLogoutHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

        http
                .exceptionHandling()
                .authenticationEntryPoint(new AjaxAuthenticationEntryPoint("/login"));



    }
}



