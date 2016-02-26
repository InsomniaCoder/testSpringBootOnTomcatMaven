package com.itoscorp.ilawyer.service.controller;

import com.itoscorp.ilawyer.service.entity.ICSJson;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Tanat on 2/24/2016.
 */
@RestController
public class CalendarController {

    @RequestMapping(value = "/sendICSInvite", method = RequestMethod.POST)
    public String sendICSInvite(
            @RequestBody Map<String,Object> icsJson){

        ICSJson icsJsonObj = new ICSJson(icsJson);
        System.out.println(icsJsonObj);
        return String.valueOf(HttpStatus.OK);
    }//end send

}//end class
