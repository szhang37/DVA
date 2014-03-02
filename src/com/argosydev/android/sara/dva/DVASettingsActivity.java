package com.argosydev.android.sara.dva;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

public class DVASettingsActivity extends PreferenceActivity  implements OnSharedPreferenceChangeListener {

	public static String dva_test_retrys_num = "dva_test_retrys_num";
	public static String dva_static_start_lvl = "dva_static_start_lvl";
	public static String dva_dyna_start_lvl = "dva_dyna_start_lvl";
	public static String dva_test_per_acuity = "dva_test_per_acuity";
	public static String dva_max_acuity_lvl = "dva_max_acuity_lvl";
	public static String dva_trg_inhibitor_num = "dva_trg_inhibitor_num";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.dva_l3);
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		sharedPref.registerOnSharedPreferenceChangeListener(this);
		update();
	}

	@SuppressLint("NewApi") 
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		Preference pref = findPreference(key);
        if (pref instanceof SwitchPreference) {
            if(sharedPreferences.getBoolean(key, false)){
            		setValue(getResources().getIntArray(R.array.dva_test_far));	

            		
            }
            else {
            	setValue(getResources().getIntArray(R.array.dva_test_near));
            }
        }
		update();
		// ((DVAActivity) this.getApplicationContext()).UpdateSharePreference();
	}
	
	private void update() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		//findPreference("dva_test_path_pj_setting").
		
		findPreference(dva_test_retrys_num).setSummary(
						getResources().getString(
								R.string.dva_test_retrys_num_summary)
								+ ": "
								+ sharedPref.getString(dva_test_retrys_num, ""));

		findPreference(dva_static_start_lvl)
				.setSummary(
						getResources().getString(
								R.string.dva_static_start_lvl_summary)
								+ ": "
								+ sharedPref
										.getString(dva_static_start_lvl, ""));

		findPreference(dva_dyna_start_lvl)
				.setSummary(
						getResources().getString(
								R.string.dva_dyna_start_lvl_summary)
								+ ": "
								+ sharedPref.getString(dva_dyna_start_lvl, ""));

		findPreference(dva_test_per_acuity)
				.setSummary(
						getResources().getString(
								R.string.dva_test_per_acuity_summary)
								+ ": "
								+ sharedPref.getString(dva_test_per_acuity, ""));

		findPreference(dva_max_acuity_lvl)
				.setSummary(
						getResources().getString(
								R.string.dva_max_acuity_lvl_summary)
								+ ": "
								+ sharedPref.getString(dva_max_acuity_lvl, ""));

		findPreference(dva_trg_inhibitor_num)
				.setSummary(
						getResources().getString(
								R.string.dva_trg_inhibitor_num_summary)
								+ ": "
								+ sharedPref.getString(dva_trg_inhibitor_num,
										""));
		int[] val = new int[6];
		val[0] = Integer.parseInt(sharedPref.getString(DVASettingsActivity.dva_test_retrys_num, ""));
		val[1]= Integer.parseInt(sharedPref.getString(DVASettingsActivity.dva_static_start_lvl, ""));
		val[2] = Integer.parseInt(sharedPref.getString(DVASettingsActivity.dva_dyna_start_lvl, ""));
		val[3] = Integer.parseInt(sharedPref.getString(DVASettingsActivity.dva_max_acuity_lvl, ""));
		val[4]= Integer.parseInt(sharedPref.getString(DVASettingsActivity.dva_test_per_acuity, ""));
		val[5] = Integer.parseInt(sharedPref.getString(DVASettingsActivity.dva_trg_inhibitor_num, ""));
		
		DVAActivity.UpdateSharePreference(val);
	}
	
	private void setValue(int[] arr){
		
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		
		editor.putString(dva_test_retrys_num, String.valueOf(arr[0]));

		editor.putString(dva_static_start_lvl, String.valueOf(arr[1]));
		
		
		editor.putString(dva_dyna_start_lvl, String.valueOf(arr[2]));
		
		
		editor.putString(dva_test_per_acuity, String.valueOf(arr[3]));
		
		
		editor.putString(dva_max_acuity_lvl, String.valueOf(arr[4]));
		
		
		editor.putString(dva_trg_inhibitor_num, String.valueOf(arr[5]));
		
		editor.commit();
		
	}
}