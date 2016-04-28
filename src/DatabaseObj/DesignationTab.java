package DatabaseObj;

import android.util.Log;
import com.orm.SugarRecord;
import com.orm.dsl.Table;
import java.util.List;

@Table
public class DesignationTab {
    //int desigid;
    public String designame;
    String roles;

    public DesignationTab(){}
    public DesignationTab(String desigName, String roles){
        //this.desigid = desigtId;
        this.designame = desigName;
        this.roles = roles;
    }

    public static DesignationTab queryDesignation(String desigName) {
        Log.i("DesignationTab:Find", "Searching " + desigName);
        String bindValue = ""+desigName;
        String whereCondition = "designame = ? ";
        List<DesignationTab> dsgRow = SugarRecord.find(DesignationTab.class, whereCondition, bindValue);
        if(dsgRow.size() > 0) {
            Log.i("DesignationTab:Find", "total records : " + dsgRow.size());
            return dsgRow.get(0);
        }
        else
            return null;
    }

    public static DesignationTab queryDesignation(int desigId) {
        Log.i("DesignationTab:Find", "Searching for" + desigId);
        String bindValue = ""+desigId;
        String whereCondition = "id = ? ";
        List<DesignationTab> dsgRow = SugarRecord.find(DesignationTab.class, whereCondition, bindValue);
        if(dsgRow.size() > 0) {
            Log.i("DesignationTab:Find", "total records : " + dsgRow.size());
            return dsgRow.get(0);
        }
        else
            return null;
    }

    public static DesignationTab queryDesignationId1(int Id) {
        String bindValue = ""+Id;
        String whereCondition = "id = ? ";
        List<DesignationTab> dsgRow = SugarRecord.find(DesignationTab.class, whereCondition, bindValue);
        if(dsgRow.size() > 0) {
            Log.i("DesignationTab:Find", "total records : " + dsgRow.size());
            return dsgRow.get(0);
        }
        else
            return null;
    }

    public static DesignationTab queryDesignationId(int desigId) {
        String bindValue = ""+desigId;
        String whereCondition = "id = ? ";
        List<DesignationTab> deptRow = SugarRecord.find(DesignationTab.class, whereCondition, bindValue);
        if(deptRow.size() > 0) {
            return deptRow.get(0);
        } else
            return null;
    }
}
