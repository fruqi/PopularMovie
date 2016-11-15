package com.amrta.android.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by amrta on 08/11/2016.
 */

public class SettingsActivity extends PreferenceActivity
    implements Preference.OnPreferenceChangeListener
{
    private static final String TAG = "SettingsActivity";


    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        // Initialize preference & bind preference into a value
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String value = newValue.toString();

        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);

            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }

        else
        {
            preference.setSummary(value);
        }

        return true;
    }


    private void bindPreferenceSummaryToValue(Preference preference)
    {
        // bind preferenceChangeListener
        preference.setOnPreferenceChangeListener(this);

        // call onPreferenceChange to initialize preference value
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }
}
