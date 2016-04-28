package com.example.multiframe;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import JsonObject.DailyReport;
import JsonObject.EmployeeAttendance;
import static Utils.GenericUtils.*;
public class SearchReportAdapter extends BaseAdapter {

    private Activity activity;
    private DailyReport dataDR;
    private static LayoutInflater inflater = null;

    View vi;
    TextView name;
    TextView empid;
    TextView intime;
    TextView outtime;
    TextView totalhrs;
    LinearLayout lytW;
    ImageView status_image;

    public SearchReportAdapter(Activity a, DailyReport dr) {
        activity = a;
        dataDR = dr;
        showLog = false;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(R.layout.dailyreport_list, null);
        vi.setBackgroundColor(Color.WHITE);
        //  empid = (TextView) vi.findViewById(R.id.lemp_id);          // employee id
        name = (TextView) vi.findViewById(R.id.lname);                 // name
        intime = (TextView) vi.findViewById(R.id.intime);               // designation
        outtime = (TextView) vi.findViewById(R.id.outtime);            // department
        outtime = (TextView) vi.findViewById(R.id.location);           // location
        totalhrs = (TextView) vi.findViewById(R.id.totalhours);        // total hours worked
        status_image = (ImageView) vi.findViewById(R.id.status_image); // status image
    }

    public int getCount() {
        return dataDR.employees.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LogMe("SearchReportAdpt:getView", "=> Start");

        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.dailyreport_list, null);
        }
        getElements(vi);
        DailyReport dr = new DailyReport();
        EmployeeAttendance ea = dataDR.employees[position];

        name.setText(ea.firstname);

        DateFormat format;
        if (ea.intime.length() == 16) {
            format = new SimpleDateFormat("dd-MM-yyyy h:mm", Locale.ENGLISH);
        } else
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        Date inDate1 = null, outDate1 = null;
        Calendar inDate =  Calendar.getInstance(), outDate =  Calendar.getInstance();;
        try {
            inDate.setTime(format.parse(ea.intime));
            if (ea.outtime == null || ea.outtime.isEmpty())
                ea.outtime = "1900-01-01 00:00:00";
            outDate.setTime(format.parse(ea.outtime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = outDate.getTimeInMillis() - inDate.getTimeInMillis();
        //outDate1.getTime() - inDate1.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;

        name.setText(dateFormat.format(inDate.getTime()));
        intime.setText(String.format("%02d", inDate.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", inDate.get(Calendar.MINUTE)));
        outtime.setText(String.format("%02d", outDate.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", outDate.get(Calendar.MINUTE)));

        if (outDate.get(Calendar.HOUR_OF_DAY) != 0) {
            totalhrs.setText(String.format("%02d", diffHours) + ":" + String.format("%02d", diffMinutes));
            if(diffHours == 0.0 && diffMinutes <= 30.0)
                status_image.setImageResource(R.drawable.status_abscent);
            else
                status_image.setImageResource(R.drawable.status_done);
        } else {
            totalhrs.setText(" - ");
            status_image.setImageResource(R.drawable.status_present);
        }

        return vi;
    }

    void getElements(View vi) {
        name = (TextView) vi.findViewById(R.id.lname); // name
        intime = (TextView) vi.findViewById(R.id.intime); // designation
        outtime = (TextView) vi.findViewById(R.id.outtime); // department
        // empid = (TextView) vi.findViewById(R.id.lemp_id); // employee id
        totalhrs = (TextView) vi.findViewById(R.id.totalhours); // total hours
        status_image = (ImageView) vi.findViewById(R.id.status_image); // employee
    }
}
