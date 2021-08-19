package io.cojam.web.controller;

import io.cojam.web.account.Account;
import io.cojam.web.domain.DeviceInfo;
import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.DeviceService;
import io.cojam.web.service.PushMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    @ResponseBody
    @RequestMapping(value = "/saveDeviceInfo" , method = RequestMethod.POST)
    public ResponseDataDTO getAnswerList(
            @RequestBody DeviceInfo deviceInfo
            , @AuthenticationPrincipal Account account
            ) throws Exception {
        ResponseDataDTO response = new ResponseDataDTO();
        if(account != null){
            if(!StringUtils.isBlank(account.getMemberKey())){
                deviceInfo.setMemberKey(account.getMemberKey());
            }
        }

        response = deviceService.saveDeviceProc(account,deviceInfo);
        return response;
    }

}
