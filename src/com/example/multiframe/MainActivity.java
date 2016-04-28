package com.example.multiframe;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;

import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import DatabaseObj.DBHelper;
import Utils.ETConstants;

import static Utils.GenericUtils.*;

public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    private String[] tabs = { "Scan", "Activity", "Report", "Employee", "Search" };
    private String[] icons = { "icon_scan", "icon_scan", "icon_report", "icon_employee", "icon_search" };
    TabPagerAdapter tabPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //DBHelper.createTestData();
        //DBHelper dbh = new DBHelper(getBaseContext());
        DBHelper.initializeDatabase(getBaseContext(), ETConstants.PACKAGE_NAME, ETConstants.DB_NAME);
        // Initialisation
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);

        actionBar = getActionBar();
        getActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0; i < tabPagerAdapter.getCount(); i++) {
            ActionBar.Tab tab = actionBar.newTab().setTabListener(this);
            int resId = getResources().getIdentifier(icons[i], "drawable",
                    this.getPackageName());
            Drawable d = this.getResources().getDrawable(resId);
            if(d != null){
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 65, 65, true));
            }

            //tab.setIcon(d);
            tab.setText(tabs[i]);
            actionBar.addTab(tab);
            LogMe("MA:OnCreate", "addTab : " + i);
        }




        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /* on swipe select the respective tab */
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
               // tabPagerAdapter.notifyDataSetChanged(); //this line will force all pages to be loaded fresh when changing between fragments
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        LogMe("MA:onCreate", "<= End");
    }

//    @Override
//    public void onResume() {
//        LogMe("MA:onResume", "<= Start");
//        super.onResume();
//        int activePage = 0;
//        if (viewPager.getAdapter() != null) {
//            activePage=viewPager.getCurrentItem();
//            LogMe(getClass().getSimpleName(), "pager.getAdapter()!=null", 2);
//       //     viewPager.setAdapter(null);
//
//        }
//        viewPager.setAdapter(tabPagerAdapter);
//        viewPager.setCurrentItem(activePage);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        com.google.zxing.integration.android.IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        LogMe("MainActivity", "onActivityResult ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showLog=true;
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_search:
                Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, MenuSettingActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_sync:
                Intent intentSync = new Intent(this, MenuSyncServer.class);
                startActivity(intentSync);
                Toast.makeText(this, "Servery Sync selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_payroll:
                Intent intentPayroll = new Intent(this, MenuPayrollProcessing.class);
                startActivity(intentPayroll);
                Toast.makeText(this, "Payroll selected", Toast.LENGTH_SHORT)
                        .show();
                break;
//          case R.id.action_scan:
//              Toast.makeText(this, "Scan selected", Toast.LENGTH_SHORT)
//                      .show();
//              FragmentManager fm = getSupportFragmentManager();
//              Fragment fragment = fm.findFragmentByTag("myFragmentTag");
//              if (fragment == null) {
//                  FragmentTransaction ft = fm.beginTransaction();
//                  fragment =new ScansActivity();
//                  ft.add(android.R.id.content, fragment, "myFragmentTag");
//                  ft.commit();
//              }
//              break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        int nPos = tab.getPosition();
        LogMe("MA:onTabSelected", "Tab Selected "+nPos);
        viewPager.setCurrentItem(nPos);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }
}