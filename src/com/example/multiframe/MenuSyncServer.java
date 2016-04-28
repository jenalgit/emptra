package com.example.multiframe;

import JsonObject.DailyReport;
import Utils.ETConstants;
import Utils.GenericUtils;
import Utils.ImageUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DatabaseObj.EmployeeAttendanceTab;
import Utils.JSONParser;

import static Utils.GenericUtils.LogMe;

public class MenuSyncServer extends Activity implements View.OnClickListener {
	private static final int PROGRESS = 0x1;

	private ProgressBar mProgressBar;
    private ProgressDialog mProgress;
	private int mProgressStatus = 10;
    private Handler mHandler = new Handler();
    static int val = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.syncserver_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

        Button btFrom = (Button) findViewById(R.id.bt_sync_from);
        btFrom.setOnClickListener(this);

        Button btTo = (Button) findViewById(R.id.bt_sync_to);
        btTo.setOnClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    public void onClick(View v) {
        ServerSyncTask mt = new ServerSyncTask(this);
        switch(v.getId()) {
            case R.id.bt_sync_from: mt.execute(ETConstants.ET_SYNC_FROM_SERVER, "GET");
                break;
            case R.id.bt_sync_to: mt.execute(ETConstants.ET_SYNC_TO_SERVER, "POST");
                break;
            default: return;
        }
    }
}

class ServerSyncTask extends  AsyncTask<String, Void, String> {
    Context context;
    Exception syncException;

    ServerSyncTask(Context context){
        this.context = context;
    }
    ProgressDialog mProgress;
    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(context);
        mProgress.setMessage("Synchronizing data from/to server");
        mProgress.setIndeterminate(true);
        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgress.setCancelable(true);
        mProgress.show();
    }

    @Override
    protected String doInBackground(String... aurl){
        LogMe("ServerSyncTask", "=> doInBackground  Start " + aurl[0] + " : " + aurl[1]);

        if(aurl[1] != null && aurl[1].equalsIgnoreCase("GET")) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Name", "TEST"));
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.makeHttpRequest(aurl[0], "GET", params, null, null);
            String result = json.toString();

            GenericUtils.saveFileToAppDir(context, Environment.DIRECTORY_DOWNLOADS, ETConstants.ET_WEEKLYREPORT_JSON, result);
            try {
                Log.i("JSON FROM URI ", "" + result);
            } catch (Exception e) {
                syncException = e;
                e.printStackTrace();
            }
            syncImages();
            return result;
        }
        else if(aurl[1] != null && aurl[1].equalsIgnoreCase("POST")){
            List<NameValuePair> params = new ArrayList<>();
            List<NameValuePair> headers = new ArrayList<>();
            params.add(new BasicNameValuePair("Name", "SAJIVE"));
            headers.add(new BasicNameValuePair("passcode", "emptra_v1"));
            headers.add(new BasicNameValuePair("Accept", "application/json"));
            headers.add(new BasicNameValuePair("Content-type", "application/json"));
            JSONParser jsonParser = new JSONParser();

            DailyReport dr = ShowAttendance.getDailyAttendance(context);
            Gson gson = new Gson();
            String atnd = gson.toJson(dr);

            Log.d("SYNC", atnd);
            JSONObject json = jsonParser.makeHttpRequest(aurl[0], "POST", params, headers, atnd);
            Log.i("POST JSON TO URI ", "" + json);
            if(json == null)
                syncException = new IOException();

            return "POST Response";
        }
        else
            return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if(mProgress != null && mProgress.isShowing()) mProgress.dismiss();

        if(syncException != null)
            GenericUtils.showSimpleAlert(context, "Server Synchronisation", "Connection to server failed/refused. Enable data network or WiFi and try again.");
    }

    protected void syncImages()
    {
        int i = 0;
        String empList[]={"10874030", "10874031","10874032", "10874033", "10874034", "10874035", "10874036", "10874037", "10874038",
                "10874039", "10874040", "10874041", "10874042", "10874043", "10874044", "10874045", "10874046", "10874047",
                "10874048", "10874049", "10874050", "10874051", "10874052", "10874053", "10874054", "10874055", "10874056", "10874057"};
        try {
            File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            while(i < empList.length) {
                File file = new File(dir, empList[i]+".jpg");
                String imageUrl = ETConstants.ET_SERVER_URL_IMG +empList[i]+".jpg";
                saveImage(imageUrl, file.getCanonicalPath());
                i++;
            }
        }catch(Exception e){Log.e("readFile", e.getMessage());}
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        final File pictureFile = new File(destinationFile + ".1");
        OutputStream os = new FileOutputStream(pictureFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        Bitmap bm = BitmapFactory.decodeFile(pictureFile.getCanonicalPath());
        Bitmap newBitMap = ImageUtils.getResizedBitmap(bm, 50, 50);
        FileOutputStream out = new FileOutputStream(destinationFile);
        newBitMap.compress(Bitmap.CompressFormat.PNG, 100, out);

        is.close();
        os.close();
    }

}
