package com.example.multiframe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import JsonObject.Attendance;
import JsonObject.Employee;
import JsonObject.Week;
import Utils.GenericUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import static Utils.GenericUtils.*;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private ArrayList<Employee> empData;
    private static LayoutInflater inflater = null;
    String[] weekDayName = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

    View vi;
    TextView name;
    TextView empid;
    TextView designation;
    TextView department;
    TextView totalhrs;
    TextView tvDay[];
    int iWeek[];
    LinearLayout lytW;
    ImageView thumb_image;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        showLog = false;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public LazyAdapter(Activity a, ArrayList<Employee> e, Boolean bFlag) {
        activity = a;
        empData = e;
        showLog = false;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(R.layout.employee_list, null);
        //vi.setBackgroundColor(Color.WHITE);
        name = (TextView) vi.findViewById(R.id.name); // name
        designation = (TextView) vi.findViewById(R.id.designation); // designation
        department = (TextView) vi.findViewById(R.id.department); // department
        empid = (TextView) vi.findViewById(R.id.emp_id);          // employee id
        totalhrs = (TextView) vi.findViewById(R.id.total_hours);  // total hours worked

        iWeek = new int[7];
        iWeek[0] = R.id.week_mon;
        iWeek[1] = R.id.week_tue;
        iWeek[2] = R.id.week_wed;
        iWeek[3] = R.id.week_thu;
        iWeek[4] = R.id.week_fri;
        iWeek[5] = R.id.week_sat;
        iWeek[6] = R.id.week_sun;

        tvDay = new TextView[7];
        tvDay[0] = (TextView) vi.findViewById(R.id.tv_week_mon);
        tvDay[1] = (TextView) vi.findViewById(R.id.tv_week_tue);
        tvDay[2] = (TextView) vi.findViewById(R.id.tv_week_wed);
        tvDay[3] = (TextView) vi.findViewById(R.id.tv_week_thu);
        tvDay[4] = (TextView) vi.findViewById(R.id.tv_week_fri);
        tvDay[5] = (TextView) vi.findViewById(R.id.tv_week_sat);
        tvDay[6] = (TextView) vi.findViewById(R.id.tv_week_sun);
        thumb_image = (ImageView) vi.findViewById(R.id.emp_image); // employee image
    }

    public int getCount() {
        return empData.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        //LogMe("LazyAdapter:getView", "=> Start ");
        if (vi == null) {
            //LogMe("LazyAdapter:getView", "null convertView", 2);
            vi = inflater.inflate(R.layout.employee_list, null);
        }

        getElements(vi);
        Employee emplist = new Employee();
        emplist = empData.get(position);

        // Setting all values in listview
        name.setText(emplist.name);
        empid.setText(emplist.empid);
        designation.setText(emplist.designation);
        department.setText(emplist.department);

        Week wk = emplist.weekatnd;
        float totalHrs = 0;
        int index = 0;
        for (int i = 0; i < 7; i++) {
            tvDay[i].setText(weekDayName[i] + "\n0.0");
            LinearLayout lytW1 = (LinearLayout) vi.findViewById(iWeek[i]);
            //LogMe("Week No", "Initializing week : "+ " ID : " +lytW1.getId());
            lytW1.setBackgroundColor(activity.getResources().getColor(R.color.color_week_abscent));
        }

        for (int j = 0; j < 7/*wk.days.size()*/; j++) {
            Attendance days = wk.days.get(j);
           // LogMe("LazyAdapter:WEEK", "" + emplist.name + " => Day: "+days.datetime+", " + days.day + " Hours: " + days.hours);

            totalHrs += days.hours;
            index = j;;
            //tvDay[index].setText(weekDayName[index] + "\n" + days.hours);
            tvDay[index].setText(days.day + "\n" + days.hours);

            lytW = null;
            lytW = (LinearLayout) vi.findViewById(iWeek[index]);
            if (j == 6)
                lytW.setBackgroundColor(activity.getResources().getColor(R.color.color_week_holiday));
            if (days.hours > 0.0)
                lytW.setBackgroundColor(activity.getResources().getColor(R.color.color_week_present));
        }

        totalhrs.setText(String.valueOf(totalHrs));

        try {

            String imageFileName = GenericUtils.readFileFromAppDir(activity, emplist.empid + ".jpg");
            File imageFile = new File(imageFileName);

            if(imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFileName);
                if (bitmap != null) {
                    thumb_image.setImageBitmap(bitmap);
                }
            }
            else
                thumb_image.setImageResource(R.drawable.emp_default);
        } catch (Exception e) {
            LogMe("LazyAdapter:WEEK", e.getLocalizedMessage());
        }

        return vi;
    }

    void getElements(View vi) {
        name = (TextView) vi.findViewById(R.id.name); // name
        designation = (TextView) vi.findViewById(R.id.designation); // designation
        department = (TextView) vi.findViewById(R.id.department); // department
        empid = (TextView) vi.findViewById(R.id.emp_id); // employee id
        totalhrs = (TextView) vi.findViewById(R.id.total_hours); // total hours
        // worked
        tvDay = new TextView[7];
        tvDay[0] = (TextView) vi.findViewById(R.id.tv_week_mon);
        tvDay[1] = (TextView) vi.findViewById(R.id.tv_week_tue);
        tvDay[2] = (TextView) vi.findViewById(R.id.tv_week_wed);
        tvDay[3] = (TextView) vi.findViewById(R.id.tv_week_thu);
        tvDay[4] = (TextView) vi.findViewById(R.id.tv_week_fri);
        tvDay[5] = (TextView) vi.findViewById(R.id.tv_week_sat);
        tvDay[6] = (TextView) vi.findViewById(R.id.tv_week_sun);
        thumb_image = (ImageView) vi.findViewById(R.id.emp_image); // employee
    }
}
