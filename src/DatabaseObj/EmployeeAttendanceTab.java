package DatabaseObj;

import JsonObject.DailyReport;
import android.content.Context;
import android.util.Log;
import com.example.multiframe.ShowAttendance;
import com.google.gson.Gson;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;
import JsonObject.EmployeeAttendance;
import Utils.*;
import com.orm.dsl.Table;

public class EmployeeAttendanceTab extends SugarRecord {
    EmployeeTab employee;
    String firstname;
    String surname;
    String sex;
    DesignationTab designation;
    DepartmentsTab department;
    String intime;
    String outtime;

    public EmployeeAttendanceTab() {
    }


    public EmployeeAttendanceTab(EmployeeTab emp,
                                 String firstname,
                                 String surname,
                                 DepartmentsTab department,
                                 DesignationTab designation,
                                 String intime,
                                 String outtime) {
        this.employee = emp;
        this.firstname = firstname;
        this.surname = surname;
        this.sex = "";
        this.designation = designation;
        this.department = department;
        this.intime = intime;
        this.outtime = outtime;
        Log.i("Employee", "Constructor" +   this.firstname);
    }

    public Long getEmpId(){return this.employee.getId();}
    public String getFirstname(){return this.firstname;}
    public String getSurname(){return this.surname;}
    public String getIntime(){return this.intime;}
    public String getOuttime(){return this.outtime;}

    /*
    ** ------------------------------------------------------------------------
    * Description :
    ** Insert data from the JSON file into the EmployeeAttendanceTab table.
    ** The JSON is the data scanned from the emp id card using the QR scanner.
    ** Check If record already exists, if not create a record with intime
    ** else update the record with the outtime.
    *
    * Return :
    ** DB_INSERT_SUCCESS : New record created
    ** DB_INSERT_SUCCESS : Updated the existing record
    ** DB_RECORD_NOT_FOUND : If employee record doesnt exist
    ** ------------------------------------------------------------------------
    */

    public int insertDataFromJson(EmployeeAttendance jsonEmpAtnd) {

        List<EmployeeTab> empRec = EmployeeTab.queryEmployee(jsonEmpAtnd.empid);
        if(empRec !=null && empRec.size() > 0 ) {
            this.employee = empRec.get(0);  //Foreign Key
            Log.i("EmployeeAttendanceTab", "[QUERY]" + " Employee id found : ");
        }
        else {
            Log.i("EmployeeAttendanceTab", "[QUERY]" + " Employee id not found");
            return ETConstants.DB_RECORD_NOT_FOUND;
        }

        //Verify if the department id (string) exists in the DepartmentsTab
        DepartmentsTab deptRec = DepartmentsTab.queryDepartment(Integer.parseInt(jsonEmpAtnd.department));
        if(deptRec != null) {
            this.department = deptRec;            //Foreign Key
            Log.i("EmployeeAttendanceTab", "[QUERY]" + " Department id found");
        }else {
            Log.i("EmployeeAttendanceTab", "[QUERY]" + " Department id not found");
            return ETConstants.DB_RECORD_NOT_FOUND;
        }

        //Verify if the designation id (string) exists in the DesignationTab
        DesignationTab dsgRec = DesignationTab.queryDesignation(Integer.parseInt(jsonEmpAtnd.designation));
        if(dsgRec != null) {
            this.designation = dsgRec;            //Foreign Key
            Log.i("EmployeeAttendanceTab", "[QUERY]" + " Designation id found : " + jsonEmpAtnd.designation);
        }else {
            Log.i("EmployeeAttendanceTab", "[QUERY]" + " Designation id not found : " + jsonEmpAtnd.designation);
            return ETConstants.DB_RECORD_NOT_FOUND;
        }

        {
            this.firstname = jsonEmpAtnd.firstname;
            this.surname = jsonEmpAtnd.surname;
            this.sex = jsonEmpAtnd.sex;
            this.intime = jsonEmpAtnd.intime;
            int empid = Integer.parseInt(jsonEmpAtnd.empid);
            //IF record already exists, then out is set else dont set
            EmployeeAttendanceTab ea = findAttendanceRecord(empid, GenericUtils.getCurrentDate()  +" 00:00:00" , GenericUtils.getCurrentDate() +" 23:59:59");
            if (ea != null) {
                Log.i("EmployeeAttendanceTab","Record found for [Update] "+jsonEmpAtnd.empid +" " + this.firstname +", "+ jsonEmpAtnd.outtime);
                this.outtime = jsonEmpAtnd.outtime;
                ea.outtime = jsonEmpAtnd.outtime;
                ea.save();
                return ETConstants.DB_UPDATE_SUCCESS;
            } else {
                this.outtime = "";
            }
        }

        return ETConstants.DB_INSERT_SUCCESS;
    }

    /*
    ** ------------------------------------------------------------------------
    * Description :
    ** Find the employee attendance for a date range, inclusive of the dates provided
    * Returns :
    ** Object of EmployeeAttendanceTab
    ** ------------------------------------------------------------------------
    */

    public  EmployeeAttendanceTab findAttendanceRecord(int empId, String startDate, String endDate) {
        boolean bFound = false;
        List<EmployeeAttendanceTab> ea = queryEmployeeAttendanceDate(""+empId, startDate, endDate);
        if (ea != null) {
            int totalRecords = ea.size();
            switch (totalRecords) {
                case 0:
                    Log.i("AttendanceTab", "No records found for emp =>" + empId + "during the period " + startDate + "-" + endDate);
                    return null;
                case 1:
                    Log.i("AttendanceTab", "Employee has signed in for today [" +totalRecords+"], => " + empId + "during the period " + startDate + "-" + endDate);
                    return ea.get(ea.size()-1);
                case 2:
                    Log.i("AttendanceTab", "Employee has signed out for today [" + totalRecords + "], => " + empId + "during the period " + startDate + "-" + endDate);
                    return ea.get(ea.size()-1);
                default:
                    Log.i("AttendanceTab", "Employee has multiple entries for today [" + totalRecords + "], => " + empId + "during the period " + startDate + "-" + endDate);
                    return ea.get(ea.size()-1);
            }
        }
        else
           return null;
    }

    /*
    ** ------------------------------------------------------------------------
    * Description :
    ** Retrieve all the attendance records for an employee in the date range specified
    ** and the outtime is NULL/BLANK
    **
    **  Query : select * from EmployeeAttendanceTab
    **          where empid = ?
    **          and   intime >= startDate + "00:00:00"
    **          and   intime <= endDate + "23:59:59"
    **          and   outtime = ''
    * Input :
    ** Empid : Unique employee id
    ** StartDate : Start date in the format   yyyy-MM-dd hh:mi:ss
    ** EndDate : End date in the format   yyyy-MM-dd hh:mi:ss
    * Returns :
    ** List of EmployeeAttendanceTab object
    ** ------------------------------------------------------------------------
    */

    public static List<EmployeeAttendanceTab> queryEmployeeAttendanceDate(String iEmpid, String sStartDate, String sEndDate) {
        List<EmployeeTab> etList = EmployeeTab.queryEmployee(iEmpid);
        int iEmp = 0;
        List<EmployeeAttendanceTab> empAtndRow= new ArrayList<>();
        Log.i("[EAT: QUERY]", "Total records for " +iEmpid +" is " + etList.size());
        while(iEmp < etList.size()) {
            EmployeeTab et = etList.get(iEmp);
            String whereCondition = "employee = ? and intime >= ? and intime <= ? and outtime = ''";
            Log.i("EA:Find", "empid: " + Integer.parseInt(iEmpid) + " intime between " + sStartDate + " and " + sEndDate);
            //empAtndRow = EmployeeAttendanceTab.find(EmployeeAttendanceTab.class, whereCondition, iEmpid, sStartDate, sEndDate);

            String query = "SELECT eat.* from Employee_Attendance_Tab eat, employee_tab et where eat.employee = et.id and et.id = " +iEmpid + " and intime >= '" + sStartDate + "' and intime <= '"+sEndDate + "' and outtime = ''";
            empAtndRow = EmployeeAttendanceTab.findWithQuery(EmployeeAttendanceTab.class, query);
            iEmp++;
        }
        return empAtndRow;
    }

    /*
    ** ------------------------------------------------------------------------
    * Description :
    ** Same as above except that outtime is NULL/BLANK is not checked for
    **
    **  Query : select * from EmployeeAttendanceTab
    **          where empid = ?
    **          and   intime >= startDate + "00:00:00"
    **          and   intime <= endDate + "23:59:59"

    ** ------------------------------------------------------------------------
    */

    public static List<EmployeeAttendanceTab> queryAttendanceForEmp(String iEmpid, String sStartDate, String sEndDate) {
        List<EmployeeTab> etList = EmployeeTab.queryEmployee(iEmpid);
        int iEmp = 0;
        List<EmployeeAttendanceTab> empAtndRow= new ArrayList<>();
        Log.i("[EAT: QUERY.E]", "Total records for " +iEmpid +" is " + etList.size());
        while(iEmp < etList.size()) {
            EmployeeTab et = etList.get(iEmp);
            Log.i("EA:Find", "empid: " + Long.parseLong(iEmpid) + " intime between " + sStartDate + " and " + sEndDate);
            String query = "SELECT eat.* from Employee_Attendance_Tab eat, employee_tab et where eat.employee = et.id and et.id = " +iEmpid + " and intime >= '" + sStartDate + "' and intime <= '"+sEndDate + "'";
            empAtndRow = EmployeeAttendanceTab.findWithQuery(EmployeeAttendanceTab.class, query);
            iEmp++;
        }
        return empAtndRow;
    }


    /*
    ** ------------------------------------------------------------------------
    * Description :
    ** Retrieve all the attendance records in the date range specified
    **
    **  Query : select * from EmployeeAttendanceTab
    **          where intime >= startDate + "00:00:00"
    **          and   intime <= endDate + "23:59:59"
    * Input :
    ** StartDate : Start date in the format   yyyy-MM-dd
    ** EndDate : End date in the format   yyyy-MM-dd
    * Returns :
    ** List of EmployeeAttendanceTab object
    ** ------------------------------------------------------------------------
    */
    public static List<EmployeeAttendanceTab> queryAttendanceSortedByDate(String sStartDate, String sEndDate) {
        String[] whereArgs = {sStartDate, sEndDate};
        String whereCondition = "intime >= ? and intime <= ?";
        String orderBy = "employee, intime";

        List<EmployeeAttendanceTab> empAtndRow = find(EmployeeAttendanceTab.class, whereCondition, whereArgs, null, orderBy, null);

        return empAtndRow;
    }

    public static String getCurrentAttendanceAsJson(Context ctx)
    {
        DailyReport dr = ShowAttendance.getDailyAttendance(ctx);
        Gson gson = new Gson();
        return gson.toJson(dr);
    }

    /*
    **  TODO : Unused for now, check if its worth being here or can it be deleted.
    */

    public static List<EmployeeAttendanceTab> queryEmployeeAttendance(String empid, String sStartDate, String sEndDate) {
        String whereCondition = "intime >= ? and intime <= ?";
        if(empid != null && !empid.isEmpty()){
            whereCondition = whereCondition + " and employee = ?";
        }
        whereCondition = whereCondition + " order by intime";
        List<EmployeeAttendanceTab> empAtndRow = EmployeeAttendanceTab.find(EmployeeAttendanceTab.class, whereCondition, sStartDate, sEndDate, empid);
        return empAtndRow;
    }

}
