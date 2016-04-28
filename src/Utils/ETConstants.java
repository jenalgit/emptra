package Utils;


public class ETConstants {
    public static final int DB_INSERT_SUCCESS = 1;
    public static final int DB_UPDATE_SUCCESS = 2;
    public static final int DB_INSERT_FAILED = -1;
    public static final int DB_UPDATE_FAILED = -2;
    public static final int DB_RECORD_FOUND = 3;
    public static final int DB_RECORD_NOT_FOUND = -3;

    public static final String ET_SYNC_TO_SERVER = "http://192.168.1.5:8081/dailyreport";    //POST Request
    public static final String ET_SYNC_FROM_SERVER = "https://api.myjson.com/bins/4q7am";    //GET Request
    public static final String ET_SERVER_URL_IMG = "http://insight.library.cornell.edu/mrsid/mrsid_images/RMC/Size4/RMC0036/";
    public static final String ET_WEEKLYREPORT_JSON = "weekly_attendance.json";
    public static final String ET_SERVER_URL_JSON = "http://192.168.1.5:8081/reports/weekly_attendance.json";


    public static final String PACKAGE_NAME = "com.example.multiframe";
    public static final String DB_NAME =  "employee_mobile.db";



}