package com.itoscorp.ilawyer.service.utility;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.property.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tanat on 2/24/2016.
 */
public class ICSGenerator {

    static SimpleDateFormat icsDateFormat =  new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");

    public static void main(String[] args) throws URISyntaxException, ParseException {

        //generate file name that unique in time
        String dynamicICSFileName = createDynamicICSFileName();

        //create instance of calendar and event
        Calendar calendar = createCalendar();

        //generate ICS file from calendar
        generateICSFile(calendar,dynamicICSFileName);

    }//end main

    private static String createDynamicICSFileName() {

        StringBuilder fileNameBuilder = new StringBuilder(icsDateFormat.format(new Date()));
        fileNameBuilder.append(Math.random()/10);
        String dynamicICSFileName = fileNameBuilder.toString()+".ics";
        return dynamicICSFileName;
    }

    private static Calendar createCalendar() throws URISyntaxException, ParseException {

        //create iCal4J Calendar instance
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();

        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(Method.REQUEST);

        VTimeZone vTimeZone = new VTimeZone();
        vTimeZone.getProperties().add(new TzId("Asia/Bangkok"));
        calendar.getComponents().add(vTimeZone);

        VEvent meeting = createEvent();
        calendar.getComponents().add(meeting);
        return calendar;
    }

    private static VEvent createEvent() throws URISyntaxException, ParseException {

        //date format of ics create/start/end properties
        VEvent meeting = new VEvent();

        //set attendee info
        //for each from array of attendee
        Attendee testAttendee1 = new Attendee("mailto:noppadorn@itoscorp.com");
        testAttendee1.getParameters().add(new CuType("INDIVIDUAL"));
        testAttendee1.getParameters().add(new Cn("พี่เหลียง"));

        Attendee testAttendee2 = new Attendee("mailto:tanat@itoscorp.com");
        testAttendee2.getParameters().add(new CuType("INDIVIDUAL"));
        testAttendee2.getParameters().add(new Cn("ปอ"));
        meeting.getProperties().add(testAttendee1);
        meeting.getProperties().add(testAttendee2);

        String strDate = icsDateFormat.format(new Date());

        net.fortuna.ical4j.model.Date startDt = new net.fortuna.ical4j.model.Date(strDate,icsDateFormat.toPattern());
        meeting.getProperties().add(new Created(strDate));
        meeting.getProperties().add(new DtStart(strDate));
        meeting.getProperties().add(new DtEnd(strDate));

        //set general info
        meeting.getProperties().add(new Location("ITOS Office"));
        meeting.getProperties().add(new Description("Testing purpose description"));
        meeting.getProperties().add(new Summary("iLawyer Test Header"));
        //set organizer info
        Organizer organizer = new Organizer("mailto:"+"tanat@itoscorp.com");
        organizer.getParameters().add(new Cn("Por Por"));
        meeting.getProperties().add(organizer);

        return meeting;
    }

    private static void generateICSFile(Calendar calendar, String dynamicICSFileName) {
        FileOutputStream fileOutputStream = null;

        try {
            //open output stream to dynamic file
            fileOutputStream = new FileOutputStream(dynamicICSFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CalendarOutputter outputTer = new CalendarOutputter();
        outputTer.setValidating(false);

        try {
            outputTer.output(calendar, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }

    }
}//end class
