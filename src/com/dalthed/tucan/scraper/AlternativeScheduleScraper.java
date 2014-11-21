package com.dalthed.tucan.scraper;

import android.content.Context;

import com.dalthed.tucan.datamodel.Appointment;
import com.dalthed.tucan.util.ScheduleSaver;



import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Location;
import biweekly.property.Summary;
import biweekly.util.DateTimeComponents;


/**
 * Created by yttyd_000 on 06.11.2014.
 */
public class AlternativeScheduleScraper {


    private final Context context;
    private ICalendar calendar;


    public AlternativeScheduleScraper(Context context, Reader icsFile) throws IOException {
        this.context = context;
        calendar = Biweekly.parse(icsFile).first();
        List<VEvent> events = calendar.getEvents();
        List<Appointment> appointments = new ArrayList<Appointment>();
        for (VEvent event : events) {
            DateTimeComponents dateStart = event.getDateStart().getRawComponents();
            DateTimeComponents dateEnd = event.getDateEnd().getRawComponents();
            Location location = event.getLocation();
            Summary summary = event.getSummary();

            Appointment appointment = new Appointment(dateStart.getYear(), dateStart.getMonth(),
                    dateStart.getDate(),
                    dateStart.getHour(), dateStart.getMinute(), dateEnd.getHour(),
                    dateEnd.getMinute(), summary.getValue(), location.getValue(), "");
            appointments.add(appointment);
        }
        ScheduleSaver.saveSchedule(appointments);


    }


}
