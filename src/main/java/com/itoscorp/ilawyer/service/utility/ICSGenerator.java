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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by Tanat on 2/24/2016.
 */
@Component
public class ICSGenerator {

    static SimpleDateFormat icsDateFormat = new SimpleDateFormat("yyyyMMdd'T'hhmmss'Z'");

    public void createAndSendICS(ICSJson icsJsonObj) throws URISyntaxException, ParseException {
        //create calendar object and from json
        Calendar calendar = createCalendar(icsJsonObj);
        File icsFile = createICSFile(calendar);
        sendICSViaJavaMailAPI(icsJsonObj,icsFile);
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


    private File createICSFile(Calendar calendar) {

        String dynamicICSFileName = createDynamicICSFileName();

        FileOutputStream fileOutputStream = null;

        File icsFile = new File(dynamicICSFileName);
        try {
            //create File at SMTP outgoing directory
            fileOutputStream = new FileOutputStream(icsFile);
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

        return icsFile;
    }



    private String createDynamicICSFileName() {

        StringBuilder fileNameBuilder = new StringBuilder(icsDateFormat.format(new Date()));
        fileNameBuilder.append(Math.random() / 10);
        String dynamicICSFileName = fileNameBuilder.toString() + ".ics";
        return dynamicICSFileName;
    }

    private void sendICSViaJavaMailAPI(ICSJson icsJsonObj, File icsFile){


        String from = icsJsonObj.getOrganizerIcs().getMailTo();
        String subject = icsJsonObj.getSummary();
        String messageText = icsJsonObj.getDescription();

        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("tanat@itoscorp.com", "tanat12345");
                    }
                });
        List<AttendeeIcs> attendeeIcs = icsJsonObj.getAttendeeIcs();

        for (AttendeeIcs attendeeIc : attendeeIcs) {

            String to = attendeeIc.getMailTo();

            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject(subject);

                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();

                // Now set the actual message
                messageBodyPart.setText(messageText);

                // Create a multipart message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Part two is attachment
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(icsFile);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName("ics");
                multipart.addBodyPart(messageBodyPart);

                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);

                System.out.println("Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        }
    }
}//end class
