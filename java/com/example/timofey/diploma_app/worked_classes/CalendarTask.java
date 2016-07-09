package com.example.timofey.diploma_app.worked_classes;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.timofey.diploma_app.activities.MainActivity;
import com.example.timofey.diploma_app.data_classes.VKEvent;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by timofey on 09.07.2016.
 */
public class CalendarTask extends AsyncTask<VKEvent, Void, Void> {

    private Context context;
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;
    int count = 0;
    GoogleAccountCredential credential;
    String activityName;

    public CalendarTask(GoogleAccountCredential credential, Context context, String activityName) {

        Log.v("Task", "task запущен");
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        this.context = context;
        this.credential = credential;
        this.activityName = activityName;
        mService = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Event Bridge").build();

        Log.v("Task", "Календарь создан");
    }

    @Override
    protected Void doInBackground(VKEvent... params) {
        try {
            Log.v("Task", "doInBackground выполняется");
            for (VKEvent event : params) {
                Log.v("Task", "param0" + event.toString());
            }
            insertTheEvent(params);

            Log.v("Task", "Success");
        } catch (Exception e) {
            mLastError = e;
            e.printStackTrace();
            cancel(true);
        }
        return null;
    }

    private void insertTheEvent(VKEvent[] vkEvent) throws IOException {

        Log.v("Task", "Вошли в метод");

        String calendarId = "primary";

        Log.v("Task", mService.events().toString());

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();


        Calendar service = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Event Bridge").build();

// Iterate over the events in the specified calendar

        Log.v("Task", credential.getSelectedAccountName());
        String pageToken = null;
        ArrayList<String> checkList = new ArrayList<String>();

        //DateTime cur = new DateTime(System.currentTimeMillis());
       //EventDateTime currentTime = new EventDateTime().setDateTime(cur);
        //Log.v("time",currentTime + "");

        do {
            Events events = service.events().list(calendarId).setPageToken(pageToken).execute();
            List<Event> items = events.getItems();
            for (Event event1 : items) {
                Log.v("MyEvent", event1.getSummary());

                checkList.add(event1.getSummary());
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);

        for (VKEvent item : vkEvent) {
            Event event = EventsExporter.getGoogleEvent(item);
            if (!checkList.contains(event.getSummary())) {
                mService.events().insert(calendarId, event).execute();
                Log.v("Task", "Event created: " + event.getSummary());
                count++;
            } else
                Log.v("Task", event.getSummary() + ": Not created");

        }

    }



    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (activityName.equals("EventInfoActivity")){
            if (count == 0) Toast.makeText(context, "Данное мероприятие уже присутствует в календаре", Toast.LENGTH_SHORT).show();
            else Toast.makeText(context, "Мероприятие успешно добавлено ", Toast.LENGTH_SHORT).show();
        }
        if (activityName.equals("MainActivity")){
            if (count == 0) Toast.makeText(context, "Все мероприятия уже присутствуют в календаре", Toast.LENGTH_SHORT).show();
            else Toast.makeText(context, "Мероприятий добавлено: " + count, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCancelled() {
        Toast.makeText(context,"The following error occurred:\n"
                        + mLastError.getMessage(),Toast.LENGTH_SHORT).show();
            }

}
