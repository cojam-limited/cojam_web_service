package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Controller
public class TestController {
    /*
    @Autowired
    QuestService questService;

    @ResponseBody
    @RequestMapping(value = "/getSuccessInfo" , method = RequestMethod.GET)
    public ResponseDataDTO getSuccessInfo(
            @NotNull @NotEmpty String selectedQuestKey
            ,@NotNull @NotEmpty  String selectedAnswerKey) throws Exception {
        return questService.getSuccessInfo(selectedQuestKey,selectedAnswerKey);
    }
     */
}
