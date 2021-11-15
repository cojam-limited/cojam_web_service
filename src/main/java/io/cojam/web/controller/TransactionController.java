package io.cojam.web.controller;


import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @ResponseBody
    @RequestMapping(value = "/cms/transactionCheck", method= RequestMethod.POST)
    public ResponseDataDTO transactionCheck() throws Exception {
        return transactionService.transactionIdStatusCheckProc();
    }
}
