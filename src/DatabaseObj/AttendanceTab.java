package DatabaseObj;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import android.util.Log;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

// Available with latest SugarORM code > 1.4
//import com.orm.dsl.Table;
//import com.orm.dsl.Column;
//@Table(name = "Attendance")

public class AttendanceTab extends SugarRecord {
	private String empid;

	public int day;   //Mon - 1 to Sun - 7
	public Timestamp intime; //dd-mm-yyyy
	public Timestamp outtime; //dd-mm-yyyy
	public float hours;
	public String notes;
	public Boolean bHoliday;
	@Ignore 
	static String packageName="Attendance";
	
  public AttendanceTab(){
  }
  public AttendanceTab(String empid,
					   Timestamp intime, Timestamp outtime) {
	  this.empid = empid;
	  if(intime.equals(null) && outtime.equals(null) ){
		  this.bHoliday = true;
	  }
	  else {
		  this.intime = intime;
		  this.outtime = outtime;
	  }
  }
  
  /* Find Attendance data using empid to get all Attendances set empid=ALLEMP*/
  public static List<AttendanceTab> queryAttendance(String whereCondition)
  {
	return AttendanceTab.find(AttendanceTab.class, whereCondition);
  }
  
  /* Get the attendance of all the employees for the given date */
  public List<AttendanceTab> getAttendanceByDate(Date sqlDate, int iRange)
  {
	  Calendar c = Calendar.getInstance();
	  int Day = c.get(Calendar.DAY_OF_MONTH);
	  String whereCondition = "where intime >= '"+ sqlDate;
	  return AttendanceTab.find(AttendanceTab.class, whereCondition);
  }
}

