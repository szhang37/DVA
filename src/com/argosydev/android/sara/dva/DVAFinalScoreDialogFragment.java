package com.argosydev.android.sara.dva;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


@SuppressLint("NewApi") 
public class DVAFinalScoreDialogFragment extends DialogFragment{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DVAActivity act = (DVAActivity) this.getActivity();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
	    double tem;
        View v =inflater.inflate(R.layout.dva_final_score, null);
        if(act.dvascore.dynamicDownTime!=null){
	    TextView tv1 = (TextView) v.findViewById(R.id.down_score_time);
	    tv1.setText(act.dvascore.dynamicDownTime.format2445());
	    TextView tv2 = (TextView) v.findViewById(R.id.down_static_score);
	    tv2.setText(act.dvascore.getStaticScore()+"");
	    TextView tv3 = (TextView) v.findViewById(R.id.down_dyna_score);
	    tv3.setText(act.dvascore.getDynamicDownScore()+"");
	    TextView tv4 = (TextView) v.findViewById(R.id.down_final_score);
	    tem = act.dvascore.getStaticScore() - act.dvascore.getDynamicDownScore();
	    tem = Math.abs(tem);
	    tv4.setText(tem+"");
        }
	    
        if(act.dvascore.dynamicUpTime!=null){
	    TextView tv21 = (TextView) v.findViewById(R.id.up_time);
	    tv21.setText(act.dvascore.dynamicUpTime.format2445());
	    TextView tv22 = (TextView) v.findViewById(R.id.up_static_score);
	    tv22.setText(act.dvascore.getStaticScore()+"");
	    TextView tv23 = (TextView) v.findViewById(R.id.up_dyna_score);
	    tv23.setText(act.dvascore.getDynamicUpScore()+"");
	    TextView tv24 = (TextView) v.findViewById(R.id.up_final_score);
	    tem = act.dvascore.getStaticScore() - act.dvascore.getDynamicUpScore();
	    tem = Math.abs(tem);
	    tv24.setText(tem+"");
        }
        
        if(act.dvascore.dynamicLeftTime!=null){
	    TextView tv31 = (TextView) v.findViewById(R.id.left_score_time);
	    tv31.setText(act.dvascore.dynamicLeftTime.format2445());
	    TextView tv32 = (TextView) v.findViewById(R.id.left_static_score);
	    tv32.setText(act.dvascore.getStaticScore()+"");
	    TextView tv33 = (TextView) v.findViewById(R.id.left_dyna_score);
	    tv33.setText(act.dvascore.getDynamicLeftScore()+"");
	    TextView tv34 = (TextView) v.findViewById(R.id.left_final_score);
	    tem = act.dvascore.getStaticScore() - act.dvascore.getDynamicLeftScore();
	    tem = Math.abs(tem);
	    tv34.setText(tem+"");
        }
        if(act.dvascore.dynamicRightTime!=null){
	    TextView tv41 = (TextView) v.findViewById(R.id.right_time);
	    tv41.setText(act.dvascore.dynamicRightTime.format2445());
	    TextView tv42 = (TextView) v.findViewById(R.id.right_static_score);
	    tv42.setText(act.dvascore.getStaticScore()+"");
	    TextView tv43 = (TextView) v.findViewById(R.id.right_dyna_score);
	    tv43.setText(act.dvascore.getDynamicRightScore()+"");
	    TextView tv44 = (TextView) v.findViewById(R.id.right_final_score);
	    tem = act.dvascore.getStaticScore() - act.dvascore.getDynamicRightScore();
	    tem = Math.abs(tem);
	    tv44.setText(tem+"");
        }
        builder.setView(v).setNeutralButton("OK", null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}