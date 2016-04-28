package com.example.multiframe;

import Utils.GenericUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.splashscreen);
        Log.i("SplashScreen:onCreate", "=> Start");

        //View rootView = inflater.inflate(R.layout.scans_layout, container, false);

        final EditText passCode = (EditText)findViewById(R.id.accessCode);

        Button go = (Button) findViewById(R.id.go);
        go.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        String userCode = passCode.getText().toString();

                        if(GenericUtils.validatePasscode(userCode, getBaseContext())) {
                            validateNetwork();
                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast toast = Toast.makeText(getBaseContext(),
                                    "Invalid/Expired Passcode. Try again", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

            }
        }, SPLASH_TIME_OUT);

        Log.i("SplashScreen:onCreate", "<= End");
    }

    private boolean validateNetwork() {
        if (GenericUtils.isNetworkAvailable(getBaseContext())) {
            Log.d("SplashScreen:Network", "connected");
            //if (GenericUtils.isInternetAvailable()) {
            if(GenericUtils.isNetworkOnline(getBaseContext())){
                Log.d("SplashScreen:Internet", "Internet available");

//                List<NameValuePair> params = new ArrayList<>();;
//                params.add(new BasicNameValuePair("Name", "TEST"));
//                JSONParser jsonParser = new JSONParser();
//                JSONObject json = jsonParser.makeHttpRequest("https://api.myjson.com/bins/4q7am", "GET", params);
//
//                try {
//                    Log.i("JSON FROM URI ", ""+json.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            } else {
                Toast toast = Toast.makeText(getBaseContext(),
                        "Internet not available", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Log.d("SplashScreen:Network", "connection failed");
            Toast toast = Toast.makeText(getBaseContext(),
                    "Network connection failed. Check wifi/data connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        return true;
    }



}
