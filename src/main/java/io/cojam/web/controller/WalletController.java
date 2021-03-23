package io.cojam.web.controller;

import com.klaytn.caver.utils.Convert;
import io.cojam.web.account.Account;
import io.cojam.web.domain.ResponseDataDTO;

import io.cojam.web.domain.wallet.TokenSendRequest;
import io.cojam.web.domain.wallet.TransferDTO;
import io.cojam.web.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class WalletController {


    @Autowired
    WalletService walletService;

    @ResponseBody
    @RequestMapping(value = "/user/wallet/balance", method= RequestMethod.POST)
    public ResponseDataDTO balance(@AuthenticationPrincipal Account account , HttpServletResponse response) throws Exception {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        responseDataDTO.setMessage("success");
        responseDataDTO.setCheck(true);
        responseDataDTO.setItem(walletService.getWalletBalance(account.getMemberKey()));
        return responseDataDTO;
    }

    @ResponseBody
    @RequestMapping(value = "/user/wallet/transfer", method= RequestMethod.POST)
    public ResponseDataDTO transfer(@AuthenticationPrincipal Account account
            ,@Valid TokenSendRequest request
            , HttpServletResponse response) throws Exception {
        ResponseDataDTO responseDataDTO = new ResponseDataDTO();
        request.setAmount(Convert.toPeb(request.getAmount().toString(), Convert.Unit.KLAY).toBigInteger());
        return walletService.sendToken(account.getMemberKey(),request);
    }
}
