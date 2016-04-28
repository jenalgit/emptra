package com.example.multiframe;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import  static Utils.GenericUtils.*;

public class TabPagerAdapter extends FragmentPagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        showLog = true;
        LogMe("TabPagerAdapter:cons", "Constructor");
    }

    @Override
    public Fragment getItem(int index) {
        LogMe("TabPagerAdapter:getItem", "=> Start " + index);

        Integer i = index;
        if (i.equals(null)) {
            LogMe("TabPagerAdapter", "getItem : Null check", 2);
            return new ListEmployees();
        }
        switch (i) {
            case 0:
                LogMe("TabPagerAdapter:getItem", "ScansActivity - " + index);
                return new ScansActivity();
            case 1:
                LogMe("TabPagerAdapter:getItem", "Activity Page - " + index);
                return new ListActivity();
            case 2:
                LogMe("TabPagerAdapter:getItem", "ShowAttendance - " + index);
                return new ShowAttendance();
            case 3:
                LogMe("TabPagerAdapter:getItem", "ListEmployees - " + index);
                return new ListEmployees();
            case 4:
                LogMe("TabPagerAdapter:getItem", "SearchActivity - " + index);
                return new SearchInformation();
            default:
                LogMe("TabPagerAdapter:getItem", "Unknown Request - " + index);
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}