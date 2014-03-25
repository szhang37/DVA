package com.argosydev.android.sara.dva;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.argosydev.android.sara.dva.DVAActivity.DVATestType;

public class DVAScore {
	//public enum scoreType {Static, Up, Down, Left, Right};
	private double staticScore = 0;
	private double dynamicUpScore = 0;
	private double dynamicDownScore = 0;
	private double dynamicLeftScore = 0;
	private double dynamicRightScore = 0;
	
	public Time dynamicUpTime;
	public Time dynamicDownTime;
	public Time dynamicLeftTime;
	public Time dynamicRightTime;
	
	public static String loglabel = "DVAScore";
	private double scoreTable[][];
	private Context context;

	public DVAScore(Context cont) {
		// read the data from the asset file
		context = cont;
		scoreTable = new double[21][6];
		InputStream dvdScoreFileStream = null;
		try {

			dvdScoreFileStream = cont.getAssets().open(
					"dva_score_full_table.xml");
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser dvaXpp = factory.newPullParser();
			dvaXpp.setInput(dvdScoreFileStream, null);
			int dvaEventType = dvaXpp.getEventType();
			int i = 0;
			while (dvaEventType != XmlPullParser.END_DOCUMENT) {
				if (dvaEventType == XmlPullParser.START_TAG) {
					
					String dvaScoreText = dvaXpp.getName();
					if (dvaScoreText.equals("Opto")) {
						//Log.e(loglabel, "step2");
						dvaEventType = dvaXpp.next();
						scoreTable[i][0] = Double.parseDouble(dvaXpp.getText());
					} else if (dvaScoreText.equals("Size")) {
						dvaEventType = dvaXpp.next();
						scoreTable[i][1] = Double.parseDouble(dvaXpp.getText());
					} else if (dvaScoreText.equals("Nearlogmar")) {
						dvaEventType = dvaXpp.next();
						scoreTable[i][2] = Double.parseDouble(dvaXpp.getText());
					} else if (dvaScoreText.equals("Nearsnellen")) {
						dvaEventType = dvaXpp.next();
						scoreTable[i][3] = Double.parseDouble(dvaXpp.getText());
						
					} else if (dvaScoreText.equals("Farlogmar")) {
						dvaEventType = dvaXpp.next();
						scoreTable[i][4] = Double.parseDouble(dvaXpp.getText());
					} else if (dvaScoreText.equals("Farsnellen")) {
						dvaEventType = dvaXpp.next();
						scoreTable[i][5] = Double.parseDouble(dvaXpp.getText());
						i++;
					}
						//Log.v(loglabel, "current i:"+i);
					}
				dvaEventType = dvaXpp.next();
				}

		} catch (XmlPullParserException e) {
			Log.e(loglabel, "1.Unable to generate score table from asset file!, caused by"+e.getLocalizedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(loglabel, "2.Unable to generate score table from asset file!, caused by"+e.getLocalizedMessage());
		}
	}

	public double getStaticScore() {
		return staticScore;
	}

	public void setStaticScore(double staticScore) {
		this.staticScore = staticScore;
	}

	public double getDynamicUpScore() {
		return dynamicUpScore;
	}

	public void setDynamicUpScore(double dynamicUpScore) {
		this.dynamicUpScore = dynamicUpScore;
	}

	public double getDynamicDownScore() {
		return dynamicDownScore;
	}

	public void setDynamicDownScore(double dynamicDownScore) {
		this.dynamicDownScore = dynamicDownScore;
	}

	public double getDynamicLeftScore() {
		return dynamicLeftScore;
	}

	public void setDynamicLeftScore(double dynamicLeftScore) {
		this.dynamicLeftScore = dynamicLeftScore;
	}

	public double getDynamicRightScore() {
		return dynamicRightScore;
	}

	public void setDynamicRightScore(double dynamicRightScore) {
		this.dynamicRightScore = dynamicRightScore;
	}
	public void updateScore(DVATestType type, int level, boolean isFar, int wrongCount){
		double score = 0;
		
			if(!isFar)
				score = scoreTable[level][2] + (scoreTable[level+1][2]-scoreTable[level][2])*(double)wrongCount/(double)DVAActivity.dvaTestPerAcuity;
			else score = scoreTable[level][4] + (scoreTable[level+1][4]-scoreTable[level][4])*(double)wrongCount/(double)DVAActivity.dvaTestPerAcuity;
			
//			int duration = Toast.LENGTH_SHORT;
//
//			Toast toast = Toast.makeText(context, "current score:"+score, duration);
//			toast.show();
			
			switch(type){
			case DN:
				dynamicDownScore = score;
				break;
			case LEFT:
				dynamicLeftScore = score;
				break;
			case RIGHT:
				dynamicRightScore = score;
				break;
			case STATIC:
				staticScore = score;
				break;
			case UP:
				dynamicUpScore = score;
				break;
			default:
				break;
			
			}
		
	}
	public void setTimestamp(DVATestType type){
		switch(type){
		case DN:
			dynamicDownTime = new Time();
			dynamicDownTime.setToNow();
			break;
		case LEFT:
			dynamicLeftTime = new Time();
			dynamicLeftTime.setToNow();
			break;
		case RIGHT:
			dynamicRightTime = new Time();
			dynamicRightTime.setToNow();
			break;
		case STATIC:
			break;
		case UP:
			dynamicUpTime = new Time();
			dynamicUpTime.setToNow();
			break;
		default:
			break;
		
		}
	}
}
