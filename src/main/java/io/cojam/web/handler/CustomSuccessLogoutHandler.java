package io.cojam.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cojam.web.constant.ResponseDataCode;
import io.cojam.web.constant.ResponseDataStatus;
import io.cojam.web.domain.ResponseDataDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomSuccessLogoutHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String refererUrl = request.getHeader("REFERER");
        if(authentication!=null && authentication.getDetails()!=null){
            request.getSession().invalidate();
        }

        ObjectMapper mapper = new ObjectMapper();	//JSON 변경용

        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        responseDataDTO.setCode(ResponseDataCode.SUCCESS);
        responseDataDTO.setStatus(ResponseDataStatus.SUCCESS);

        Map<String, String> items = new HashMap<String,String>();
        items.put("url", refererUrl.isEmpty()?"/user/home":refererUrl);	// 이전 페이지 저장
        responseDataDTO.setItem(items);

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(mapper.writeValueAsString(responseDataDTO));
        response.getWriter().flush();

    }
}
