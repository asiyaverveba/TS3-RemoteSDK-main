package com.enhancell.remotesample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.verveba.ts3.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference _callNumberPref;
    private EditTextPreference _testDurationPref;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        _callNumberPref = findPreference("call_number");
        assert _callNumberPref != null;
        _callNumberPref.setOnPreferenceChangeListener((preference, newValue) -> {
            var number = newValue.toString();
            var edit = requireActivity().getSharedPreferences("ts3-demo", Context.MODE_PRIVATE).edit();
            edit.putString("call_number", number);
            edit.apply();
            return true;
        });

        _testDurationPref = findPreference("test_duration");
        assert _testDurationPref != null;
        _testDurationPref.setOnPreferenceChangeListener((preference, newValue) -> {
            try {
                int duration = Integer.parseInt(newValue.toString());
                var edit = requireActivity().getSharedPreferences("ts3-demo", Context.MODE_PRIVATE).edit();
                edit.putInt("test_duration", duration);
                edit.apply();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });

        initialize();
    }

    private void initialize() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("ts3-demo", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        if (!prefs.contains("phone_call_number")) {
            _callNumberPref.setText(Configs.DEFAULT_CALL_NUMBER);
        }
        if (!prefs.contains("test_duration")) {
            _testDurationPref.setText(String.valueOf(Configs.DEFAULT_TEST_DURATION));
        }
        edit.apply();
    }
}
