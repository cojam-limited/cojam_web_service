package io.cojam.web.controller;

import io.cojam.web.domain.ResponseDataDTO;
import io.cojam.web.service.EventService;
import io.cojam.web.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class EventController {

    @Autowired
    EventService eventService;


    @ResponseBody
    @RequestMapping(value = "/event/sendReward")
    public ResponseDataDTO sendReward() {
        return null;
        //return eventService.sendReward();
    }
}
