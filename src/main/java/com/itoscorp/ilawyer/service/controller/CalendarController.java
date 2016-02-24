package com.itoscorp.ilawyer.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Tanat on 2/24/2016.
 */
@RestController
public class CalendarController {

    @RequestMapping(value = "/sendICSInvite", method = RequestMethod.POST)
    public String sendICSInvite(){



        return "200";
    }//end send

}//end class
