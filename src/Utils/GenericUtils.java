package Utils;

import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.multiframe.MenuSyncServer;
import com.example.multiframe.R;
import com.orm.SugarApp;
//import static com.orm.SugarApp.getSugarContext;

public class GenericUtils {
    static final String SCAN_FIRSTNAME = "firstname";
    static final String SCAN_SURNAME = "surname";
    static final String SCAN_EMPID = "empid";
    static final String SCAN_DESIGN = "designation";
    static final String SCAN_FORMAT = "Format";

    static final String SCAN_INTIME = "intime";
    static final String SCAN_OUTTIME = "outtime";

    static Boolean bFilePath = false;
    static File dirFile = null;

    public static Boolean showLog = false;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
    public static SimpleDateFormat dayFormat=new SimpleDateFormat("EEEE", Locale.ENGLISH);

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            Log.e("UTILS:CopyStream", ex.getLocalizedMessage());
        }
    }

    public static File saveFileToAppDir(Context context, String dirType, String fileName, String data) {
        File downloadDir = new File(context.getExternalFilesDir(dirType), "");
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            Log.e("UTILS", "saveFileToAppDir: Directory not created");
        }

        File file = new File(context.getExternalFilesDir(dirType), fileName);
        try {
            FileOutputStream myOutput = new FileOutputStream(file);
            myOutput.write(data.getBytes());
            myOutput.close();
        } catch (IOException e) {
            Log.e("UTILS", e.getMessage());
        }

        return file;
    }

    public static String readFileFromAppDir(Context context, String fileName) {
        if(!bFilePath)
        {
            dirFile = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            bFilePath = true;
        }
        if(dirFile != null) {
            try {
                File downloadDir = new File(dirFile, fileName);
                fileName = downloadDir.getCanonicalPath();
            } catch (Exception e) {
                Log.e("readFile", e.getMessage());
                fileName = null;
            }
        }
        return fileName;
    }

    public static void CreateXML(String sFileName,
                                 HashMap<String, String>[] hXMLList, String sTag) {

        File sdCard = Environment.getExternalStorageDirectory();
        String XML_FILE_PATH = sdCard.getAbsolutePath()
                + "/Download/MyProj/databases/" + sFileName;
        String sHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        String sParentTagStart = "<" + sTag + "s>\n";
        String sParentTagEnd = "</" + sTag + "s>\n";
        String sRecordTagStart = "<" + sTag + ">\n";
        String sRecordTagEnd = "</" + sTag + ">\n";

        try {
            FileOutputStream fos = new FileOutputStream(XML_FILE_PATH);
            fos.write(sHeader.getBytes());
            fos.write(sParentTagStart.getBytes());

            for (int i = 0; i < hXMLList.length; i++) {
                fos.write(sRecordTagStart.getBytes());
                for (Entry<String, String> e : hXMLList[i].entrySet()) {
                    String field = e.getKey();
                    String value = e.getValue();
                    String sFinalVal = "<" + field.toUpperCase() + ">" + value
                            + "</" + field.toUpperCase() + ">\n";
                    try {
                        fos.write(sFinalVal.getBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                fos.write(sRecordTagEnd.getBytes());
            }
            fos.write(sParentTagEnd.getBytes());
            fos.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public static HashMap<String, String> ProcessScanResult(String sScanInfo,
                                                            Boolean bFlag) {
        HashMap<String, String> hList = new HashMap<String, String>();

        String sTemp = sScanInfo.replace((char) 13, ':');
        sTemp = sTemp.replaceAll("\\s+", "");
        String sList[] = sTemp.split(":");

        for (int i = 0; i < sList.length; i++) {
            if (sList[i].compareToIgnoreCase(SCAN_FIRSTNAME) == 0) {
                i++;
                hList.put(SCAN_FIRSTNAME, sList[i]);
            }
            if (sList[i].compareToIgnoreCase(SCAN_SURNAME) == 0) {
                i++;
                hList.put(SCAN_SURNAME, sList[i]);
            }
            if (sList[i].compareToIgnoreCase(SCAN_EMPID) == 0) {
                i++;
                hList.put(SCAN_EMPID, sList[i]);
            }
            if (sList[i].compareToIgnoreCase(SCAN_DESIGN) == 0) {
                i++;
                hList.put(SCAN_DESIGN, sList[i]);
            }
        }
        SimpleDateFormat sdfI = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.UK);
        String inTime = sdfI.format(new Date());
        hList.put(SCAN_INTIME, inTime);
        hList.put(SCAN_OUTTIME, inTime);

        return hList;
    }

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("www.google.com");
            return !(ipAddr.toString().equals(""));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNetworkOnline(Context paramContext) {
        boolean bool1 = false;
        int m = 0;
        int k = 0;
        try {
            NetworkInfo[] networkInfo = ((ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getAllNetworkInfo();
            int n = networkInfo.length;
            int i = 0;
            while (i < n) {
                Object localObject = networkInfo[i];
                int j = m;
                if (((NetworkInfo) localObject).getTypeName().equalsIgnoreCase("WIFI")) {
                    j = m;
                    if (((NetworkInfo) localObject).isConnected()) {
                        j = 1;
                    }
                }
                m = k;
                if (((NetworkInfo) localObject).getTypeName().equalsIgnoreCase("MOBILE")) {
                    boolean bool2 = ((NetworkInfo) localObject).isConnected();
                    m = k;
                    if (bool2) {
                        m = 1;
                    }
                }
                i++;
                k = m;
                m = j;
            }
            if ((m != 0) || (k != 0)) {
                bool1 = true;
            }
        } catch (Exception e) {
            for (; ; ) {
                e.printStackTrace();
            }
        }
        return bool1;
    }

    public static String MicroTimestamp() {
        long startDate = System.currentTimeMillis();
        long startNanoseconds = System.nanoTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        return dateFormat.format(startDate);
    }

    public static String getCurrentTime() {
        long startDate = System.currentTimeMillis();
        long startNanoseconds = System.nanoTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.UK);
        return dateFormat.format(startDate);
    }

    public static String getCurrentDate() {
        long startDate = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        return dateFormat.format(startDate);
    }

    public static Date getDate() {
        long startDate = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        String curDate = dateFormat.format(startDate);
        try {
            return dateFormat.parse(curDate);
        }catch(Exception e){LogMe("GenericUtils:Date", "DateformatException "+e.getLocalizedMessage());}
        return(new Date());
    }

    public Date getCurrentCalDate(){
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    public static String DateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        return dateFormat.format(date);
    }

    public static String DateToString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
        return dateFormat.format(date);
    }

    public static Date DateStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.UK);
        try {
            LogMe("StrToDate", "Converting string to date " +date);
            return dateFormat.parse(date);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Date daysAgo(int days){
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        return(new Date(System.currentTimeMillis() - ((days-1) * DAY_IN_MS)));
    }
    public static Date daysAgo(Date startDate, int days){
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        return(new Date(startDate.getTime() - ((days-1) * DAY_IN_MS)));
    }

    public static int dateDiff(Date startDate, Date endDate) {
        int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return diffInDays ;
    }

    public static String GetNullDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.Z", Locale.UK);
        return dateFormat.format("1900-01-01 00:00:00.000");
    }

    public final AlertDialog initiateScan(FragmentActivity fa, Collection<String> desiredBarcodeFormats) {
        Intent intentScan = new Intent("com.google.zxing.client.android.SCAN");

        intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        intentScan.putExtra("SCAN_FORMATS", "QR_CODE_MODE");
        fa.startActivityForResult(intentScan, 312 /*IntentIntegrator.REQUEST_CODE*/);

        String targetAppPackage = "ET - QRCODE SCANNER";
        intentScan.setPackage(targetAppPackage);
        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return null;
    }

    public static void LogMe(String module, String msg){
        LogMe(module, msg, 0);
    }
    public static void LogMe(String module, String msg, Integer logType) {
        Boolean bShowLog = showLog;
        if (bShowLog) {
            switch(logType){
                case 0: Log.i(module, msg);break;
                case 1: Log.d(module, msg);break;
                case 2: Log.w(module, msg);break;
                case 3: Log.e(module, msg);break;
                default : Log.i(module, msg);
            }
        }
    }

    public static void showMessage(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showSimpleAlert(Context context, String title, String msg) {
        context.setTheme(R.style.MenuTheme);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        context.setTheme(R.style.ETTheme);
    }


    public static void showAlert(Context context, String title, String msg){
        context.setTheme(R.style.MenuTheme);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Sync the data, pop up the Sync Server screen.
                        Intent intentSync = new Intent(context, MenuSyncServer.class);
                        context.startActivity(intentSync);
                        Toast.makeText(context, "Servery Sync selected", Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        context.setTheme(R.style.ETTheme);
    }

    public static void showPlainAlert(Context context, String title, String msg){
        context.setTheme(R.style.MenuTheme);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        context.setTheme(R.style.ETTheme);
    }


    public static String getSetting(Context ctx, String key) {

        if (ctx != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences prefsFile = ctx.getSharedPreferences(key, Context.MODE_PRIVATE);
            return prefs.getString(key, "unknown");
        } else{
            Log.e("SETTING", "NULL context defined");
            return null;
        }
    }

    public static final String PREFS_NAME = "ScanPrefsFile";
    static SharedPreferences scandata;
    static SharedPreferences.Editor editor;

    public static String getSharedPrefs(Context context, String key){
        String prefsData;
        scandata = context.getSharedPreferences(PREFS_NAME, 0);
        prefsData = scandata.getString(key, "No data found");
        return prefsData;
    }

    public static void setSharedPrefs(Context context, String key, String value){
        scandata = context.getSharedPreferences(PREFS_NAME, 0);
        editor = scandata.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean validatePasscode(String passcode, Context ctx){
        return validateCode(ctx, passcode);
    }

    private static boolean validateCode(Context context, String msg)
    {
        Integer val = Integer.parseInt(msg);
        UserDAO ud = new UserDAO();
        Log.v("validate" , " "+val);

        //ud.printPasswordHash(msg);
        String role = ud.validateLogin(msg);
        if(role.equals("unknown"))
            return false;
        else if(role.equals("administrator"))
            return true;
        else if(role.equals("manager"))
            return true;
        else if(role.equals("supervisor"))
            return true;
        else if(role.equals("director"))
            return true;
        else if(role.equals("trial")) {
            //check if app was installed and used for 30 days
            Context lContext = context; //SugarApp.getSugarContext();
            String trialKey = GenericUtils.getSharedPrefs(lContext, "trialkey");
            if(trialKey.compareTo("No data found") == 0){
                GenericUtils.setSharedPrefs(lContext, "trialkey", getCurrentDate());
                Log.d("TRIAL KEY ", "Trial starts on " + getCurrentDate());
            }
            else {
                int diffDays = 0;
                Log.d("TRIAL KEY ", "Trial Key installed Date " + trialKey);
                try{
                    diffDays = GenericUtils.dateDiff(dateFormat.parse(trialKey), getDate());
                }catch(Exception e){
                    Log.e("Licensing", "Date format exception");
                }
                if(diffDays > 30)
                   return false;
                else
                    Log.i("License Expiry", "License expires in " + (30 - diffDays) );
                   // showPlainAlert(lContext, "License Expiry", "License expires in " + (30 - diffDays));
            }
            return true;
        }
        else
            return false;
    }

    public static Boolean validateApplicationLicense(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String devId = tm.getDeviceId();
        //showPlainAlert(context, "Device Id", devId);
        return true;
    }
}