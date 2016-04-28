package com.example.multiframe;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.List;
import DatabaseObj.EmployeeAttendanceTab;
import DatabaseObj.EmployeeTab;
import JsonObject.DailyReport;
import JsonObject.EmployeeAttendance;
import static Utils.GenericUtils.*;

public class SearchInformation extends Fragment {
	ListView list;
    Button srchBtn;
    EditText empid;
    EditText name;
    SearchReportAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		LogMe("SrchInfo:onCreateview", "=> Start ");

		View rootView = inflater.inflate(R.layout.search_layout, container, false);

		Button srchBtn = (Button) rootView.findViewById(R.id.srch_button);
        final EditText empId = (EditText) rootView.findViewById(R.id.srch_empid);
        final EditText name = (EditText) rootView.findViewById(R.id.srch_empname);
        final TextView search_result_list = (TextView) rootView.findViewById(R.id.search_result_list);

    //    final TextView search_result_title = (TextView) rootView.findViewById(R.id.search_result_title);
        final TextView search_atnd_title = (TextView) rootView.findViewById(R.id.search_atnd_title);

        empId.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        empId.setText("");
                    }
                }
        );
        name.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name.setText("");
                    }
                }
        );

        srchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogMe("Srch:onClick", "Start");
                        String emp_id = empId.getText().toString();
                        String emp_name = name.getText().toString();
                        String empInfo = "";
                        DailyReport dr = new DailyReport();
                        // Check if no view has focus:
                        Activity a = getActivity();
                        View view = a.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)a.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        List<EmployeeTab> emp = EmployeeTab.queryEmployee(emp_id);
                        if(emp!=null && emp.size() !=0){
                            int i = 0;
                            EmployeeTab e = emp.get(i);
                            empInfo = "Emp.Id   : " + emp_id + "\n" +
                                      "Emp Name : " + e.firstname + " " + e.surname+"\n" +
                                      "Status   : " + e.status;

                           // search_result_title.setVisibility(View.VISIBLE);
                        }
                        else
                            empInfo = "Invalid employee id or name";

                        search_result_list.setText(empInfo);
                        search_result_list.setVisibility(View.VISIBLE);
                        String msg = "";
                        List<EmployeeAttendanceTab> ea = EmployeeAttendanceTab.queryAttendanceForEmp(emp_id, "0000-00-00", MicroTimestamp());
                        if (ea != null) {
                            int i = 0;
                            search_atnd_title.setVisibility(View.VISIBLE);
                            dr.employees = new EmployeeAttendance[ea.size()];
                            while(i< ea.size()){
                                EmployeeAttendanceTab e = ea.get(i);
                                dr.employees[i] = new EmployeeAttendance();
                                dr.employees[i].firstname = e.getFirstname();
                                dr.employees[i].intime = e.getIntime();
                                dr.employees[i].outtime = e.getOuttime();
                                dr.employees[i].empid = ""+emp_id;
                                msg = msg + emp_id + " " + e.getFirstname() + " " + e.getIntime() + "" + e.getOuttime()+"\n";
                                i++;
                            }
                        } else {
                            msg = "Attendance data not recorded for this employee";
                        }

                        list = (ListView) rootView.findViewById(R.id.search_atnd_list);
                        adapter = new SearchReportAdapter(getActivity(), dr);
                        list.setAdapter(adapter);
                    }
        });

		return rootView;
	}

}
