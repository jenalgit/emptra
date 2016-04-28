package com.example.multiframe;

import JsonObject.ActivityList;
import JsonObject.ActivityObj;
import android.support.v4.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import Utils.JsonProcessing;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import static Utils.GenericUtils.*;

public class ListActivity extends Fragment {
    ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ListAct:onCreateview", "=> Start ");
        ActivityAdapter adapter;
        View rootView = inflater.inflate(R.layout.activity_layout, container, false);
        ArrayList<ActivityObj> arrActivities  = new ArrayList<ActivityObj>();
        ActivityList activityList = new ActivityList();
        String jsonFileName = "activity_details.json";
        JsonProcessing js;
        FragmentActivity activity = getActivity();
        try {
            js = new JsonProcessing(activity);
            String jsonData = js.readFile(activity, jsonFileName, false);
            activityList = js.convertJsonToObj(jsonData, ActivityList.class);
            LogMe("JSON", activityList.address + ", "+activityList.company + " - " + activityList.acts[0].act_author +", "+ activityList.acts[0].act_status);
        } catch (FileNotFoundException e) {
            Log.e("ListActivity", "FILE NOT FOUND "+e);
        } catch (IOException e) {
            Log.e("ListActivity", "FILE : IOException : " +e);
            e.printStackTrace();
        }

        list = (ListView) rootView.findViewById(R.id.list);

        // Getting adapter by passing json data ArrayList
        adapter = new ActivityAdapter(activity, activityList, false);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });
        return rootView;
    }
}

/*
** Notes ---------------------
   Gson gson = new Gson();
   BufferedReader reader = new BufferedReader(new InputStreamReader(this
                   .getAssets().open("json_data/"+ jsonFileName), "UTF-8"));
   JsonEmp myTypes = gson.fromJson(reader, JsonEmp.class);
   arrEmp = myTypes.employees;
   Log.i("JSON Processing", "JSON : "+ js.convertObjToJson(emp));
 */