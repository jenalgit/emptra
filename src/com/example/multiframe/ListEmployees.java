package com.example.multiframe;

/*
  Reads the weekly_report.json to get weekly report and
  monthly_report.json for monthly report.
  The structure of the json are the same.
  The weekly json processing takes into account the attendance
  records within the last 7 days from today (inclusive).


  mothlyreport [                   --- array : one months record
     employee records              --- employee record
        {
          attendancemonthly[       --- array : attendance of the month
          ]
        }
  ]
 */
import JsonObject.*;
import Utils.GenericUtils;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Utils.JsonProcessing;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static Utils.GenericUtils.*;

public class ListEmployees extends Fragment {
    ListView list;
    Date creationDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLog = true;
        LogMe("ListEmp:onCreateview", "=> Start ");
        LazyAdapter adapter;
        View rootView = inflater.inflate(R.layout.employees_layout, container, false);
        TextView dateVal = (TextView) rootView.findViewById(R.id.date_val);

        FragmentActivity activity = getActivity();
        Date curDateTime1 = new Date();
        Calendar curDateTime = Calendar.getInstance();
        Date weekAgoDate = daysAgo(curDateTime.getTime(), 7);
        dateVal.setText("Date : "  +dateFormat.format(weekAgoDate) + " to " +dateFormat.format(curDateTime.getTime()));
        ArrayList<Employee> arrEmp1 = getWeeklyReport(curDateTime);

        list = (ListView) rootView.findViewById(R.id.list);

        // Getting adapter by passing json data ArrayList
        adapter = new LazyAdapter(activity, arrEmp1, false);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intentEmpinfo = new Intent(getActivity(), EmployeeInfo.class);
                intentEmpinfo.putExtra("empid", ""+ arrEmp1.get(position).empid);
                startActivity(intentEmpinfo);
            }
        });
        LogMe("ListEmp:onCreateview", "<= End");
        return rootView;
    }

    private boolean isViewShown = false;

    //@Override
    public void setUserVisibleHint1(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        FragmentActivity activity = getActivity();
        showLog = true;

        if (getView() != null) {
            isViewShown = true;
            GenericUtils.LogMe("MyFragment", "Weekly report visible.");

        } else {
            isViewShown = false;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        FragmentActivity activity = getActivity();
        showLog = true;
        GenericUtils.LogMe("MyFragment", "I'm on the EMP Listing tab!!!!!!!. " + this.isVisible() +" / " + isVisibleToUser);
        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                GenericUtils.LogMe("MyFragment", "Not visible anymore.");
                // Show Alert of the old data being shown
            }
            else {
                GenericUtils.LogMe("MyFragment", "Weekly report visible.");
                if(creationDate !=null && creationDate.before(GenericUtils.getDate())){
                    showAlert(activity, "Weekly Attendance", "Data is older than current data. Click ok to sync now" );
                }
                else{
                    GenericUtils.showMessage(activity, "Generated on " + dateFormat.format(creationDate));
                }
            }
        }
    }

    public ArrayList<Employee> getMonthlyReport() {
        LogMe("ListEmp:getMonthlyReport", "=> Start ");
        WeeklyReportObj weeklyRep;
        String jsonFileName = "monthly_report.json";
        JsonProcessing js;
        FragmentActivity activity = getActivity();
        try {
            js = new JsonProcessing(activity);
            String jsonData = js.readFile(activity, jsonFileName, false);
            weeklyRep = js.convertJsonToObj(jsonData, WeeklyReportObj.class);

            ArrayList<Employee> allEmployees = new ArrayList<Employee>();
            int totalEmployees = weeklyRep.monthlyreports.length;

            for (int i = 0; i < totalEmployees; i++) {
                Employee emp = new Employee();
                MonthlyReport empAttendance = weeklyRep.monthlyreports[i];
                emp.empid = empAttendance.empid;
                emp.name = empAttendance.name;
                emp.department = empAttendance.department;
                emp.designation = empAttendance.designation;
                int totalMonths = empAttendance.attendancemonthly.length;
                int currentMonth = 0;
                int currentWeek = 1;
                // Month loop
                for (int month = 0; month < totalMonths; month++) {
                    emp.weekatnd = new Week();
                    emp.weekatnd.weekno = currentWeek;
                    ArrayList<Attendance> days = InitializeAttendance();

                    int totalDays = empAttendance.attendancemonthly[currentMonth].days.length;
                    LogMe("ListEmp:getMonthlyReport", "=> Stage 2");
                    for (int d = 0; d < totalDays; d++) {
                        AttendanceObj atndJsonObj = empAttendance.attendancemonthly[currentMonth].days[d];
                        Attendance atnd = days.get(atndJsonObj.day);
                        {
                            atnd.day = "Mon"; //atndJsonObj.day;
                            atnd.datetime = atndJsonObj.datetime;
                            atnd.hours = Float.parseFloat(atndJsonObj.hours);
                            atnd.intime = Timestamp.valueOf(GenericUtils.DateToString(atndJsonObj.intime)+".000");
                            atnd.outtime = Timestamp.valueOf(GenericUtils.DateToString(atndJsonObj.outtime)+".000");
                            atnd.bHoliday = false;
                            atnd.notes = "";
                        }
                    }
                    emp.weekatnd.days = days;
                }
                allEmployees.add(emp);
            }

            LogMe("ListEmp:getMonthlyReport", "<= End");
            return allEmployees;

        } catch (FileNotFoundException e) {
            LogMe("ListActivity", "FILE NOT FOUND " + e, 3);
        } catch (IOException e) {
            LogMe("ListActivity", "FILE : IOException : " + e, 3);
            e.printStackTrace();
        }
        LogMe("ListEmp:getMonthlyReport", "<= End");
        return null;
    }

    /*
    Get time data for each employee, for the period
    (curDate - 7) to curDate
     */
    public ArrayList<Employee> getWeeklyReport(Calendar toDate) {
        LogMe("ListEmp:getWeeklyReport", "=> Start ");
        WeeklyReportObj weeklyRep;
        String jsonFileName = "weekly_report.json";
        JsonProcessing js;
        FragmentActivity activity = getActivity();

        Timestamp fromDateTime1 = Timestamp.valueOf(GenericUtils.DateToString(daysAgo(toDate.getTime(), 7)) + ".000");

        Date daysAgo = daysAgo(toDate.getTime(), 7);
        Calendar fromDateTime = Calendar.getInstance();
        fromDateTime.setTime(daysAgo);
        String toDay=dayFormat.format(toDate.getTime());
        int toMonthVal = toDate.get(Calendar.MONTH);
        int toDateVal = toDate.get(Calendar.DATE);
        int toDayVal = toDate.get(Calendar.DAY_OF_WEEK);
        String fromDay=dayFormat.format(fromDateTime.getTime());

       // LogMe("LE:WeeklyReport", "Between : " + fromDateTime.toString() +" "+fromDay+ " - " + toDate.toString() +", Day:"+toDay);
        try {
            js = new JsonProcessing(activity);
            String jsonData = js.readFile(activity, jsonFileName, false);
            weeklyRep = js.convertJsonToObj(jsonData, WeeklyReportObj.class);

            ArrayList<Employee> allEmployees = new ArrayList<Employee>();
            creationDate = GenericUtils.DateStringToDate(weeklyRep.creation_date);

            int totalEmployees = weeklyRep.monthlyreports.length;

            for (int i = 0; i < totalEmployees; i++) {
                Employee emp = new Employee();
                MonthlyReport empAttendance = weeklyRep.monthlyreports[i];
                emp.empid = empAttendance.empid;
                emp.name = empAttendance.name;
                emp.department = empAttendance.department;
                emp.designation = empAttendance.designation;
                int totalMonths = empAttendance.attendancemonthly.length;
                int currentMonth = 0;
                int currentWeek = 0;

                //LogMe(emp.name, "Processing between " +fromDateTime.getTime() + " and " + toDate.getTime());
                // Month loop
                int day = 4; //1-7
                for (int month = 0; month <= currentMonth; month++) {
                    emp.weekatnd = new Week();
                    emp.weekatnd.weekno = currentWeek;
                    ArrayList<Attendance> days = InitializeWeekAttendance(toDate);

                    Calendar fromDate = Calendar.getInstance();
                    fromDate.setTime(daysAgo(toDate.getTime(), 8));

                    int totalDays = empAttendance.attendancemonthly[currentMonth].days.length;
                    for (int d = 0; d < totalDays; d++) {
                        AttendanceObj atndJsonObj = empAttendance.attendancemonthly[currentMonth].days[d];
                        if( atndJsonObj.datetime.after(fromDate.getTime()) && atndJsonObj.datetime.before(toDate.getTime()) ) {
                            int index = 6 - (int)(toDate.getTimeInMillis() - atndJsonObj.datetime.getTime() )/ (1000 * 60 * 60 * 24) ;

                            Attendance atnd = days.get(index);
                            atnd.datetime = atndJsonObj.datetime;
                            atnd.hours = Float.parseFloat(atndJsonObj.hours);
                            atnd.bHoliday = false;
                            atnd.notes = "" ;
                        }
                    }
                    emp.weekatnd.days = days;
                }

                allEmployees.add(emp);
            }
            return allEmployees;

        } catch (FileNotFoundException e) {
            LogMe("ListActivity", "FILE NOT FOUND " + e, 3);
        } catch (IOException e) {
            LogMe("ListActivity", "FILE : IOException : " + e, 3);
            e.printStackTrace();
        }
        return null;
    }

    ArrayList<Attendance> InitializeAttendance(){
        ArrayList<Attendance> attendance = new ArrayList<>();
        Timestamp nullDate = java.sql.Timestamp.valueOf("1900-01-01 00:00:00.000");
        for(int d = 0; d < 31; d++){
            Attendance atnd = new Attendance();
            atnd.day = "Unk";
            atnd.datetime = nullDate;
            atnd.hours = Float.parseFloat("0.0");
            atnd.intime = nullDate;
            atnd.outtime = nullDate;
            atnd.bHoliday = false;
            atnd.notes = "Data not available";
            attendance.add(atnd);
        }
        return attendance;
    }

    ArrayList<Attendance> InitializeWeekAttendance(){
        ArrayList<Attendance> attendance = new ArrayList<>();
        Timestamp nullDate = java.sql.Timestamp.valueOf("1900-01-01 00:00:00.000");
        for(int d = 0; d < 7; d++){
            Attendance atnd = new Attendance();
            atnd.day = "";
            atnd.datetime = nullDate;
            atnd.hours = Float.parseFloat("0.0");
            atnd.intime = nullDate;
            atnd.outtime = nullDate;
            atnd.bHoliday = false;
            atnd.notes = "Unk";
            attendance.add(atnd);
        }
        return attendance;
    }

    ArrayList<Attendance> InitializeWeekAttendance(Calendar date){
        int numOfDays=7;
        ArrayList<Attendance> attendance = new ArrayList<>();
        Timestamp nullDate = java.sql.Timestamp.valueOf("1900-01-01 00:00:00.000");
        for(int d = numOfDays; d > 0; d--){
            Attendance atnd = new Attendance();
            atnd.day = dayFormat.format(daysAgo(date.getTime(), d)).substring(0,3);
            atnd.datetime = nullDate;
            atnd.hours = Float.parseFloat("0.0");
            atnd.intime = nullDate;
            atnd.outtime = nullDate;
            atnd.bHoliday = false;
            atnd.notes = "Unk";
            attendance.add(atnd);
        }
        return attendance;
    }
}

/*
** Notes ---------------------
   Gson gson = new Gson();
   BufferedReader reader = new BufferedReader(new InputStreamReader(this
                   .getAssets().open("json_data/"+ jsonFileName), "UTF-8"));
   JsonEmp myTypes = gson.fromJson(reader, JsonEmp.class);
   arrEmp = myTypes.employees;
   Log.i("JSON Processing", "JSON : "+ js.convertObjToJson(emp));
 */
