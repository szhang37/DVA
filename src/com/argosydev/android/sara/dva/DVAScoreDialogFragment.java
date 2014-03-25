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
public class DVAScoreDialogFragment extends DialogFragment{
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
	    View v =inflater.inflate(R.layout.dva_score, null);
        TextView v1 = (TextView) v.findViewById(R.id.score_title);
        TextView v2 = (TextView) v.findViewById(R.id.score);
        String message ="SCORE UNAVAILABLE.";
        double score = 0;
        switch(DVAActivity.dvaTestType){
		case STATIC:
			message = "STATIC LOGMAR SCORE:";
			score = ((DVAActivity)getActivity()).dvascore.getStaticScore();
			break;
        case DN:
        	message = "DYNAMIC DOWN LOGMAR SCORE:";
        	score = ((DVAActivity)getActivity()).dvascore.getDynamicDownScore();
			break;
		case LEFT:
			message = "DYNAMIC LEFT LOGMAR SCORE:";
			score = ((DVAActivity)getActivity()).dvascore.getDynamicLeftScore();
			break;
		case RIGHT:
			message = "DYNAMIC RIGHT LOGMAR SCORE:";
			score = ((DVAActivity)getActivity()).dvascore.getDynamicRightScore();
			break;
		case UP:
			message = "DYNAMIC UP LOGMAR SCORE:";
			score = ((DVAActivity)getActivity()).dvascore.getDynamicUpScore();
			break;
		default:
			break;
        }
        v1.setText(message);
        v2.setText(String.format("%.5f", score));
        builder.setView(v).setNeutralButton("OK", null);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}