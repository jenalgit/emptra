package com.example.multiframe;

import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.os.Bundle;
import android.preference.*;
import Utils.*;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class MenuSettingActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MenuTheme);
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            PreferenceScreen preferences=getPreferenceScreen();
            preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            onSharedPreferenceChanged(null, "prefUsername");
            onSharedPreferenceChanged(null, "prefLocation");
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d("SETTING", "onSharedPreferenceChanged " +key);
            updatePrefSummary(findPreference(key));
        }

        private void updatePrefSummary(Preference p) {
            Log.d("SETTING", "updatePrefSummary : " + p);

            if (p instanceof ListPreference) {
                ListPreference listPref = (ListPreference) p;
                p.setSummary(listPref.getEntry());
            }
            else if (p instanceof EditTextPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                if (p.getTitle().toString().contains("password")) {
                    p.setSummary("******");
                } else {
                    p.setSummary(editTextPref.getText());
                }
            }
            else if (p instanceof MultiSelectListPreference) {
                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());
            }
            else {
                EditTextPreference editTextPref = (EditTextPreference) p;
                p.setSummary(editTextPref.getText());
            }
        }



        @Override
        public void onResume() {
            Log.d("SET", "onResume");
            super.onResume();
            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                            // listener implementation
                        }
                    };

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            Log.d("SET", "onPause");
            super.onPause();
//        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}