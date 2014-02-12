package com.argosydev.android.sara.dva;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;

import android.os.Environment;
import android.util.Log;

import android.content.Context;
import android.util.Log;

public final class CombineDblArr {
	public double[][] rtrnArr;
	public String bigFileStrng;
	private static File dvaLog;
	private static BufferedWriter dvaLogWriter;
	private static String logBuf;

	CombineDblArr(double[][] lrgArr, int lrgTSDim, double[][] smlArr,
			int smlTSDim, double startTS, BufferedWriter dvaDynaBigWriter) {
		// find the closest TSes for each array
		// pull the data from the arrays into a file loading string
		// search the arrays for the first timestamp that is nearest the
		// start TS

		double d = -1;
		double bestDistanceNow = Double.MAX_VALUE;
		int locLrgDex = 0;
		int locSmlDex = 0;
		int lrgSize = lrgArr.length;
		int smlSize = smlArr.length;
		int locCShown = 0;
		int locCResponse = 0;

		//File root = Environment.getExternalStorageDirectory();
		//dvaLog = new File(root, "DVA/testLog.csv");
		//try {
			//dvaLog.delete();
			//dvaLog.createNewFile();
			//dvaLogWriter = new BufferedWriter(new FileWriter(dvaLog, true));
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}

		for (int i = 0; i < lrgSize; i++) {
			if (lrgArr[i][lrgTSDim] == startTS) {
				locLrgDex = i;
				break;
			} else {
				d = Math.abs(lrgArr[i][lrgTSDim] - startTS);
				// cannot optimize the search due to cyclical TS design of
				// arrays
				// the max TS delta could be in consecutive rows
				if (d < bestDistanceNow) {
					bestDistanceNow = d;
					locLrgDex = i;
				}
			}
		}
		d = -1;
		bestDistanceNow = Double.MAX_VALUE;
		for (int i = 0; i < smlSize; i++) {
			if (smlArr[i][smlTSDim] == startTS) {
				locSmlDex = i;
				break;
			} else {
				d = Math.abs(smlArr[i][smlTSDim] - startTS);
				// cannot optimize the search due to cyclical TS design of
				// arrays
				// the max TS delta could be in consecutive rows
				if (d < bestDistanceNow) {
					bestDistanceNow = d;
					locSmlDex = i;
				}
			}
		}
		// get the dims of the 2D arrays
		int lrgDmsn = 0;
		int smlDmsn = 0;
		try {
			lrgDmsn = lrgArr[0].length;
			smlDmsn = smlArr[0].length;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		rtrnArr = new double[lrgSize][lrgDmsn + smlDmsn];
		// change from lrgSize for testing
		for (int j = 0; j < lrgSize; j++) {

			// align the TS when combining rows. both arrays are the same size,
			// but diff TS values. NOTE: may not need to traverse entire lrgArr
			// and smlArr.
			for (int k = 0; k < lrgDmsn; k++) {
				rtrnArr[j][k] = lrgArr[locLrgDex][k];
			}
			for (int k = 0; k < smlDmsn; k++) {
				rtrnArr[j][k + lrgDmsn] = smlArr[locSmlDex][k];
			}
			// see if the next TS in smlArr is within range of the next TS in
			// lrgArr
			locLrgDex++;
			// check for rollover condition
			if ((locLrgDex + 1) >= lrgSize)
				locLrgDex = 0;
			if (locSmlDex + 1 >= smlSize)
				locSmlDex = 0;
			// timestamp look ahead compare to replicate slower sampled values
			if (smlArr[locSmlDex + 1][smlTSDim] < lrgArr[locLrgDex + 1][lrgTSDim])
				locSmlDex++;
		}
		// create the writeable string
		int bigFileDim = lrgDmsn + smlDmsn;
		int locTS1 = lrgTSDim;
		int locTS2 = lrgTSDim + smlTSDim + 1;
		// find the location of the acuityLvl and c_shown information. source
		// arrays could be flipped.
		// warning: changes in dimension of source arrays impacts this
		// conditional
		if (smlArr[0].length == 7) {
			// smlArr is the internal accel store and has the acuityLvl &
			// c_shown info
			locCShown = lrgDmsn + 5;
			locCResponse = lrgDmsn + 6;
		} else {
			locCShown = 5;
			locCResponse = 6;
		}

		DecimalFormat fTS = new DecimalFormat("######.###");
		bigFileStrng = "";
		// removed \n from the SB initializer
		StringBuilder sb = new StringBuilder();
		// warning: if the sample rates are changed for the sensors, data
		// location will be shifted
		// in the big data file. may add headers to the file.
		sb.setLength(30000);
		for (int l = 0; l < lrgSize; l++) {
			// break if a TS value is 0 because source array was not filled and
			// rolled over
			if (rtrnArr[l][lrgTSDim] != 0.0) {
				// build the string
				for (int m = 0; m < bigFileDim; m++)
					if (m == locTS1 || m == locTS2) {
						// convert
						sb.append(fTS.format(rtrnArr[l][m])).append(",");
					} else if (m == locCShown || m == locCResponse) {
						switch ((int) rtrnArr[l][m]) {
						case 0:
							sb.append("UP").append(",");
							break;
						case 1:
							sb.append("RIGHT").append(",");
							break;
						case 2:
							sb.append("DOWN").append(",");
							break;
						case 3:
							sb.append("LEFT").append(",");
							break;
						default:
							sb.append("ERROR").append(",");
							break;
						}
					} else {
						sb.append(rtrnArr[l][m]).append(",");
						if (rtrnArr[l][m] == 0) {
							logBuf = l + "," + m + "," + rtrnArr[l][m] + "\n";
//							try {
//								dvaLogWriter.write(logBuf);
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
						}
					}

				sb.append("\n");
			} else
				break;
		}
		try {
			dvaDynaBigWriter.write(sb.toString());
			dvaDynaBigWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			dvaDynaBigWriter.close();
			dvaLogWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
