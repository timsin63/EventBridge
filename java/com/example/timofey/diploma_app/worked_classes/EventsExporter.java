package com.example.timofey.diploma_app.worked_classes;

import android.util.Log;

import com.example.timofey.diploma_app.data_classes.VKEvent;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.util.Arrays;

/**
 * Created by timofey on 14.06.2016.
 */
public class EventsExporter {

    public EventsExporter(){}

    public static Event getGoogleEvent(VKEvent vkEvent){

        Event event = new Event()
                .setSummary(vkEvent.getTitle())
                .setLocation(vkEvent.getPlace())
                .setDescription(vkEvent.getDefinition());

        DateTime dateTimeStart = new DateTime(vkEvent.getDateStart() * 1000);
        Log.v("event", "DateTimeStart: " + dateTimeStart.toString());
        EventDateTime start = new EventDateTime()
                .setDateTime(dateTimeStart);
        event.setStart(start);

        DateTime dateTimeEnd = new DateTime(vkEvent.getDateEnd() * 1000);
        Log.v("event","DateTimeEnd: " + dateTimeEnd.toString());
        EventDateTime end = new EventDateTime()
                .setDateTime(dateTimeEnd);
        event.setEnd(end);

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("popup")
                .setMinutes(180)
        };

        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        return event;
    }


}
