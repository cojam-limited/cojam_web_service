package io.cojam.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/cms/ground")
public class GroundController {

    @RequestMapping(value = "/adjourn" , method = RequestMethod.GET)
    public String adjourn() {

        return "thymeleaf/page/cms/ground/adjourn";
    }

    @RequestMapping(value = "/approve" , method = RequestMethod.GET)
    public String approve() {

        return "thymeleaf/page/cms/ground/approve";
    }

    @RequestMapping(value = "/invalid" , method = RequestMethod.GET)
    public String invalid() {

        return "thymeleaf/page/cms/ground/invalid";
    }


    @RequestMapping(value = "/ongoing" , method = RequestMethod.GET)
    public String ongoing() {

        return "thymeleaf/page/cms/ground/ongoing";
    }

    @RequestMapping(value = "/success" , method = RequestMethod.GET)
    public String success() {

        return "thymeleaf/page/cms/ground/success";
    }
}
