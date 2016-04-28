package JsonObject;
/*
 * Description : Day Attendance structure to store data from JSON file
 */
import com.orm.SugarRecord;

import java.sql.Timestamp;
import java.util.Date;

public class Attendance
{
	public String day;            //Indexing
	public Date intime;   //dd-mm-yyyy
	public Timestamp outtime;  //dd-mm-yyyy
	public float hours;
	public String notes;
	public Boolean bHoliday;
	public Date datetime;
}
