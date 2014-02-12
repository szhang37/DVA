package com.argosydev.android.sara.dva;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DVASettingsActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.dva_l3);
    }
}