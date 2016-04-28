package com.example.multiframe;
import android.content.Context;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.Calendar;
import java.util.List;
import DatabaseObj.EmployeeAttendanceTab;
import JsonObject.DailyReport;
import JsonObject.EmployeeAttendance;
import android.widget.TextView;

//import java.util.Date;
//import DatabaseObj.DesignationTab;
//import GenericUtils.JsonProcessing;
//import org.w3c.dom.Text;

import static Utils.GenericUtils.*;

public class ShowAttendance extends Fragment {
    ListView list;
    DailyReportAdapter adapter;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogMe("SA:onCreateView", " => Start");
        rootView = inflater.inflate(R.layout.dailyreport_layout, container, false);
        TextView location = (TextView) rootView.findViewById(R.id.location);
        TextView dateField = (TextView) rootView.findViewById(R.id.date);

        Calendar curDateTime = Calendar.getInstance();
        dateField.setText("Date : " +dateFormat.format(curDateTime.getTime()));

        location.setText("Location : "+getSetting(getActivity(), "prefLocation"));

        setAdapterView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void setAdapterView(){
        DailyReport dr;
        //String jsonFileName = "dailyreport_today.json";
        //JsonProcessing js = new JsonProcessing(getActivity());
        try {
            //dr = js.convertJsonToObj(jsonFileName, DailyReport.class, false);
            //Log.i("ShowAttendance", "JSON Processing : Total emp : " + dr.employees.length);
            dr = getDailyReportFromDB(getActivity());
            if(dr == null) throw new Exception();

            if(getActivity() != null)
            LogMe("SA:onCreateView", "DB Data Processing : Total emp : " + dr.employees.length);
            else
                LogMe("SA:onCreateView", "NULL CONTEXT ");

            list = (ListView) rootView.findViewById(R.id.list);
            adapter = new DailyReportAdapter(getActivity(), dr);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                }
            });
        } catch (Exception e) {
            LogMe("SA:onCreateView", "FILE/DATA NOT FOUND " + e.getMessage(), 0);
            //e.printStackTrace();
        }
    }
    private DailyReport getDailyReportFromDB(Context ctx){
        LogMe("SA:getDailyReportFromDB", " => Start");
        DailyReport dr = new DailyReport();

        if(ctx == null) {
            LogMe("SA:getDailyReportFromDB", " NULL Context");
        }
        dr.today = getCurrentDate();
        dr.location = getSetting(ctx, "prefLocation");
        dr.user = getSetting(ctx, "prefUsername");

        List<EmployeeAttendanceTab> empRow = EmployeeAttendanceTab.queryAttendanceSortedByDate(getCurrentDate()  +" 00:00:00" , getCurrentDate() +" 23:59:59");
        if(empRow.size() > 0) {
            dr.employees = new EmployeeAttendance[empRow.size()];

            for(int i = 0; i< empRow.size(); i++) {
                EmployeeAttendanceTab ea = empRow.get(i);

                String output = ea.getFirstname() +","+ ea.getIntime()+","+ea.getOuttime();
                LogMe("SA:getDailyReportFromDB", " => " + output);
                dr.employees[i] = new EmployeeAttendance();
                dr.employees[i].empid = Long.toString(ea.getEmpId());
                dr.employees[i].firstname = ea.getFirstname();
                dr.employees[i].intime = ea.getIntime();
                dr.employees[i].outtime = ea.getOuttime();
            }
            return dr;
        }
        else{
            LogMe("SA:getDailyReportFromDB", " No data found for today", 2);
            return null;
        }
    }

    public static DailyReport getDailyAttendance(Context ctx)
    {
        ShowAttendance sa = new ShowAttendance();
        return sa.getDailyReportFromDB(ctx);
    }
    public interface OnRefreshListener {
        void onRefresh();
    }
}
