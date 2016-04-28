package DatabaseObj;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.orm.SugarRecord;
//import com.orm.dsl.Column;
//import com.orm.dsl.Table;
//@Table(name = "ScanInfoTable")

public class ScanInfoTable extends SugarRecord {
	private String empid;
	private String name;
	private Date   intime;
	private Date   outtime;
	private String notes;
	
	public ScanInfoTable() {
		this.empid = "";
		this.name= "";
		this.intime = new Date(1,1,1900);
		this.outtime = new Date(1,1,1900);
		this.notes = "";
	}
	
	public ScanInfoTable(String empid, String name, Date intime, Date outtime) {
		this.empid = empid;
		this.name= name;
		this.intime = intime;
		this.outtime = outtime;
		this.notes = "";
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	    String currentDateandTime = sdf.format(new java.util.Date());
	    intime = java.sql.Date.valueOf(currentDateandTime);
	    outtime = java.sql.Date.valueOf(currentDateandTime);
	}
	
}
