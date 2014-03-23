package com.argosydev.android.sara.dva;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class DVAScore {
	public static double staticScore = 0;
	public static double dynamicUpScore = 0;
	public static double dynamicDownScore = 0;
	public static double dynamicLeftScore = 0;
	public static double dynamicRightScore = 0;
	public static String loglabel = "DVAScore";
	private double scoreTable[][];

	public DVAScore(Context cont) {
		// TODO create the array of full table

		InputStream dvdScoreFileStream;
		try {
			
			dvdScoreFileStream = cont.getAssets().open("dva_score_full_table.xml");

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			
			factory.setNamespaceAware(true);
			
			XmlPullParser dvaXpp = factory.newPullParser();
			
			dvaXpp.setInput(dvdScoreFileStream, null);
			
			int dvaEventType = dvaXpp.getEventType();
			int i=0;
			while (dvaEventType != XmlPullParser.END_DOCUMENT) {
				if (dvaEventType == XmlPullParser.START_TAG) {
					String dvaScoreText = dvaXpp.getName();
					if(dvaScoreText.equals("row")){
						while (dvaEventType != XmlPullParser.END_DOCUMENT) {
							if (dvaEventType == XmlPullParser.START_TAG) {
								String scoreType = dvaXpp.getName();
								if(scoreType.equals("Opto")){
									scoreTable[i][0] = Double.parseDouble(dvaXpp.getText());
								}
								else if(scoreType.equals("Size")){
									scoreTable[i][1] = Double.parseDouble(dvaXpp.getText());
								}
								else if(scoreType.equals("Nearlogmar")){
									scoreTable[i][2] = Double.parseDouble(dvaXpp.getText());
								}
								else if(scoreType.equals("Nearsnellen")){
									scoreTable[i][3] = Double.parseDouble(dvaXpp.getText());
								}
								else if(scoreType.equals("Farlogmar")){
									scoreTable[i][4] = Double.parseDouble(dvaXpp.getText());
								}
								else if(scoreType.equals("Farsnellen")){
									scoreTable[i][5] = Double.parseDouble(dvaXpp.getText());
								}
							}
							dvaEventType = dvaXpp.next();
						}
						i++;
					}
					
				}
				dvaEventType = dvaXpp.next();
			}
			
			
		} catch (Exception e) {
			Log.e(loglabel, "Unable to generate score table from asset file!");
		}
	}

}
