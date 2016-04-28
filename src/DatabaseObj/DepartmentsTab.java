package DatabaseObj;

import android.util.Log;
import java.util.List;
import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

@Table
public class DepartmentsTab extends SugarRecord {
    //@Column(name = "deptid", unique = true, notNull = true)
    public String deptname;
    String description;

    public DepartmentsTab(){}
    public DepartmentsTab(Long deptId, String deptName, String description){
        this.deptname = deptName;
        this.description = description;
        Log.i("DepartmentTab", "INSERT RECORD");
    }

    public static DepartmentsTab queryDepartment(int deptId) {
        String bindValue = ""+deptId;
        String whereCondition = "id = ? ";
        List<DepartmentsTab> deptRow = SugarRecord.find(DepartmentsTab.class, whereCondition, bindValue);
        if(deptRow.size() > 0) {
            return deptRow.get(0);
        } else
            return null;
    }

    public static DepartmentsTab queryDepartmentId(int deptId) {
        String bindValue = ""+deptId;
        String whereCondition = "id = ? ";
        List<DepartmentsTab> deptRow = SugarRecord.find(DepartmentsTab.class, whereCondition, bindValue);
        if(deptRow.size() > 0) {
            return deptRow.get(0);
        } else
            return null;
    }

    public static DepartmentsTab queryDepartment(String deptName) {
        String bindValue = ""+deptName;
        String whereCondition = "deptname = ? ";
        List<DepartmentsTab> deptRow = SugarRecord.find(DepartmentsTab.class, whereCondition, bindValue);
        if(deptRow.size() > 0) {
            return deptRow.get(0);
        } else
            return null;
    }

}
