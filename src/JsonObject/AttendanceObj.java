package JsonObject;
/*
 * Description : Day Attendance structure to store data from JSON file
 */

import java.sql.Timestamp;
import java.util.Date;

public class AttendanceObj
{
	public int day;      //Mon - 1 to Sun - 7
	public Date intime;  //dd-mm-yyyy
	public Date outtime; //dd-mm-yyyy
	public String hours;
	public String notes;
	public Boolean bHoliday;
	public Date datetime;
}
