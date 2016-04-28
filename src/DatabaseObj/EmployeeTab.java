package DatabaseObj;

import java.sql.Blob;
import java.util.List;

import android.provider.ContactsContract;
import android.util.Log;
import com.orm.SugarRecord;
import com.orm.dsl.Table;


// Available with latest SugarORM code > 1.4
//import com.orm.dsl.Column;
//import com.orm.dsl.Table;
//@Table(name = "Employee")

@Table
public class EmployeeTab extends SugarRecord {
    private Long id;
    public String surname;
    public String firstname;
    public String dob;
    public String sex;
    public DepartmentsTab deptId;
    public DesignationTab desigId;
    public String status;
    public String phone;
    public String  pancard_no;
    public String  passport_no;
    public int experience;
    public String education;
    public Blob photo;

    public EmployeeTab() {
    }

    public EmployeeTab(String empid,
                       String firstname,
                       String surname,
                       DepartmentsTab deptId,
                       DesignationTab desigId,
                       String contact) {
        this.id = Long.parseLong(empid);
        this.firstname = firstname;
        this.surname = surname;
        this.desigId = desigId;
        this.status = "active";
        Log.i("Employee", "Constructor - Create employee " + this.firstname);
    }

    public Long getId() { return this.id; }

    /*
    ** ------------------------------------------------------------------------
    ** Find employee record based on the primary key
    ** ------------------------------------------------------------------------
    */
    public static EmployeeTab findDescId(Class<EmployeeTab> emp, Long i) {
        Log.i("Employee", "findDescId");
        EmployeeTab lEmployee = new EmployeeTab();

        if (emp == null) {
            Log.w("EmployeeTab", "Empty class found : Null");
        }
        try {
            lEmployee = SugarRecord.findById(emp, i);
            Log.i("findDescId", "Found : "  + lEmployee.firstname);
        } catch (Exception e) {
            Log.w("EmployeeTab", "EXCEPTION : " + e.getMessage());
        }
        return lEmployee;
    }

    /*
    ** ------------------------------------------------------------------------
    ** Find employee data using empid to get all employees set empid=ALLEMP
    ** ------------------------------------------------------------------------
    */

    public static List<EmployeeTab> queryEmployee(String empid) {
        String whereCondition = "";
        String bindValue = "";
        String bindValue_status = "";

        if (empid.compareTo("ALL") == 0) {
            whereCondition = " ";
        }
        else if (empid.compareTo("ALL_ACTIVE") == 0) {
            whereCondition = "upper(status) = ?";
            bindValue = "ACTIVE";
        }
        else {
            whereCondition = "id = ? and upper(status) = 'ACTIVE'";
            bindValue = empid;
        }

        int i =0;
        List<EmployeeTab> etList = SugarRecord.find(EmployeeTab.class, whereCondition, bindValue);
        while(i < etList.size())
        {
            EmployeeTab et = etList.get(i);
            Log.d("[FIND EMP]", "Found " + et.firstname +", "+et.dob);
            i++;
        }
        return etList;
    }

}

