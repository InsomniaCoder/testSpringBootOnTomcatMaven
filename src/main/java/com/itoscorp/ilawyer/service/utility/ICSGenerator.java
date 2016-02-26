package com.itoscorp.ilawyer.service.utility;

import com.itoscorp.ilawyer.service.entity.AttendeeIcs;
import com.itoscorp.ilawyer.service.entity.ICSJson;
import com.itoscorp.ilawyer.service.entity.OrganizerIcs;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.CuType;
import net.fortuna.ical4j.model.property.*;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Tanat on 2/24/2016.
 */
@Component
public class ICSGenerator {

    static SimpleDateFormat icsDateFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");

    public void createAndSendICS(ICSJson icsJsonObj) throws URISyntaxException, ParseException {
        //create calendar object and from json
        Calendar calendar = createCalendar(icsJsonObj);
        sendICSToSmtpServer(calendar);
    }

    private Calendar createCalendar(ICSJson icsJsonObj) throws URISyntaxException, ParseException {

        Calendar calendar = initialCalendarInstance();
        createEvent(calendar, icsJsonObj);
        return calendar;
    }

    private Calendar initialCalendarInstance() throws URISyntaxException, ParseException {
        //create iCal4J Calendar instance
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();

        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(Method.REQUEST);

        VTimeZone vTimeZone = new VTimeZone();
        vTimeZone.getProperties().add(new TzId("Asia/Bangkok"));
        calendar.getComponents().add(vTimeZone);

        return calendar;
    }

    private void createEvent(Calendar calendar, ICSJson icsJsonObj) throws URISyntaxException, ParseException {

        VEvent meeting = new VEvent();

        List<AttendeeIcs> attendeeIcs = icsJsonObj.getAttendeeIcs();
        OrganizerIcs organizerIcs = icsJsonObj.getOrganizerIcs();
        String location = icsJsonObj.getLocation();
        String description = icsJsonObj.getDescription();
        String summary = icsJsonObj.getSummary();
        String startDate = icsJsonObj.getStartDate();
        String endDate = icsJsonObj.getEndDate();

        for (AttendeeIcs attendee : attendeeIcs) {

            Attendee eachAttendee = new Attendee("mailto:"+attendee.getMailTo());
            eachAttendee.getParameters().add(new CuType("INDIVIDUAL"));
            eachAttendee.getParameters().add(new Cn(attendee.getCn()));
            meeting.getProperties().add(eachAttendee);
        }

        //set Date info
        String strDate = icsDateFormat.format(new Date());
        meeting.getProperties().add(new Created(strDate));
        meeting.getProperties().add(new DtStart(startDate));
        meeting.getProperties().add(new DtEnd(endDate));

        //set general info
        meeting.getProperties().add(new Location(location));
        meeting.getProperties().add(new Description(description));
        meeting.getProperties().add(new Summary(summary));

        //set organizer info
        Organizer organizer = new Organizer("mailto:" + organizerIcs.getMailTo());
        organizer.getParameters().add(new Cn(organizerIcs.getCn()));
        meeting.getProperties().add(organizer);

        //attach event to main calendar
        calendar.getComponents().add(meeting);
    }


    private void sendICSToSmtpServer(Calendar calendar) {

        String dynamicICSFileName = createDynamicICSFileName();

        FileOutputStream fileOutputStream = null;

        try {
            //create File at SMTP outgoing directory
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

    private String createDynamicICSFileName() {

        StringBuilder fileNameBuilder = new StringBuilder(icsDateFormat.format(new Date()));
        fileNameBuilder.append(Math.random() / 10);
        String dynamicICSFileName = fileNameBuilder.toString() + ".ics";
        return dynamicICSFileName;
    }
}//end class
