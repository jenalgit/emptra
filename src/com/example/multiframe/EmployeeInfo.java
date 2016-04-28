package com.example.multiframe;

import JsonObject.*;
import Utils.GenericUtils;
import Utils.JsonProcessing;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;

import static Utils.GenericUtils.LogMe;

public class EmployeeInfo extends Activity {
	ListView list;
    private static LayoutInflater inflater = null;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.empinfo_layout);
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        String empid = getIntent().getStringExtra("empid");

        try {
            EmployeeInfoJson ei = getEmpInfo(empid);
            setPersonalSection(ei.personal);
            setEmploymentSection(ei.employment);
            setContactsSection(ei.contacts);
            setTrainingsSection(ei.trainings[0]);
        }
        catch(Exception e)
        {
            Log.e("EmployeeInfo", "JSON Processing issue " + e);
        }
    }

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		LogMe("EmpInfo:onCreateview", "=> Start ");
		return inflater.inflate(R.layout.empinfo_layout, container, false);
	}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  Read the file from assets/empinfo/<empid>.json
    //  Read each section to sub objects

    EmployeeInfoJson getEmpInfo(String empid) {
        String jsonFileName = empid + ".json";
        JsonProcessing js;

        try {
            js = new JsonProcessing(this);
            String jsonData = js.readFileFromPath(this, jsonFileName, "empinfo");
            LogMe("EmployeeInfo", "Employee Info JSON : " + jsonData);

            EmployeeInfoJson ei = js.convertJsonToObj(jsonData, EmployeeInfoJson.class);
            //Employee e = ei.personal;
            if(ei == null)
            {
                LogMe("EmployeeInfo", "Employee Info JSON is empty: ");
            }
            //LogMe("EmployeeInfo", "Personal Info : " + e.name + " - " + e.firstname);
            return ei;
        } catch (FileNotFoundException e) {
            Log.e("EmployeeInfo", "FILE NOT FOUND " + e);
        } catch (IOException e) {
            Log.e("EmployeeInfo", "FILE : IOException : " + e);
        }
        catch (Exception e) {
        Log.e("EmployeeInfo", "JSON Processing : Failure - " + e);
        }
        return null;
    }

    void setPersonalSection(Employee e) {
        LogMe("EmpInfo:setPersonalSection ", "=> Start " + e.name);
        TextView empinfo_name = (TextView) findViewById(R.id.empinfo_emp_name);
        TextView empinfo_dob = (TextView) findViewById(R.id.empinfo_dob);
        TextView empinfo_sex = (TextView) findViewById(R.id.empinfo_sex);

        empinfo_name.setText(e.firstname +", "+e.surname);
        empinfo_dob.setText("DOB: " + GenericUtils.DateToString(e.dob, "dd-MM-yyyy"));
        empinfo_sex.setText("Sex: " + e.sex);
    }

    void setEmploymentSection(Employment e) {
        LogMe("EmpInfo:setEmploymentSection ", "=> Start " + e.empid);
        TextView empinfo_id = (TextView) findViewById(R.id.empinfo_empid);
        TextView empinfo_desig = (TextView) findViewById(R.id.empinfo_employment_role);
        TextView empinfo_startdt = (TextView) findViewById(R.id.empinfo_employment_startdate);
        TextView empinfo_status = (TextView) findViewById(R.id.empinfo_employment_status);
        TextView empinfo_supervisor = (TextView) findViewById(R.id.empinfo_employment_supervisor);
        TextView empinfo_loc = (TextView) findViewById(R.id.empinfo_employment_location);

        empinfo_id.setText(e.empid);
        empinfo_desig.setText(e.designation);
        empinfo_startdt.setText("Start Date: "+GenericUtils.DateToString(e.start_date, "dd-MM-yyyy"));
        empinfo_status.setText("Status: " + e.status);
        empinfo_supervisor.setText("Supervisor: " + e.supervisor);
        empinfo_loc.setText("Work Loc: " + e.location);
    }

    void setContactsSection(Contacts c) {
        LogMe("EmpInfo:setContactsSection ", "=> Start " + c.mobile);
        TextView empinfo_mobile = (TextView) findViewById(R.id.empinfo_contact_mobile);
        TextView empinfo_email = (TextView) findViewById(R.id.empinfo_contact_email);
        TextView empinfo_address_p = (TextView) findViewById(R.id.empinfo_contact_permenant);
        TextView empinfo_address_c = (TextView) findViewById(R.id.empinfo_contact_current);

        empinfo_mobile.setText(c.mobile);
        empinfo_email.setText(c.email);
        empinfo_address_c.setText("Current Address \n" + c.getFormattedAddress(c.current_address));
        empinfo_address_p.setText("Permanent Address \n" + c.getFormattedAddress(c.permanent_address));

    }

    void setTrainingsSection(Trainings t) {
        LogMe("EmpInfo:setTrainingsSection ", "=> Start " + t.getTitle());
        TextView empinfo_name = (TextView) findViewById(R.id.empinfo_training_name);
        TextView empinfo_desc = (TextView) findViewById(R.id.empinfo_training_desc);
        TextView empinfo_status = (TextView) findViewById(R.id.empinfo_training_status);
        TextView empinfo_start_date = (TextView) findViewById(R.id.empinfo_training_startdate);

        empinfo_name.setText(t.getTitle());
        empinfo_desc.setText(t.getTitle());
        empinfo_status.setText(t.getStatus());
        empinfo_start_date.setText(GenericUtils.DateToString(t.getStartDate(), "dd-MM-yyyy"));
    }

}
