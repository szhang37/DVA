package com.argosydev.android.sara.dva;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

@SuppressLint("NewApi") 
public class DVADialogFragment extends DialogFragment{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View v =inflater.inflate(R.layout.dva_init, null);
	    
	    final EditText subName = (EditText) v.findViewById(R.id.dva_subject_name);
	    subName.setText(DVAActivity.subName);
	    final EditText prjName = (EditText) v.findViewById(R.id.dva_project_name);
	    prjName.setText(DVAActivity.prjName);
	    final EditText fdName = (EditText) v.findViewById(R.id.dva_folder_name);
	    fdName.setText(DVAActivity.fdName);
	    final EditText perAcuity = (EditText) v.findViewById(R.id.dva_set_per_acuity);
	    perAcuity.setText(String.valueOf(DVAActivity.dvaTestPerAcuity));
	    final EditText maxAcuity = (EditText) v.findViewById(R.id.dva_set_max_acuity);
	    maxAcuity.setText(String.valueOf(DVAActivity.dvaMaxAcuityLvl));
	    final EditText staticStrt = (EditText) v.findViewById(R.id.dva_set_static_strt);
	    staticStrt.setText(String.valueOf(DVAActivity.dvaStaticStrt));
	    final EditText dynaStrt = (EditText) v.findViewById(R.id.dva_set_dyna_strt);
	    dynaStrt.setText(String.valueOf(DVAActivity.dvaDynaStrt));
	    final EditText trgIn = (EditText) v.findViewById(R.id.dva_set_trg_inhi);
	    trgIn.setText(String.valueOf(DVAActivity.dvaTrigInhibitor));
	    final EditText retrys = (EditText) v.findViewById(R.id.dva_set_retrys);
	    retrys.setText(String.valueOf(DVAActivity.dvaRetrys));
	    builder.setView(v)
	           .setPositiveButton(R.string.dvachange, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // sign in the user ...
	            	   String[] strs ={String.valueOf(prjName.getText()),String.valueOf(subName.getText()),String.valueOf(fdName.getText())};
	            	   int[] vals = {Integer.valueOf(String.valueOf(retrys.getText())),
	            					Integer.valueOf(String.valueOf(staticStrt.getText())),
	            					Integer.valueOf(String.valueOf(dynaStrt.getText())),
	            					Integer.valueOf(String.valueOf(perAcuity.getText())),
	            					Integer.valueOf(String.valueOf(maxAcuity.getText())),
	            					Integer.valueOf(String.valueOf(trgIn .getText())) };
	            	   setValue(vals, strs);
	           		   //(DVAActivity)getActivity()).update    
	            	   ((DVAActivity) getActivity()).updateFilePath();
	               }
	           });    
	    return builder.create();
	}
private void setValue(int[] arr, String[] val){
		
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		
		SharedPreferences.Editor editor = sharedPref.edit();
		
		editor.putString(DVASettingsActivity.dva_test_retrys_num, String.valueOf(arr[0]));

		editor.putString(DVASettingsActivity.dva_static_start_lvl, String.valueOf(arr[1]));
		
		
		editor.putString(DVASettingsActivity.dva_dyna_start_lvl, String.valueOf(arr[2]));
		
		
		editor.putString(DVASettingsActivity.dva_test_per_acuity, String.valueOf(arr[3]));
		
		
		editor.putString(DVASettingsActivity.dva_max_acuity_lvl, String.valueOf(arr[4]));
		
		
		editor.putString(DVASettingsActivity.dva_trg_inhibitor_num, String.valueOf(arr[5]));
		
		editor.putString("dva_test_path_pj_setting", String.valueOf(val[0]));

		editor.putString("dva_test_path_sub_setting", String.valueOf(val[1]));
		
		editor.putString("dva_test_path_fd_setting", String.valueOf(val[2]));
		
		editor.commit();
		DVAActivity.UpdateSharePreference(arr);
		DVAActivity.updatePathParams( String.valueOf(val[0]), String.valueOf(val[1]), String.valueOf(val[2]));
	}
}
