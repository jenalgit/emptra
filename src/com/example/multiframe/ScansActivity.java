package com.example.multiframe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import com.google.zxing.integration.android.IntentResult;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.os.Bundle;

import DatabaseObj.DepartmentsTab;
import DatabaseObj.DesignationTab;
import DatabaseObj.EmployeeAttendanceTab;
import JsonObject.EmployeeAttendance;
import Utils.JsonProcessing;
import Utils.*;
import static Utils.GenericUtils.*;
public class ScansActivity extends Fragment implements OnClickListener {
    View rootView;
    private TextView contentTxt;
    String qrCodeScanContent;

    View scanLayout;

    /*Show_emp layout */
    RelativeLayout scan_emp_layout;
    LinearLayout scan_thumb;
    private ImageView scan_photo;
    private TextView scan_empName;
    private TextView scan_empID;
    private TextView scan_department;
    private TextView scan_designation;
    private TextView scan_intime;
    private TextView scan_outtime;

    public static final int REQUEST_CODE = 0x0000c0de;
    private static final String BS_PACKAGE = "com.google.zxing.client.android";
    private static final String EXTRA_CODE = "com.example.androidtabswipeactivity.code";
    Button scanBtn;

    public static ScansActivity newInstance(String code) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CODE, code);

        ScansActivity fragment = new ScansActivity();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogMe("Scan:onCreateView", "=> Start");
        showLog = true;

        rootView = inflater.inflate(R.layout.scans_layout, container, false);
        contentTxt = (TextView) rootView.findViewById(R.id.scanResult);

        scan_emp_layout = (RelativeLayout) rootView.findViewById(R.id.show_emp);
        scan_thumb =  (LinearLayout) rootView.findViewById(R.id.thumbnail);
        scan_photo = (ImageView) rootView.findViewById(R.id.scan_photo);
        scan_empName = (TextView) rootView.findViewById(R.id.scan_emp_name);
        scan_empID = (TextView) rootView.findViewById(R.id.scan_empid);
        scan_department = (TextView) rootView.findViewById(R.id.scan_department);
        scan_designation = (TextView) rootView.findViewById(R.id.scan_designation);
        scan_intime = (TextView) rootView.findViewById(R.id.scan_in_time);
        scan_outtime = (TextView) rootView.findViewById(R.id.scan_out_time);
        setVisibility(false);

        scanBtn = (Button) rootView.findViewById(R.id.bt_scan);
        scanBtn.setOnClickListener(this);

        LogMe("Scan:onCreateView", "<= End");
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LogMe("Scan:onActivityCreated", "=> Start");
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    //This function is never called, the MainActivity->onActivityResult will be called.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // retrieve scan result
        LogMe("Scan:onActivityResult", "=> Start");

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);

        qrCodeScanContent = scanningResult.getContents();
        LogMe("Scan:onActivityResult", qrCodeScanContent, 1);

        processScanResult(qrCodeScanContent);
        LogMe("Scan:onActivityResult", "  <= End", 1);
    }

    public void onClick(View v) {
        // respond to clicks
        LogMe("Scan:onClick", "Start");
        if(GenericUtils.validateApplicationLicense(getActivity())) {

            try {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_FORMATS", "QR_CODE_MODE");
                intent.putExtra("SCAN_RESULT_ORIENTATION", 20);
                startActivityForResult(intent, IntentIntegrator.REQUEST_CODE);
            } catch (Exception e) {
                LogMe("BARCODE_ERROR", e.getMessage(), 3);
            }
        }
        else {
            showPlainAlert(getActivity(),
                    getResources().getString(R.string.license_expired_title),
                    getResources().getString(R.string.license_expired_msg));
        }
    }

    private void setVisibility(Boolean bVisibility)
    {
        int iVisible = bVisibility?View.VISIBLE:View.INVISIBLE;
        scan_emp_layout.setVisibility(iVisible);
        scan_thumb.setVisibility(iVisible);
        scan_photo.setVisibility(iVisible);
        scan_empName.setVisibility(iVisible);
        scan_empID.setVisibility(iVisible);
        scan_department.setVisibility(iVisible);
        scan_designation.setVisibility(iVisible);
        scan_intime.setVisibility(iVisible);
        scan_outtime.setVisibility(iVisible);
    }


    private int insertIntoEmpAtndTab(EmployeeAttendance ea) {
        EmployeeAttendanceTab eaDb = new EmployeeAttendanceTab();
        switch (eaDb.insertDataFromJson(ea)) {
            case ETConstants.DB_INSERT_SUCCESS:
                LogMe("ScansActivity", "insertIntoEmpAtndTab : "   + eaDb.getIntime() + ", " + eaDb.getOuttime());
                eaDb.save();
                return ETConstants.DB_INSERT_SUCCESS;
            case ETConstants.DB_UPDATE_SUCCESS:
                return ETConstants.DB_UPDATE_SUCCESS;
            case ETConstants.DB_RECORD_NOT_FOUND:
                return ETConstants.DB_RECORD_NOT_FOUND;
            default: return ETConstants.DB_INSERT_FAILED;
        }
    }

    private Boolean processScanResult(String scanContent){
        if (scanContent  != null) {
            FragmentActivity activity = getActivity();
            //String jsonData = scanContent.replace("\\\"", "\"");
            //LogMe("SCAN:processScanResult", "JSON : " + jsonData);

            EmployeeAttendance ea;
            JsonProcessing js = new JsonProcessing(activity);
            try {
                ea = js.convertJsonToObj(scanContent, EmployeeAttendance.class);
                if(ea != null) {
                    LogMe("SCAN:processScanResult", "Valid scanned QR Code");
                    contentTxt.setVisibility(View.INVISIBLE);
                    scan_empName.setText(ea.firstname);
                    scan_empID.setText(ea.empid);
                    DepartmentsTab depttab = DepartmentsTab.queryDepartmentId(Integer.parseInt(ea.department));
                    if(depttab != null)
                        scan_department.setText(depttab.deptname);
                    else
                        LogMe("SCAN:processScanResult", "Invalid department, record not found <NullError>");

                    DesignationTab desigtab = DesignationTab.queryDesignationId(Integer.parseInt(ea.designation));
                    if(desigtab != null)
                       scan_designation.setText(desigtab.designame);
                    else
                       LogMe("SCAN:processScanResult", "Invalid designation, record not found <NullError>");

                    ea.intime = ea.outtime = GenericUtils.MicroTimestamp();
                    scan_intime.setText("In Time : " + GenericUtils.getCurrentTime());
                    scan_outtime.setText("Out Time :  [Working ]");

                    try {
                        String imgFileName = readFileFromAppDir(activity, ea.empid + ".jpg");
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFileName);
                        if(bitmap != null) {
                            scan_photo.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        LogMe("SCAN:processScanResult", e.getLocalizedMessage());
                    }

                    switch(insertIntoEmpAtndTab(ea)){
                        case ETConstants.DB_INSERT_SUCCESS:
                            scan_outtime.setText("Out Time :  [Working ]");
                            setVisibility(true);
                            break;
                        case ETConstants.DB_UPDATE_SUCCESS:
                            scan_outtime.setText("Out Time :  " + GenericUtils.getCurrentTime());
                            setVisibility(true);
                            break;
                        default:
                            scan_emp_layout.setVisibility(View.INVISIBLE);
                            contentTxt.setVisibility(View.VISIBLE);
                            contentTxt.setTextColor(getResources().getColor(R.color.color_app_text_error));
                            contentTxt.setText("Employee data, Expired/Invalid");
                            setVisibility(false);
                    }
                }
                else{
                    scan_emp_layout.setVisibility(View.INVISIBLE);
                    contentTxt.setVisibility(View.VISIBLE);
                    contentTxt.setTextColor(getResources().getColor(R.color.color_app_text_error));
                    contentTxt.setText("Invalid QR Code scanned. Try Again");
                    setVisibility(false);
                    return false;
                }
            } catch (Exception e) {
                LogMe("SCAN:processScanResult", "Exception processing QR Code " + e.getMessage(), 3);
                e.printStackTrace();
                return false;
            }
            LogMe("SCAN:processScanResult", scanContent);
        } else {
            LogMe("SCAN:processScanResult", "Scan cancelled", 1);
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

}


/*
** -----------------  Notes  -----------------------
     {"firstname": "Swarna Shree","empid":"12346","department": "14","designation":"Peoplesoft Admin","Other": "" }
**
 */