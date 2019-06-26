package com.example.user.gamearticlesmenu;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class SourceSettingActivity extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private Toolbar toolbar;

    public static SourceSettingActivity newInstance() {
        return new SourceSettingActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.source_preferences);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }
}
