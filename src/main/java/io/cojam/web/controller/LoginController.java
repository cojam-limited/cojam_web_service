package io.cojam.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.*;
import java.security.spec.RSAPublicKeySpec;

@Controller
public class LoginController {


    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Login custom page
     * @param request
     * @param response
     * @param model
     * @return
     */
    @GetMapping(value = "/login")
    public String loginPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        RequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        try {
            request.getSession().setAttribute("prevPage", savedRequest.getRedirectUrl());
        } catch(NullPointerException e) {
            request.getSession().setAttribute("prevPage", "/");
        }
        return "thymeleaf/page/member/login";
    }

    @GetMapping(value = "/loginP")
    public String loginOtpPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "thymeleaf/page/member/loginP";
    }

}
