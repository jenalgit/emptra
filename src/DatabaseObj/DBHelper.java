package DatabaseObj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import Utils.ETConstants;
import Utils.GenericUtils;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.orm.SugarRecord;

//import static com.orm.SugarRecord.save;
/* Project specific imports */

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;
    private String sqlQuery;
    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "employee_mobile.db";
    public static String DB_PATH;
    public static String APP_DB_PATH;
    public static String APP_DB_FILE;
    Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i("DBHelper:DBHelper", "=> Start");
        File sdCard = Environment.getExternalStorageDirectory();
        APP_DB_PATH = sdCard.getAbsolutePath() + "/Download/MyProj/databases/";
        APP_DB_FILE = APP_DB_PATH + DB_NAME + ".db";
        myContext = context;
        DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        Log.i("DBHelper:DBHelper", DB_PATH);
    }

    public void onCreateDatabase() throws IOException {
        Log.i("DBHelper:onCreateDB", "=> Start");
        boolean dbExists = databaseExists();

        if (dbExists) {
            //Log.w("DBHelper:onCreateDatabase", DB_PATH +"=>" +APP_DB_PATH + "tempDB" + ".db");
            //FileCache.copyFile(DB_PATH, APP_DB_PATH + "tempDB" + ".db");
            dropDatabase();
            dbExists = false;
        }

        if (!dbExists) {
            this.getReadableDatabase();

            try {
                copyDatabase();
            } catch (IOException e) {
                System.out.println("copy database error");
            }
        }
        Log.i("DBHelper:onCreateDB", "<= End");
    }

    private boolean databaseExists() {
        Log.i("DBHelper:databaseExists", "=> Start");
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH;

        Log.w("DBHelper:databaseExists", "Check if exists : " + myPath);

        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.w("DBHelper:databaseExists", "database does not exist : "
                    + myPath);
        }

        Log.i("DBHelper:databaseExists", "<= End");
        return checkDB != null ? true : false;
    }

    private void dropDatabase() {
        Log.i("DBHelper:dropDatabase", DB_PATH);
        File file = new File(DB_PATH);
        SQLiteDatabase.deleteDatabase(file);
    }

    private void copyDatabase() throws IOException {
        Log.i("DBHelper:copyDatabase", "=> Start");
        // myContext.getAssets().
        // InputStream myInput = myContext.getAssets().open(DB_NAME);

        String inputFileName = APP_DB_PATH + DB_NAME + ".db";
        Log.i("DBHelper:copyDatabase", "inputFileName : " + inputFileName);

        try {
            File file = new File(inputFileName);
            InputStream myInput = new BufferedInputStream(new FileInputStream(file));

            String outFileName = DB_PATH;
            Log.i("DBHelper:copyDatabase", "outFileName : " + outFileName);
            OutputStream myOutput = new FileOutputStream(outFileName);

            try {
                GenericUtils.CopyStream(myInput, myOutput);
            } finally {
                try {
                    if (myOutput != null) {
                        try {
                            myOutput.flush();
                        } finally {
                            myOutput.close();
                        }
                    }
                } finally {
                    if (myInput != null) {
                        myInput.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.w("DBHelper:copyDatabase", e.getMessage());
        }
        Log.i("DBHelper:copyDatabase", "<= End");
    }


    public static void initializeDatabase(Context context, String packageName, String DBName)
    {
        checkDataBase(context, packageName, DBName);
    }

    protected static void copyDataBaseFromAssets(Context context) throws IOException {

        //Open your local db as the input stream
        InputStream myInput =  context.getAssets().open("employee_mobile.db");

        // Path to the just created empty db
        String outFileName = "/data/data/com.example.multiframe/databases/" + ETConstants.DB_NAME;

        SQLiteDatabase db = context.openOrCreateDatabase("employee_mobile.db", context.MODE_PRIVATE, null);
        db.close();
        Log.w("DBHelper", "Copying file from "+myInput+" to "+outFileName);
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    protected static boolean checkDataBase(Context context, String packageName, String DBName){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = "/data/data/"+packageName+"/databases/" + DBName;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            Log.w("DBHelper", "Database already exits");
        }catch(SQLiteException e){
            //database doesn't exist yet.
            try {
                copyDataBaseFromAssets(context);
                Log.w("DB CREATION", "Copying DB from assets");
            } catch(Exception ex/*| IOException ex*/){
                System.out.println("IOException : " +ex);
            }
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public boolean sqlInsert(String sTable,
                             HashMap<String, String> hColValues) {

        if (db == null) {
            return false;
        }
        db.execSQL(sqlQuery);
        return true;
    }

    /* Name : sqlSelect
     * Params : tablename and list of colums to be retrieved
     * Desc : Return 0 or more rows in the HashMap array
     * with hash map containing columns & values
     */
    public HashMap<String, String>[] sqlSelect(String sTable, String hCols[],
                                               HashMap<String, String> hWhere) {
        Log.i("DBHelper:sqlSelect", "=> Start");
        String sColsSelect = "";
        String sColsWhere = " ";

        int i = 0;

        SQLiteDatabase db = this.getWritableDatabase();

        for (i = 0; i < hCols.length; i++) {
            sColsSelect = sColsSelect + hCols[i] + ", ";
        }

        for (Entry<String, String> e : hWhere.entrySet()) {
            String field = e.getKey();
            String value = e.getValue();
            sColsWhere = sColsWhere + field + " = " + value + " and ";
        }
        String sqlQuery = "SELECT " + sColsSelect + "1 FROM " + sTable
                + " WHERE " + sColsWhere + " 1=1 ";

        Log.i("DBHelper:sqlSelect", sqlQuery);
        Cursor c = db.rawQuery(sqlQuery, null);
        int nRows = 0;
        HashMap<String, String>[] hResult = new HashMap[c.getCount()];
        Log.i("DBHelper:sqlSelect", "Total Rows : " + c.getCount());
        if (c.moveToFirst()) {
            hResult[0] = new HashMap<String, String>();
            for (i = 0; i < hCols.length; i++) {
                hResult[0].put(hCols[i], c.getString(i));
                Log.i("DBHelper:sqlSelect", c.getString(i));
            }
        }
        nRows++;
        while (c.moveToNext()) {
            hResult[nRows] = new HashMap<String, String>();
            for (i = 0; i < hCols.length; i++) {
                hResult[nRows].put(hCols[i], c.getString(i));
                Log.i("DBHelper:sqlSelect", c.getString(i));
            }
            nRows++;
        }

        Log.i("DBHelper:sqlSelect", "Total records " + hResult.length);

        Log.i("DBHelper:sqlSelect", "<= End");
        return hResult;

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper:onCreate", "=> Start");
        boolean createDatabase = true;

    /*
     * This will create by reading a sql file and executing the commands in
     * it.
     */
        // try {
        // InputStream is =
        // myContext.getResources().getAssets().open("create_database.sql");
        //
        // String[] statements = FileHelper.parseSqlFile(is);
        //
        // for (String statement : statements) {
        // db.execSQL(statement);
        // }
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
    }

    public SQLiteDatabase openDatabase() throws SQLException {
        // Open the database
        Log.i("DBHelper:openDatabase", "=> Start");
        String myPath = DB_PATH + DB_NAME;

        if (databaseExists())
            return SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        return null;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.i("DBHelper:onOpen", "=> Start");
    }

    public static void GetDBEmployees(Context context, HashMap<String, String> empScanned) {
        DBHelper mydb = new DBHelper(context);

        try {
            mydb.onCreateDatabase();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //SQLiteDatabase myDb = null;
        //myDb = mydb.getWritableDatabase();

        String hCols[] = {"id", "name", "address"};
        String xmlCols[] = {"empid", "name", "address"};

        HashMap<String, String> hWhere = new HashMap<String, String>();
        //hWhere.put("id",empScanned.get("empid"));

        HashMap<String, String>[] hResult = mydb.sqlSelect("employee", hCols, hWhere);
        String sResult = "";
        for (int i = 0; i < hResult.length; i++) {
            for (Entry<String, String> e : hResult[i].entrySet()) {
                String field = e.getKey();
                String value = e.getValue();
                sResult = sResult + field.toUpperCase() + "  " + value + "\n";
            }

            Log.w("DBHelper:GetDBEmployees", sResult);
            // showMessage("Employee Details", sResult);
        }
        GenericUtils.CreateXML("employees.xml", hResult, "employee");
    }

    public static void createTestData()
    {
        String[] department = {"14", "Human resource", "Project Management", "Administration", "Transport", "Facility Management", "Sales and Purchase",
        "Marketing", "Finance", "Software Development", "Health & Safety", "Electronics", "Robotics", "Communication", "Library", "Training"};
        String[] designation = {"CEO", "CFO", "Finance Manager", "Administration Manager", "Transport Manager", "Facility Manager", "Sales Manager", "Lead Developer", "Peoplesoft Admin"};

        for (int i = 0; i < designation.length; i++) {
            DepartmentsTab deptTab = new DepartmentsTab((long)i, department[i], department[i]);
            deptTab.save();

            DesignationTab desgnTab = new DesignationTab(designation[i], designation[i]);
            SugarRecord.save(desgnTab);

            Log.i("TestData", "INSERT "+ i +", first" + department[i] + "," + designation[i]);

            EmployeeTab empTab = new EmployeeTab(i + "", "first" + i, "surname" + i, deptTab, desgnTab, "my@mail.id");
            empTab.save();
        }
        DepartmentsTab deptTab = new DepartmentsTab((long)14, "14", "unknown department");
        SugarRecord.save(deptTab);

        DesignationTab desgnTab = new DesignationTab("Peoplesoft Admin", "Peoplesoft Admin");

        SugarRecord.save(desgnTab);

        EmployeeTab empTab = new EmployeeTab(12346 + "", "Swarna", "Shree", deptTab, desgnTab, "my@gmail.id");
        empTab.save();


        empTab = new EmployeeTab(110011 + "", "Alan", "McPhee", deptTab, desgnTab, "alan@gmail.id");
        empTab.save();
    }
}
