package com.example.user.gamearticlesmenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.prefs.PreferencesFactory;

public class MainSettingActivity extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static MainSettingActivity newInstance() {
        return new MainSettingActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        findPreference("button_source_select").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "button_source_select":
                Log.d("button selected", "aiueo");
                transitionFragment(SourceSettingActivity.newInstance());
                break;
        }
        return false;
    }

    private void transitionFragment (PreferenceFragment nextPreferenceFragment) {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, nextPreferenceFragment)
                .commit();
    }
}
