//package Utils;
//C:\Program Files\Java\jdk1.8.0_05\bin\javac

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

class DailyReport {
    public String today;
    public String location;
    public String address;
    public EmployeeAttendance employees[];
};

class EmployeeAttendance
{
    public String empid;
    public String firstname;
    public String surname;
    public String sex;
    public String department;
    public String designation;
    public String intime;
    public String outtime;
}

//public class Test extends MyClass {

public class Test {
    public static void main(String[] argv) {

        Test tObj = new Test();
        DailyReport dr = tObj.convertJsonToObj(DailyReport.class);
        for(int i = 0; i< dr.employees.length; i++) {
            EmployeeAttendance e = dr.employees[i];
            System.out.println(e.firstname);
            System.out.println(e.empid);
        }
        tObj.convertObjToJson(tObj);
    }

    public  <T> void convertObjToJson(T t) {
        Gson gson = new Gson();
        String jsonResult = gson.toJson(t);
        System.out.println(jsonResult);
    }

    public <T> T convertJsonToObj(Class<T> genericClassType) {
        Gson gson = new Gson();

        try {
            BufferedReader reader;
            {
                File directory = new File("c:\\sajive\\testdata");
                File file = new File(directory, "dailyreport_today.json");

                FileInputStream streamIn = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(streamIn));
            }

            T obj = gson.fromJson(reader, genericClassType);
            System.out.println(obj);
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/*
class MyClass {
    public static int nElementLevel = 0;
    static String lnStart = "\"";
    static String lnEnd = "\",";

    public static <T> void DisplayColVal(T t) {
        Class c = t.getClass();
        Field[] f = c.getDeclaredFields();
        System.out.println("Class Name : " + c.getCanonicalName());
        Object obj = (Object) t;
        for (int i = 0; i < f.length; i++) {
            try {
                String fieldBasicType = f[i].getType().toString();
                String fieldType = f[i].getType().getName();
                Boolean bPrimitive = f[i].getType().isPrimitive();

                // System.out.println("Field["+i+"] "+fieldType + " <" +
                // bPrimitive + "> " + fieldBasicType);
                if (fieldType.equals("java.lang.String")) {
                    Object object = f[i].get(obj);
                    System.out.println(" Value " + (String) object);
                } else if (!bPrimitive && fieldBasicType.contains("class")) {
                    DisplayColVal(f[i].get(obj));
                } else {
                    Object object = f[i].get(obj);
                    System.out.println(" Value " + (Integer) object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public static void DisplayClassVal(Employee c) {
        Class claz = c.getClass();
        Object obj = (Object) c;
        Field[] f = claz.getDeclaredFields();
        System.out.println("Class Name : " + claz.getCanonicalName());

        for (int i = 0; i < f.length; i++) {
            System.out.print("Column " + f[i].getName() + " Type : "
                    + f[i].getType().getName());
            try {
                if (f[i].getType().getName().equals("java.lang.String")) {
                    Object name = f[i].get(obj);
                    System.out.println(" => " + (String) name);
                } else if (f[i].getType().getName().equals("int")) {
                    Object name = f[i].getInt(obj);
                    System.out.println(" => " + (Integer) name);
                }
                System.out.println("----------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public static <T> void ToJson(T t) {
        int i = 0;
        if (t != null) {
            Class c = t.getClass();
            Field[] f = c.getDeclaredFields();
            lnEnd = "\",";
            if (nElementLevel == 0) {
                System.out.println("{ ");
            }
            System.out.println("\"" + c.getCanonicalName() + "\": {");

            Object obj = (Object) t;
            for (i = 0; i < f.length; i++) {
                try {
                    String fieldName = f[i].getName();
                    String fieldBasicType = f[i].getType().toString();
                    String fieldType = f[i].getType().getName();
                    Boolean bPrimitive = f[i].getType().isPrimitive();

                    Object object = f[i].get(obj);
                    Object nextObj = null;
                    if (i == f.length - 1) {
                        lnEnd = "\"";
                    } else {
                        nextObj = f[i + 1].get(obj);
                        lnEnd = "\",";
                    }
                    if (nextObj == null && i == f.length - 2)
                        lnEnd = "\"";

                    //printInfo(0, nElementLevel, i, f.length);


                    if (fieldType.equals("java.lang.String")) {
                        System.out.print(lnStart + fieldName + "\" : ");
                        System.out.println(lnStart + (String) object + lnEnd);
                    } else if (!bPrimitive && fieldBasicType.contains("class")) {
                        //lnStart = "   " + lnStart;
                        nElementLevel++;
                        ToJson(object);
                        nElementLevel--;
                        if (object != null) {
                            if (i != f.length - 1)
                                System.out.println(", ");
                        }
                    } else {
                        System.out.print(lnStart + fieldName + "\" : ");
                        System.out.println(" \"" + (Integer) object + lnEnd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (i == f.length) {
                if (nElementLevel != 0) {
                    System.out.print(" } ");
                } else if (nElementLevel == 0 && i == f.length) {
                    System.out.println("}");
                    System.out.println("}"); // End of JSON
                }
            }

        }
        return;
    }

    static void printInfo(int pLevel, int nLevel, int i, int totalElm) {
        System.out.print("[" + pLevel + ", " + nLevel + "] " + i + "/" + totalElm + " => ");
    }
}
*/


