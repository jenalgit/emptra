package JsonObject;

public class WeeklyReportObj{
    public String creation_date;
    public MonthlyReport monthlyreports[];

    public AttendanceObj getWeeklyReport_week(int position, int monthno, int weekno){
        return monthlyreports[position].attendancemonthly[monthno-1].days[weekno-1];
    }
    public AttendanceMonthList getWeeklyReport_month(int position, int monthno){
        return monthlyreports[position].attendancemonthly[monthno-1];
    }

}