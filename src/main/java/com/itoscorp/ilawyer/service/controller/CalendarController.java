package com.itoscorp.ilawyer.service.controller;

import com.itoscorp.ilawyer.service.entity.ICSJson;
import com.itoscorp.ilawyer.service.utility.ICSGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by Tanat on 2/24/2016.
 */
@RestController
public class CalendarController {


    @Autowired
    ICSGenerator icsGenerator;

    @RequestMapping(value = "/sendICSInvite", method = RequestMethod.POST)
    public String sendICSInvite(
            @RequestBody Map<String,Object> icsJson) throws URISyntaxException, ParseException {

        //transform JSON from request body to Object
        ICSJson icsJsonObj = new ICSJson(icsJson);
        //generate file and send
        icsGenerator.createAndSendICS(icsJsonObj);
        return String.valueOf(HttpStatus.OK);
    }//end send

}//end class
