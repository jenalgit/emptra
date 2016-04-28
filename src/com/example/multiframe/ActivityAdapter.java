package com.example.multiframe;

import JsonObject.*;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import static Utils.GenericUtils.*;

public class ActivityAdapter extends BaseAdapter {

    private Activity activity;
    private ActivityList activityData;
    private static LayoutInflater inflater = null;

    View vi;
    TextView company;
    TextView address;
    TextView act_date;
    TextView act_title;
    TextView act_desc;
    TextView act_author;
    TextView act_status;
    TextView act_type;
    TextView act_comment;

    LinearLayout lytW;
    ImageView thumb_image;

    public ActivityAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        showLog = false;
    }

    public ActivityAdapter(Activity a, ActivityList ao, Boolean bFlag) {
        activity = a;
        activityData = ao;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(R.layout.activity_list, null);
        //vi.setBackgroundColor(Color.WHITE);
        act_date = (TextView) vi.findViewById(R.id.act_date);
        act_author = (TextView) vi.findViewById(R.id.act_author);
        act_title = (TextView) vi.findViewById(R.id.act_title);
        act_desc = (TextView) vi.findViewById(R.id.act_desc);
        act_status = (TextView) vi.findViewById(R.id.act_status);
        //act_image = (ImageView) vi.findViewById(R.id.act_image);
        showLog = false;
    }

    public int getCount() {
        return activityData.acts.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.activity_list, null);
        }
        getElements(vi);
        ActivityObj actObj = new ActivityObj();
        actObj = activityData.acts[position];

        // Setting all values in listview
        act_status.setText(actObj.act_status);
        act_title.setText(actObj.act_title);
        act_date.setText(actObj.act_date);
        act_desc.setText(actObj.act_desc);
        act_author.setText(actObj.act_author);
      //  thumb_image.setImageResource(R.drawable.emp_default);

        return vi;
    }

    void getElements(View vi) {
        act_status = (TextView) vi.findViewById(R.id.act_status);
        act_title = (TextView) vi.findViewById(R.id.act_title);
        act_desc = (TextView) vi.findViewById(R.id.act_desc);
        act_date= (TextView) vi.findViewById(R.id.act_date);
        act_author = (TextView) vi.findViewById(R.id.act_author);
        //act_image = (ImageView) vi.findViewById(R.id.act_image);
    }
}
