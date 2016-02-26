package com.itoscorp.ilawyer.service.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by Tanat on 2/26/2016.
 */
public class ICSJson {

    List<AttendeeIcs> attendeeIcs;
    OrganizerIcs organizerIcs;
    String location;
    String description;
    String summary;
    String startDate;
    String endDate;

    public ICSJson(Map<String,Object> icsJson) {

        this.attendeeIcs = (List<AttendeeIcs>) icsJson.get("attendee");
        this.location = (String) icsJson.get("location");
        this.description = (String) icsJson.get("description");
        this.summary = (String) icsJson.get("summary");
        this.startDate = (String) icsJson.get("startDate");
        this.endDate = (String) icsJson.get("endDate");
        Map<String,Object> organizerMap = (Map<String, Object>) icsJson.get("organizer");
        this.organizerIcs = new OrganizerIcs();
        this.organizerIcs.mailTo = (String) organizerMap.get("mailto");
        this.organizerIcs.cn = (String) organizerMap.get("cn");
    }

    public List<AttendeeIcs> getAttendeeIcs() {
        return attendeeIcs;
    }

    public void setAttendeeIcs(List<AttendeeIcs> attendeeIcs) {
        this.attendeeIcs = attendeeIcs;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public OrganizerIcs getOrganizerIcs() {
        return organizerIcs;
    }

    public void setOrganizerIcs(OrganizerIcs organizerIcs) {
        this.organizerIcs = organizerIcs;
    }
}
