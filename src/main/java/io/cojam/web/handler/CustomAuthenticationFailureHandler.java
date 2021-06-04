package io.cojam.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.cojam.web.constant.ResponseDataCode;
import io.cojam.web.constant.ResponseDataStatus;
import io.cojam.web.dao.MemberDao;
import io.cojam.web.domain.Member;
import io.cojam.web.domain.MyConfig;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.utils.AES256Util;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 로그인 실패시 로직
 *
 */
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    @Autowired
    MyConfig myConfig;

    @Autowired
    MemberDao memberDao;

    @SneakyThrows
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();	//JSON 변경용
        request.getParameter("memberId");
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        if(exception.getMessage().contains("not Certification")){
            String memberKey = exception.getMessage().split("TT")[1];
            Member param = new Member();
            param.setMemberKey(memberKey);
            Member memberCertification = memberDao.getMemberJoinCertification(param);
            String parameter;
            if(memberCertification == null){
                parameter = String.format("%s**%s**%s", memberKey, "A", "A");
            }else{
                parameter = String.format("%s**%s**%s", memberCertification.getMemberKey(), memberCertification.getMemberEmail(), memberCertification.getFpNumber());
            }
            responseDataDTO.setItem(new AES256Util(myConfig.getJoinParameterKey()).encrypt(parameter));

            responseDataDTO.setCode(ResponseDataCode.NOT_CERTIFICATION);
            responseDataDTO.setStatus(ResponseDataStatus.ERROR);
            responseDataDTO.setMessage("No Certification E-mail.");

        }else if(exception.getMessage().contains("temporary email")){
            responseDataDTO.setCode(ResponseDataCode.ERROR);
            responseDataDTO.setStatus(ResponseDataStatus.ERROR);
            responseDataDTO.setMessage(String.format("please use emails from %s",memberDao.getEnableEmailName()));

        }else{
            responseDataDTO.setCode(ResponseDataCode.ERROR);
            responseDataDTO.setStatus(ResponseDataStatus.ERROR);
            responseDataDTO.setMessage("ID or password do not match.");
        }




        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(mapper.writeValueAsString(responseDataDTO));
        response.getWriter().flush();

    }
}