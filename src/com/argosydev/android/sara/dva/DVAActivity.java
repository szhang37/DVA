package com.argosydev.android.sara.dva;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Formatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.argosydev.android.sara.dva.R;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;

public class DVAActivity extends Activity implements SensorEventListener {

	private static Context context;
	static final int REQUEST_ENABLE_BT = 1;
	static final int REQUEST_CONNECT_SHIMMER = 2;
	static final int REQUEST_CONFIGURE_SHIMMER = 3;
	static final int REQUEST_CONFIGURE_VIEW_SENSOR = 4;
	private final int SENSOR_ACCELGYRO = 0xC0;
	private static GraphView mGraphExtrnl;
	private static GraphView mGraphIntrnl;

	// private DataMethods DM;
	private static TextView mTitle;
	private static TextView mValueSensor1;
	private static TextView mValueSensor2;
	private static TextView mValueSensor3;

	private static TextView mTextSensor1;
	private static TextView mTextSensor2;
	private static TextView mTextSensor3;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Name of the connected device
	private static String mConnectedDeviceName = null;
	// Member object for communication services
	private static Shimmer mShimmerDevice = null;

	private static String mSensorView = ""; // The sensor device which should be
											// viewed on the graph
	private static int mGraphSubSamplingCount = 0;

	// members for internal accelerometer
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	private enum DVATestType {
		STATIC, UP, DN, LEFT, RIGHT
	}

	private enum DVAImageType {
		UP, DN, LEFT, RIGHT
	}

	// not used
	// for Big file store; Up = 0, Rght = 1, Dn = 2 and Left = 3
	private enum DVAState {
		DVAInit, DVAConfigure, DVAStart, DVAStatic, DVADyna, DVADynaUp, DVADynaDn, DVADynaLeft, DVADynaRight, DVACalc, DVAQuit
	}

	private enum DVATesting {
		ACTIVE, INACTIVE
	}

	// not used yet
	private enum DVASensors {
		NONE, INTERNAL, EXTERNAL, BOTH
	}

	// dvaTest data store: internal x,y,z extrnlacc x,y,z extrnlgyro x,y,z
	// timestamp
	private static double[][] dvaStaticArr;
	private static double[][] dvaDynaArr;
	private static double[] dvaStaticScore;
	private static double[] dvaDynaScore;
	private static Long[] dvaTimeStamp;
	private static int dvaStaticDex;
	private static int dvaDynaDex;
	private static int dvaDexMax;
	private static int dDown;

	// test data store and related vars
	private static double[] tstGyro = new double[10000];
	private int tstGyroDex;
	private double triggerPeriod;
	private static int trigCapture;
	private static double gyroTrigger;
	// DVA state control
	private static DVAState dvaState;
	// used to control configuration mode and data acquisition
	private static DVATesting dvaTesting;
	private static DVATestType dvaTestType;
	// used to run Static testing w/o external sensors
	private static DVASensors dvaSensors;
	private int dvaStateMac;
	private int dvaGo;
	private static Button dvb20Static;
	private static Button dvb28StartNewTest;
	private static Button dvb29Left;
	private static Button dvb22Dn;
	private static Button dvb23Rght;
	private static Button dvb24Up;
	private static Button dvb30Return;
	private static Button dvb31Quit;
	private static EditText editText21;
	private static Button dvb1Static;
	private static Button dvb2DynaUp;
	private static Button dvb3DynaDn;
	private static Button dvb4DynaRght;
	private static Button dvb5DynaLeft;
	private static Button dvb6ComputeDVA;
	private static Button dvb7Quit;
	private static ImageView mImageView;
	private static double gyrxMax;
	private static double gyryMax;
	private static double gyrzMax;
	private static String address;
	private static int[] dataArray = new int[0];
	private static double[] extCalibratedDataArr = new double[0];
	private static double[] intrnlDataArr = new double[0];
	private static double tstGyrxMax;
	private static double tstGyrxMin;
	private static double tstGyryMax;
	private static double tstGyryMin;
	private static double tstGyrzMax;
	private static double tstGyrzMin;

	private static Long dvaTstStrtTime;
	private static int acuityMax;
	private static int acuityLvl;
	private static int dvaTestCnt;
	private static int dvaTestCycle;
	private static int dvaTestMax;
	private static int dvaWrgCnt;
	private static Random random;
	private static String staticFileRcd;
	private static String dynaBigFileRcd;
	private static String dynaBigFileStrng;
	private static String dynaSmlFileRcd;
	//add the trigger file record string declaration
	private static String dynaTrgFileRcd;
	private static String dynaTrgMarker;
	private static File dvaFile;
	private static BufferedWriter dvaStaticWriter;
	private static BufferedWriter dvaDynaSmlWriter;
	private static BufferedWriter dvaDynaBigWriter;
	//add the trigger file writer
	private static BufferedWriter dvaDynaTrgWriter;
	private static DVATestType c_shown;
	private static DVATestType c_response;
	private static int[][] dvaCArr;
	private static String dvaFileName;
	private static double maxNumberofSamplesPerSecond;
	private static int gyroLookback;
	ScheduledFuture<?> rcrdBigStrng;
	ScheduledExecutorService dvaSes;
	private static int bigRcrdDex;
	private static String[] sBigRcrd;
	private static double[][] dBigRcrd;
	private static double dvaStartTS;
	private static SimpleDateFormat dvaTSFormat;
	private static Calendar dvaClndr;
	private static int staticStartAcuity;
	private static int dynaStartAcuity;
	private static int maxAcuity;
	private static int flashTries;
	private static XmlPullParser dvaXpp;
	private static InputStream dvaInputStr;
	private static OutputStream dvaOutputStr;
	private static int dvaRetrys;
	private static int dvaStaticStrt;
	private static int dvaDynaStrt;
	private static int dvaStaticTestMax;
	private static int dvaDynaTestMax;
	private static int dvaMaxAcuityLvl;
	private static int retrigger;
	private static TextView dvaRetryCnt;
	private static int dvaTrigInhibit;
	private static int dvaTestPerAcuity;
	private static int dvaTrigInhibitor;
	private static String deviceName;
	private static TextView dvaDeviceTV;
	private static int dvaPositionHold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_dva);
		// setContentView(R.layout.dva_l1);
		dvaGo = 0;
		dvaStaticDex = 0;
		dvaDynaDex = 0;
		gyrxMax = -100;
		gyryMax = -100;
		gyrzMax = -100;
		dataArray = new int[7]; // RM-to display 6 channels of acc +
		// gyr
		extCalibratedDataArr = new double[7]; // 6 challens of acc +
		// gyr
		intrnlDataArr = new double[3];
		trigCapture = dvaRetrys;
		// the gyro trigger threshold in mS
		triggerPeriod = 40;
		gyroTrigger = 120.0; // matches default in layout
		dynaTrgMarker = "TRIG";
		random = new Random();
		acuityMax = 3;
		acuityLvl = acuityMax; // number of acuity levels to test
		dvaWrgCnt = 0;
		dvaTestCycle = 2; // loaded later from file
		// dvaTestMax = acuityLvl * dvaTestCycle;
		dynaBigFileStrng = "";
		maxNumberofSamplesPerSecond = 50;
		double interimDivisor = ((1 / maxNumberofSamplesPerSecond) * 1000);
		try {
			gyroLookback = (int) ((int) triggerPeriod / interimDivisor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dvaSes = Executors.newScheduledThreadPool(1);
		dvaClndr = Calendar.getInstance();
		try {
			dvaTSFormat = new SimpleDateFormat("HHmmssSSS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		File root = Environment.getExternalStorageDirectory();
		File dvaCfgFileDir = new File(root, "DVA/cfg/");
		
		/** modification from Shi */
		if(!dvaCfgFileDir.exists()){
			dvaCfgFileDir.mkdirs();
		}
		File dvaCfgFile = new File(dvaCfgFileDir, "dvaCfg.xml");
		if(!dvaCfgFile.exists())
		{
			try {
				dvaCfgFile.createNewFile();
				//dvaOutputStr = new BufferedOutputStream(new FileOutputStream(dvaCfgFile));
				FileWriter dvdCfgFileWriter = new FileWriter(dvaCfgFile);
				InputStream dvdCfgFileStream = getAssets().open("dvaCfg.xml");
		        int size = dvdCfgFileStream.available();
		        byte[] buffer = new byte[size];
		        dvdCfgFileStream.read(buffer);
		        dvdCfgFileStream.close();
		        dvdCfgFileWriter.write(new String(buffer));
			
			} catch (IOException e) {
				
				Log.e("write file error", e.getMessage());
			}
		}
		
		/** end modification */
		try {
			dvaInputStr = new BufferedInputStream(new FileInputStream(
					dvaCfgFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//parse the data from the configuration file dvaCfg.xml on the sd card
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			dvaXpp = factory.newPullParser();
			dvaXpp.setInput(dvaInputStr, null);
			int dvaEventType = dvaXpp.getEventType();
			while (dvaEventType != XmlPullParser.END_DOCUMENT) {
				if (dvaEventType == XmlPullParser.START_TAG) {
					String dvaCfgText = dvaXpp.getName();
					if (dvaCfgText.equals("dvaRetrys")) {
						dvaEventType = dvaXpp.next();
						dvaRetrys = Integer.parseInt((dvaXpp.getText()));
					}
					if (dvaCfgText.equals("dvaStaticStrt")) {
						dvaEventType = dvaXpp.next();
						dvaStaticStrt = Integer.parseInt((dvaXpp.getText()));
					}
					if (dvaCfgText.equals("dvaDynaStrt")) {
						dvaEventType = dvaXpp.next();
						dvaDynaStrt = Integer.parseInt((dvaXpp.getText()));
					}
					if (dvaCfgText.equals("dvaMaxAcuityLvl")) {
						dvaEventType = dvaXpp.next();
						dvaMaxAcuityLvl = Integer.parseInt((dvaXpp.getText()));
					}
					if (dvaCfgText.equals("dvaTestPerAcuity")) {
						dvaEventType = dvaXpp.next();
						dvaTestPerAcuity = Integer.parseInt((dvaXpp.getText()));
					}
					if (dvaCfgText.equals("dvaTrigInhibitor")) {
						dvaEventType = dvaXpp.next();
						dvaTrigInhibitor = Integer.parseInt((dvaXpp.getText()));
					}
				}
				dvaEventType = dvaXpp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dvaStaticTestMax = dvaStaticStrt * dvaTestPerAcuity;
		dvaDynaTestMax = dvaDynaStrt * dvaTestPerAcuity;
	}

	@Override
	public void onStart() {
		super.onStart();
		// sensor & BT managers instances in onStart
		// instance the internal sensor manager.
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// if there is no BT adapter for external sensor, finish()
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this,
					"Device does not support Bluetooth\nExiting...",
					Toast.LENGTH_LONG).show();
			finish();
		}
		// instance the BT adapter
		try {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				if (mShimmerDevice == null)
					mShimmerDevice = new Shimmer(null, mHandler, "Device 1",
							false);
				onNavigate(110);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		// finish();
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShimmerDevice != null)
			mShimmerDevice.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dva, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem scanItem = menu.findItem(R.id.scan);
		if ((mShimmerDevice != null)
				&& (mShimmerDevice.getShimmerState() == Shimmer.STATE_CONNECTED)) {
			scanItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			scanItem.setTitle(R.string.disconnect);
		} else {
			scanItem.setIcon(android.R.drawable.ic_menu_search);
			scanItem.setTitle(R.string.connect);
		}
		if (mShimmerDevice.getStreamingStatus() == true
				&& mShimmerDevice.getShimmerState() == Shimmer.STATE_CONNECTED) {
		}
		if (mShimmerDevice.getStreamingStatus() == false
				&& mShimmerDevice.getShimmerState() == Shimmer.STATE_CONNECTED
				&& mShimmerDevice.getInstructionStatus() == true) {
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			try {
				if ((mShimmerDevice.getShimmerState() == Shimmer.STATE_CONNECTED)) {
					mShimmerDevice.stop();
					mShimmerDevice = new Shimmer(this, mHandler, "Device 1",
							false);
				} else {
					Intent serverIntent = new Intent(context,
							DeviceListActivity.class);
					startActivityForResult(serverIntent,
							REQUEST_CONNECT_SHIMMER);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		case R.id.activateintrnlaccel:
			onNavigate(130);
			return true;
		case R.id.dvateststart:
			onNavigate(140);
			return true;
		case R.id.dvatestsetting:
			onNavigate(340);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case Shimmer.MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case Shimmer.STATE_CONNECTED:
					/*
					 * mTitle.setText(R.string.title_connected_to);
					 * mTitle.append(mConnectedDeviceName);
					 */break;
				case Shimmer.STATE_CONNECTING:
					// mTitle.setText(R.string.title_connecting);
					break;
				case Shimmer.STATE_NONE:
					// mTitle.setText(R.string.title_not_connected); //finish()
					// is exiting through here and mTitle is null?
					// this also stops streaming
					break;
				case Shimmer.MSG_STATE_FULLY_INITIALIZED: // can now connect
					onNavigate(120);
					break;
				}
				break;

			case Shimmer.MESSAGE_READ:
				if (msg.obj instanceof ObjectCluster) {
					ObjectCluster objectCluster = (ObjectCluster) msg.obj;
					/*
					 * int[] dataArray = new int[0]; double[]
					 * extCalibratedDataArr = new double[0];
					 */
					String[] sensorName = new String[0];
					String units = "";
					String calibratedUnits = "";

					sensorName = new String[7]; // for sensor data values
					/*
					 * dataArray = new int[6]; // RM-to display 6 channels of
					 * acc + // gyr extCalibratedDataArr = new double[6]; // 6
					 * challens of acc + // gyr
					 */
					sensorName[0] = "Accelerometer X";
					sensorName[1] = "Accelerometer Y";
					sensorName[2] = "Accelerometer Z";
					sensorName[3] = "Gyroscope X";
					sensorName[4] = "Gyroscope Y";
					sensorName[5] = "Gyroscope Z";
					sensorName[6] = "Timestamp";

					units = "u12";

					String deviceName = objectCluster.mMyName;
					// load data into the arrs
					int shimChnl;
					for (shimChnl = 6; shimChnl >= 0; shimChnl--) {
						Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster
								.get(sensorName[shimChnl]);
						FormatCluster formatCluster = ((FormatCluster) ObjectCluster
								.returnFormatCluster(ofFormats, "CAL"));
						if (formatCluster != null) {
							// Obtain data for text view
							extCalibratedDataArr[shimChnl] = formatCluster.mData;
							calibratedUnits = formatCluster.mUnits;
							// Obtain data for graph
							dataArray[shimChnl] = (int) ((FormatCluster) ObjectCluster
									.returnFormatCluster(ofFormats, "RAW")).mData;
						}
					}

					int subSamplingCount = 0;
					if (mShimmerDevice.getSamplingRate() > maxNumberofSamplesPerSecond) {
						subSamplingCount = (int) (mShimmerDevice
								.getSamplingRate() / maxNumberofSamplesPerSecond);
						mGraphSubSamplingCount++;
					}
					if (mGraphSubSamplingCount == subSamplingCount) {
						mGraphExtrnl.setDataWithAdjustment(dataArray,
								"Shimmer : " + deviceName, units);
						mGraphSubSamplingCount = 0;
					}
					if (dvaSensors == DVASensors.NONE
							|| dvaSensors == DVASensors.INTERNAL)
						if (dvaSensors == DVASensors.INTERNAL)
							dvaSensors = DVASensors.BOTH;
						else
							dvaSensors = DVASensors.EXTERNAL;
					// store the calibrated data
					// which listener creates the timestamp....the faster
					// one..intrnl
					if (dvaDynaDex >= dvaDexMax)
						// overwrite old data
						dvaDynaDex = 0;
					dvaDynaArr[dvaDynaDex][0] = extCalibratedDataArr[0]; // accx
					dvaDynaArr[dvaDynaDex][1] = extCalibratedDataArr[1]; // accy
					dvaDynaArr[dvaDynaDex][2] = extCalibratedDataArr[2]; // accz
					dvaDynaArr[dvaDynaDex][3] = extCalibratedDataArr[3]; // gyrx
					dvaDynaArr[dvaDynaDex][4] = extCalibratedDataArr[4]; // gyry
					dvaDynaArr[dvaDynaDex][5] = extCalibratedDataArr[5]; // gyrz
					dvaTimeStamp[dvaDynaDex] = System.currentTimeMillis() / 1000;
					dvaDynaArr[dvaDynaDex][6] = extCalibratedDataArr[6]; // ext
																			// sensor
																			// TS
					dvaDynaArr[dvaDynaDex][7] = Double.valueOf(dvaTSFormat
							.format(System.currentTimeMillis()));
					// TS
					// testing for the max gyro values
					if (extCalibratedDataArr[3] > tstGyrxMax)
						tstGyrxMax = extCalibratedDataArr[3];
					if (extCalibratedDataArr[3] < tstGyrxMin)
						tstGyrxMin = extCalibratedDataArr[3];
					if (extCalibratedDataArr[4] > tstGyryMax)
						tstGyryMax = extCalibratedDataArr[4];
					if (extCalibratedDataArr[4] < tstGyryMin)
						tstGyryMin = extCalibratedDataArr[4];
					if (extCalibratedDataArr[5] > tstGyrzMax)
						tstGyrzMax = extCalibratedDataArr[5];
					if (extCalibratedDataArr[5] < tstGyrzMin)
						tstGyrzMin = extCalibratedDataArr[5];

					// send toasts until mGraphExtrnl is working
					if (dDown <= 0)
						dDown = 500;
					dDown -= 1;
					if (dDown <= 0)
						Toast.makeText(
								getContext(),
								"Shimmer value: " + dvaDynaDex + " " + gyrxMax
										+ " " + gyryMax + " " + gyrzMax,
								Toast.LENGTH_SHORT).show();
					// what set of conditions define an active or completed test
					// suite
					if (dvaTesting == DVATesting.ACTIVE && dvaTestCnt != 0)
						DVADyna();
					dvaDynaDex += 1; // incr here in the slower sensor
					break;
				}
			case Shimmer.MESSAGE_ACK_RECEIVED:
				break;
			case Shimmer.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = mShimmerDevice.getBluetoothAddress();
				Toast.makeText(getContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case Shimmer.MESSAGE_TOAST:
				Toast.makeText(getContext(),
						msg.getData().getString(Shimmer.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}; // end of instantiation statement

	private static Context getContext() {
		return DVAActivity.context;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// DVA - some of the requestCode is not used.
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				if (mShimmerDevice == null)
					onNavigate(109);
				// setMessage("\nBluetooth is now enabled");
				Toast.makeText(this, "Bluetooth is now enabled",
						Toast.LENGTH_SHORT).show();
			} else {
				// User did not enable Bluetooth or an error occurred
				Toast.makeText(this, "Bluetooth not enabled\nExiting...",
						Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
		case REQUEST_CONNECT_SHIMMER:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				// DVA - changed per Jong Chern recommendation to configure
				// shimmer prior to connection
				double mSamplingRate = 51.2;
				int mAccelRange = 3; // +/- 6g
				int mGSRRange = 4; // galvanic skin response autorange n/a to
									// DVA
				// int mSetEnabledSensors = 0xC0;
				// boolean mSetupDevice = true;
				boolean mContinousSync = false;
				mShimmerDevice = new Shimmer(this, mHandler, "Device 1",
						mSamplingRate, mAccelRange, mGSRRange,
						SENSOR_ACCELGYRO, mContinousSync);
				// mShimmerDevice.setgetdatainstruction("a");
				mShimmerDevice.connect(address, "default");

			}
			break;
		// RESULT_OK being returned even when mShimmerDevice not
		// connected.
		// Let timer in Shimmer class control duration
		/*
		 * int lpAbort = 8; while (mShimmerDevice.getShimmerState() !=
		 * mShimmerDevice.STATE_CONNECTED && lpAbort > 0) { // delay then check.
		 * flooding the object with requests lpAbort--; long mDelay = 1000; long
		 * mFuture = System.currentTimeMillis() + mDelay; while
		 * (System.currentTimeMillis() < mFuture) ; // delays for mDelay mS if
		 * (mShimmerDevice.getShimmerState() != mShimmerDevice.STATE_CONNECTING)
		 * { if (mShimmerDevice.getShimmerState() == mShimmerDevice.STATE_NONE)
		 * { // Toast.makeText(this,"Device Not Connected - Retry", //
		 * Toast.LENGTH_LONG).show(); break; } } }
		 */
		/*
		 * int lpAbort = 4; long mDelay = 2000; while (lpAbort > 0) { lpAbort--;
		 * long mFuture = System.currentTimeMillis() + mDelay; // delays // for
		 * // mDelay // mS while (System.currentTimeMillis() < mFuture) ; if
		 * (mShimmerDevice.getShimmerState() == mShimmerDevice.STATE_CONNECTED)
		 * { onNavigate(120); onNavigate(121); break; } else
		 * Toast.makeText(this, "Device Not Connected - Retry",
		 * Toast.LENGTH_LONG).show(); }
		 */
		}
	}

	private boolean DVADyna() {
		// This function manages the administration of the Dynamic Testing
		double dvaThresh = 0;
		// compute the loop count based on the device sample rate and
		// triggerPeriod
		int flash = 0;
		double tstGyroTrig1 = 0;
		double tstGyroTrig2 = 0;
		// extCalibratedDataArr[3] is GyroX and [4] is GyroY
		dvaTrigInhibit--;
		switch (dvaTestType) {
		case UP:
			dvaThresh = -gyroTrigger;
			if (extCalibratedDataArr[4] < dvaThresh && trigCapture > 0
					&& dvaTrigInhibit <= 0)
				// test consecutive historic values of the gyro over the trigger
				// period
				for (tstGyroDex = gyroLookback; tstGyroDex > 0; tstGyroDex--) {
					// on rare occasions, can get an array OOB error when the
					// index is < 0
					tstGyroTrig1 = dvaDynaArr[dvaDynaDex - tstGyroDex][4];
					tstGyroTrig2 = dvaDynaArr[dvaDynaDex][4];

					if (dvaDynaArr[dvaDynaDex - tstGyroDex][4] >= dvaThresh) {
						// one wrong value exits the for loop
						flash = 0;
						break;
					} else
						flash = 1;
				}
			//trigger start
			if (flash == 1) {
				setImage();
				trigCapture--;
				dvaTrigInhibit = dvaTrigInhibitor;
				testButtons(1);
				flash = 0;
				/** Shi Zhang modified:Capture data for trigger file*/
				dvaDynaTrgRcrd();
			}
			break;
		case DN:
			dvaThresh = gyroTrigger;
			if (extCalibratedDataArr[4] > dvaThresh && trigCapture > 0
					&& dvaTrigInhibit <= 0)
				// test consecutive historic values of the gyro over the trigger
				// period
				for (tstGyroDex = gyroLookback; tstGyroDex > 0; tstGyroDex--) {
					if (dvaDynaArr[dvaDynaDex - tstGyroDex][4] <= dvaThresh) {
						// one wrong value exits the for loop
						flash = 0;
						break;
					} else
						flash = 1;
				}
			if (flash == 1) {
				setImage();
				trigCapture--;
				dvaTrigInhibit = dvaTrigInhibitor;
				testButtons(1);
				flash = 0;
				/** Shi Zhang modified:Capture data for trigger file*/
				dvaDynaTrgRcrd();
			}
			break;
		case LEFT:
			dvaThresh = gyroTrigger;
			if (extCalibratedDataArr[3] > dvaThresh && trigCapture > 0
					&& dvaTrigInhibit <= 0)
				// test consecutive historic values of the gyro over the trigger
				// period
				for (tstGyroDex = gyroLookback; tstGyroDex > 0; tstGyroDex--) {
					if (dvaDynaArr[dvaDynaDex - tstGyroDex][3] <= dvaThresh) {
						// one wrong value exits the for loop
						flash = 0;
						break;
					} else
						flash = 1;
				}
			if (flash == 1) {
				setImage();
				trigCapture--;
				dvaTrigInhibit = dvaTrigInhibitor;
				testButtons(1);
				flash = 0;
				/** Shi Zhang modified:Capture data for trigger file*/
				dvaDynaTrgRcrd();
			}
			break;
		case RIGHT:
			dvaThresh = -gyroTrigger;
			if (extCalibratedDataArr[3] < dvaThresh && trigCapture > 0
					&& dvaTrigInhibit <= 0)
				// test consecutive historic values of the gyro over the trigger
				// period
				for (tstGyroDex = gyroLookback; tstGyroDex > 0; tstGyroDex--) {
					if (dvaDynaArr[dvaDynaDex - tstGyroDex][3] >= dvaThresh) {
						// one wrong value exits the for loop
						flash = 0;
						break;
					} else
						flash = 1;
				}
			if (flash == 1) {
				setImage();
				trigCapture--;
				dvaTrigInhibit = dvaTrigInhibitor;
				testButtons(1);
				flash = 0;
				/** Shi Zhang modified:Capture data for trigger file*/
				dvaDynaTrgRcrd();
			}
			break;
		default: {
			dvaThresh = 0;

		}
		}
		// begin storing all values to max capacity of the array after the
		// threshold is exceeded
		// to collect test data. tstGyro[] is updated on each call of this
		// function
		// once the movement threshold is exceeded, fill the test array with
		// data until reset.
		return true;
	}

	private void setImage() {
		// randomly set the image for the test
		// int dirRandom = random.nextInt(3);
		int dirRandom = 0;
		dvaTstStrtTime = (System.currentTimeMillis());
		// when .nextInt(3), 3 was never selected so changed to default:
		if (trigCapture >= dvaRetrys) {
			dirRandom = random.nextInt(4);
			dvaPositionHold = dirRandom;
		} else
			dirRandom = dvaPositionHold;
		int imageDuration = 100;
		mImageView = (ImageView) findViewById(R.id.imageView1);
		dvaRetryCnt = (TextView) findViewById(R.id.dvaRetrys);
		mImageView.setVisibility(ImageView.INVISIBLE);
		// select which acuity level of images to use
		String dvaCDn = "c_dn_" + Integer.toString(acuityLvl);
		String dvaCUp = "c_up_" + Integer.toString(acuityLvl);
		String dvaCRight = "c_right_" + Integer.toString(acuityLvl);
		String dvaCLeft = "c_left_" + Integer.toString(acuityLvl);
		switch (dirRandom) {
		case 0:
			if (dvaTestType == DVATestType.STATIC) {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCDn, "drawable", getPackageName()));
				// mImageView.setImageResource(dvaCArr[acuityLvl - 1][0]);
				mImageView.setVisibility(ImageView.VISIBLE);
			} else {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCDn, "drawable", getPackageName()));
				Animation myAni2 = new AlphaAnimation(1.0f, 0.0f);
				myAni2.setFillAfter(true);
				myAni2.setDuration(imageDuration);
				mImageView.startAnimation(myAni2);
				dvaRetryCnt.setText(Integer.toString(trigCapture - 1));
			}
			c_shown = DVATestType.DN;
			break;
		case 1:
			if (dvaTestType == DVATestType.STATIC) {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCLeft, "drawable", this.getPackageName()));
				mImageView.setVisibility(ImageView.VISIBLE);
			} else {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCLeft, "drawable", this.getPackageName()));
				Animation myAni2 = new AlphaAnimation(1.0f, 0.0f);
				myAni2.setFillAfter(true);
				myAni2.setDuration(imageDuration);
				mImageView.startAnimation(myAni2);
				dvaRetryCnt.setText(Integer.toString(trigCapture));
			}
			c_shown = DVATestType.LEFT;
			break;
		case 2:
			if (dvaTestType == DVATestType.STATIC) {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCRight, "drawable", this.getPackageName()));
				mImageView.setVisibility(ImageView.VISIBLE);
			} else {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCRight, "drawable", this.getPackageName()));
				Animation myAni2 = new AlphaAnimation(1.0f, 0.0f);
				myAni2.setFillAfter(true);
				myAni2.setDuration(imageDuration);
				mImageView.startAnimation(myAni2);
				dvaRetryCnt.setText(Integer.toString(trigCapture));
			}
			c_shown = DVATestType.RIGHT;
			break;
		default:
			if (dvaTestType == DVATestType.STATIC) {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCUp, "drawable", this.getPackageName()));
				mImageView.setVisibility(ImageView.VISIBLE);
			} else {
				mImageView.setImageResource(getResources().getIdentifier(
						dvaCUp, "drawable", this.getPackageName()));
				Animation myAni2 = new AlphaAnimation(1.0f, 0.0f);
				myAni2.setFillAfter(true);
				myAni2.setDuration(imageDuration);
				mImageView.startAnimation(myAni2);
				dvaRetryCnt.setText(Integer.toString(trigCapture));
			}
			c_shown = DVATestType.UP;
			break;
		}
	}

	private void DVAStatic() {
		// static mode testing
		setImage();

	}

	private void onDVAStart() {
		// display the DVA1 start view and launch testing
		// setContentView(R.layout.dva_l1); this is set in the stateMac code
		// return true;
	}

	/*
	 * private static boolean onDVADyna() { // called from mHandler // head
	 * movement trigger - at 51.2Hz movement is sampled every 20mS double
	 * angTrig = 60.0; double visibility = 80.00; double dvaSample; // int
	 * viewTrig = 0; int dvaSamples = 2; switch (dvaTestType) { case DN: { //
	 * Dynamic DOWN test: Shimmer y-axis is reading positive value for (int i =
	 * 0; i < dvaSamples; i++) { dvaSample = dvaDataArray[(dvaDADex - i)][7]; if
	 * ( dvaSample < angTrig) break; else { showLandoltC(visibility); return
	 * true; } }
	 * 
	 * return true; } case UP: { return true; } case LEFT: { return true; } case
	 * RIGHT: { return true; } default: return false; }
	 * 
	 * }
	 */

	private static boolean onDVAStatic() {
		// display the DVA2 and start the dynamic test
		return true;
	}

	private static boolean testButtons(int enable) {
		if (enable == 0) {
			dvb29Left.setEnabled(false);
			dvb22Dn.setEnabled(false);
			dvb23Rght.setEnabled(false);
			dvb24Up.setEnabled(false);
		}
		if (enable == 1) {
			dvb29Left.setEnabled(true);
			dvb22Dn.setEnabled(true);
			dvb23Rght.setEnabled(true);
			dvb24Up.setEnabled(true);
		}

		return true;
	}

	// the following code may not be needed....
	private static boolean onCalc() {
		// display the DVAx and compute the test results
		return true;
	}

	private static boolean onDynaUp() {
		// start the DynaUp test
		return true;
	}

	private static boolean onDynaDn() {
		// start the DynaDn test

		return true;
	}

	private static boolean onDynaLeft() {
		// start the DynaLeft test
		return true;
	}

	private static boolean onDynaRight() {
		// start the DynaRight test
		return true;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		/*
		 * sensor data for the internal sensor. these values are in m/s^2 and
		 * are very different from the values presented by the BT adapter
		 * shimmer devices that are both raw, uncalibrated ADC values
		 * (dataArray) and calibrated (extCalibratedDataArr)
		 */
		// if an internal other than the TYPE_ACCELEROMETER, exit
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		if (dvaStateMac == 130) {
			// run this during configuration validation
			int[] dataArray = new int[0];
			String[] sensorName = new String[0];
			sensorName = new String[3]; // for x y and z axis
			dataArray = new int[3];
			sensorName[0] = "AccelerometerX";
			sensorName[1] = "AccelerometerY";
			sensorName[2] = "AccelerometerZ";
			// GraphView only accepts int with a units so convert for
			// verification
			dataArray[0] = (int) (100 * event.values[0]);
			dataArray[1] = (int) (100 * event.values[1]);
			dataArray[2] = (int) (100 * event.values[2]);
			deviceName = "Device 1";
			String units = "i16";
			mGraphIntrnl.setDataWithAdjustment(dataArray, "Intrnl Accel: "
					+ deviceName, units);
		}
		if (dvaStateMac == 210 || dvaStateMac == 160) {
			dvaStaticDex++;
			if (dvaStaticDex >= dvaDexMax)
				// overwrite old data
				dvaStaticDex = 0;
			dvaStaticArr[dvaStaticDex][0] = event.values[0]; // accx
			dvaStaticArr[dvaStaticDex][1] = event.values[1]; // accy
			dvaStaticArr[dvaStaticDex][2] = event.values[2]; // accz
			dvaStaticArr[dvaStaticDex][3] = Double.valueOf(dvaTSFormat
					.format(System.currentTimeMillis()));
			dvaStaticArr[dvaStaticDex][4] = acuityLvl;
			if (c_shown != null) {
				switch (c_shown) {
				case DN:
					dvaStaticArr[dvaStaticDex][5] = 2;
					break;
				case LEFT:
					dvaStaticArr[dvaStaticDex][5] = 3;
					break;
				case RIGHT:
					dvaStaticArr[dvaStaticDex][5] = 1;
					break;
				case UP:
					dvaStaticArr[dvaStaticDex][5] = 0;
					break;
				default:
					break;
				}
			}
			if (c_response != null) {
				switch (c_response) {
				case DN:
					dvaStaticArr[dvaStaticDex][6] = 2;
					break;
				case LEFT:
					dvaStaticArr[dvaStaticDex][6] = 3;
					break;
				case RIGHT:
					dvaStaticArr[dvaStaticDex][6] = 1;
					break;
				case UP:
					dvaStaticArr[dvaStaticDex][6] = 0;
					break;
				default:
					break;
				}
			}
			intrnlDataArr[0] = event.values[0]; // accx
			intrnlDataArr[1] = event.values[1]; // accy
			intrnlDataArr[2] = event.values[2]; // accz
			// dvaTimeStamp[dvaStaticDex] = System.currentTimeMillis();
			// intrnlDataArr[3] = event.values[3];
		}
		// this is no longer used and can be deleted later
		if (dvaSensors == DVASensors.NONE || dvaSensors == DVASensors.EXTERNAL)
			if (dvaSensors == DVASensors.EXTERNAL)
				dvaSensors = DVASensors.BOTH;
			else
				dvaSensors = DVASensors.INTERNAL;
		// test cycle complete, initialize for next run
		if (dvaTestCnt <= 0)
			onNavigate(140);

	}

	private boolean onNavigate(int dvaGo) {
		// onNavigate advance the dvaStateMac to the next state
		// the button listeners always onNavigate to the next state
		if (dvaGo == 110) {
			// this is the start view state of the app. sensor data has not been
			// configured or verified, therefore buttons are initially disabled
			// option menu display issue was the diff between SDK11 and SDK10
			trigCapture = 0; // triggers the capture of test data
			// initialize to preserve progression states. this will change when
			// type is selected.
			dvaTestCnt = dvaDynaTestMax;

			// initialize the state vars
			dvaState = DVAState.DVAInit;
			dvaTesting = DVATesting.INACTIVE;
			dvaSensors = DVASensors.NONE;
			// cyclical data store
			dvaDexMax = 30000;
			dvaStaticArr = new double[dvaDexMax][7];
			dvaDynaArr = new double[dvaDexMax][8];
			dvaTimeStamp = new Long[dvaDexMax];
			DVAActivity.context = getApplicationContext();
			setContentView(R.layout.dva_l1);
			dvb1Static = (Button) findViewById(R.id.dvb1Static);
			dvb2DynaUp = (Button) findViewById(R.id.dvb2DynaUp);
			dvb3DynaDn = (Button) findViewById(R.id.dvb3DynaDn);
			dvb4DynaRght = (Button) findViewById(R.id.dvb4DynaRght);
			dvb5DynaLeft = (Button) findViewById(R.id.dvb5DynaLeft);
			dvb6ComputeDVA = (Button) findViewById(R.id.dvb6ComputeDVA);
			dvb7Quit = (Button) findViewById(R.id.dvb7Quit);
			dvb1Static.setVisibility(View.VISIBLE);
			dvb1Static.setEnabled(false);
			dvb2DynaUp.setVisibility(View.VISIBLE);
			dvb3DynaDn.setVisibility(View.VISIBLE);
			dvb4DynaRght.setVisibility(View.VISIBLE);
			dvb5DynaLeft.setVisibility(View.VISIBLE);
			dvb2DynaUp.setEnabled(false);
			dvb3DynaDn.setEnabled(false);
			dvb4DynaRght.setEnabled(false);
			dvb5DynaLeft.setEnabled(false);
			dvb6ComputeDVA.setVisibility(View.VISIBLE);
			dvb6ComputeDVA.setEnabled(false);
			dvb7Quit.setVisibility(View.VISIBLE);
			dvb7Quit.setEnabled(true);
		}
		if (dvaGo == 120) {
			// Shimmer configuration
			setContentView(R.layout.graphextrnl);
			mGraphExtrnl = (GraphView) findViewById(R.id.graphextrnlobj);
			mValueSensor1 = (TextView) findViewById(R.id.sensorvalue4);
			mValueSensor2 = (TextView) findViewById(R.id.sensorvalue5);
			mValueSensor3 = (TextView) findViewById(R.id.sensorvalue6);
			mTextSensor1 = (TextView) findViewById(R.id.LabelSensor4);
			mTextSensor2 = (TextView) findViewById(R.id.LabelSensor5);
			mTextSensor3 = (TextView) findViewById(R.id.LabelSensor6);
			// Let timer in Shimmer class control duration
			if (mShimmerDevice.getShimmerState() == Shimmer.STATE_CONNECTED) {
				mShimmerDevice.startStreaming();
			}
			mSensorView = "Accelerometer";
			dvaStateMac = 120;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 120");
			return true;
		}
		if (dvaGo == 130) {
			// configure internal accel
			setContentView(R.layout.graphintrnl);
			mGraphIntrnl = (GraphView) findViewById(R.id.graphintrnl);
			dvaStateMac = 130;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 130");
			return true;
		}
		if (dvaGo == 140) {
			// DVA ready to start
			// initialize to preserve progression states. this will change when
			// type is selected.
			dvaTestCnt = dvaDynaTestMax;
			setContentView(R.layout.dva_l1);
			// assuming internal accel is operational. add validation
			/*
			 * if (mShimmerDevice.getStreamingStatus() == true) {
			 * dvb2DynaUp.setEnabled(false); dvb3DynaDn.setEnabled(false);
			 * dvb4DynaRght.setEnabled(false); dvb5DynaLeft.setEnabled(false);
			 * dvb6ComputeDVA.setEnabled(false); }
			 */
			dvb1Static = (Button) findViewById(R.id.dvb1Static);
			dvb2DynaUp = (Button) findViewById(R.id.dvb2DynaUp);
			dvb3DynaDn = (Button) findViewById(R.id.dvb3DynaDn);
			dvb4DynaRght = (Button) findViewById(R.id.dvb4DynaRght);
			dvb5DynaLeft = (Button) findViewById(R.id.dvb5DynaLeft);
			dvb6ComputeDVA = (Button) findViewById(R.id.dvb6ComputeDVA);
			dvb7Quit = (Button) findViewById(R.id.dvb7Quit);
			dvb1Static.setEnabled(true);
			dvb6ComputeDVA.setEnabled(false);
			dvb7Quit.setEnabled(true);
			if (mShimmerDevice.getStreamingStatus() == true) {
				dvb2DynaUp.setEnabled(true);
				dvb3DynaDn.setEnabled(true);
				dvb4DynaRght.setEnabled(true);
				dvb5DynaLeft.setEnabled(true);
			} else {
				dvb2DynaUp.setEnabled(false);
				dvb3DynaDn.setEnabled(false);
				dvb4DynaRght.setEnabled(false);
				dvb5DynaLeft.setEnabled(false);
			}

			dvaStateMac = 140;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 140");
			return true;
		}
		if (dvaGo == 150) {
			// slide 6 layout state - ready to start dyna
			// to test for active view use "this.findViewById(R.id.content)"
			setContentView(R.layout.dva_l2);
			mImageView = (ImageView) findViewById(R.id.imageView1);
			dvaDeviceTV = (TextView) findViewById(R.id.dvDevice);
			if (deviceName != null)
				dvaDeviceTV.setText(deviceName);
			dvb20Static = (Button) findViewById(R.id.dvb20Static);
			dvb28StartNewTest = (Button) findViewById(R.id.dvb28StartNewTest);
			dvb29Left = (Button) findViewById(R.id.dvb29Left);
			dvb22Dn = (Button) findViewById(R.id.dvb22Dn);
			dvb23Rght = (Button) findViewById(R.id.dvb23Rght);
			dvb24Up = (Button) findViewById(R.id.dvb24Up);
			dvb30Return = (Button) findViewById(R.id.dvb30Return);
			dvb31Quit = (Button) findViewById(R.id.dvb31Quit);
			dvb20Static.setVisibility(View.VISIBLE);
			dvb20Static.setEnabled(false);
			dvb28StartNewTest.setVisibility(View.VISIBLE);
			dvb28StartNewTest.setEnabled(true);
			dvb29Left.setVisibility(View.VISIBLE);
			dvb29Left.setEnabled(false);
			dvb22Dn.setVisibility(View.VISIBLE);
			dvb22Dn.setEnabled(false);
			dvb23Rght.setVisibility(View.VISIBLE);
			dvb23Rght.setEnabled(false);
			dvb24Up.setVisibility(View.VISIBLE);
			dvb24Up.setEnabled(false);
			dvb31Quit.setVisibility(View.VISIBLE);
			dvb31Quit.setEnabled(true);
			editText21 = (EditText) findViewById(R.id.editText21);
			RadioGroup dvaRGroup = (RadioGroup) findViewById(R.id.radioGroup1);
			dvaRGroup
					.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							switch (checkedId) {
							case R.id.dvrb60:
								gyroTrigger = 60.0;
								break;
							case R.id.dvrb120:
								gyroTrigger = 120.0;
								break;
							}
						}
					}

					);
			dvaState = DVAState.DVADyna;
			setFileName(dvaState);
			acuityLvl = dvaDynaStrt;
			dvaWrgCnt = 0;
			dvaTestCnt = dvaDynaTestMax;
			dvaStateMac = 150;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 150");
			return true;
		}
		
		if (dvaGo == 160) {
			// Dyna testing started with "Start New Test"
			// records the data at 10ms intervals until canceled by direction
			// selection
			dvb28StartNewTest.setEnabled(false);
			dvb29Left.setEnabled(true);
			dvb22Dn.setEnabled(true);
			dvb23Rght.setEnabled(true);
			dvb24Up.setEnabled(true);
			dvaStateMac = 160;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 160");
		}
		if (dvaGo == 190) {
			dvaStateMac = 190;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 190");
			return true;
		}
		if (dvaGo == 210) {
			// slide 2 layout state - ready to start static
			setContentView(R.layout.dva_l2);
			mImageView = (ImageView) findViewById(R.id.imageView1);
			dvb20Static = (Button) findViewById(R.id.dvb20Static);
			dvb28StartNewTest = (Button) findViewById(R.id.dvb28StartNewTest);
			dvb29Left = (Button) findViewById(R.id.dvb29Left);
			dvb22Dn = (Button) findViewById(R.id.dvb22Dn);
			dvb23Rght = (Button) findViewById(R.id.dvb23Rght);
			dvb24Up = (Button) findViewById(R.id.dvb24Up);
			dvb30Return = (Button) findViewById(R.id.dvb30Return);
			dvb31Quit = (Button) findViewById(R.id.dvb31Quit);
			editText21 = (EditText) findViewById(R.id.editText21);
			dvb20Static.setVisibility(View.VISIBLE);
			dvb20Static.setEnabled(false);
			dvb28StartNewTest.setVisibility(View.VISIBLE);
			dvb28StartNewTest.setEnabled(false);
			dvb29Left.setVisibility(View.VISIBLE);
			dvb29Left.setEnabled(true);
			dvb22Dn.setVisibility(View.VISIBLE);
			dvb22Dn.setEnabled(true);
			dvb23Rght.setVisibility(View.VISIBLE);
			dvb23Rght.setEnabled(true);
			dvb24Up.setVisibility(View.VISIBLE);
			dvb24Up.setEnabled(true);
			dvb31Quit.setVisibility(View.VISIBLE);
			dvb31Quit.setEnabled(true);

			dvaTesting = DVATesting.ACTIVE;
			dvaTestType = DVATestType.STATIC;
			// gen the Landolt C image
			acuityLvl = dvaStaticStrt;
			dvaWrgCnt = 0;
			dvaTestCnt = dvaStaticTestMax;
			setImage();
			dvaState = DVAState.DVAStatic;
			setFileName(dvaState);
			dvaStateMac = 210;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 210");
			return true;
		}
		if (dvaGo == 270) {
			dvaStateMac = 270;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 270");
			return true;
		}
		if (dvaGo == 320) {
			dvaStateMac = 320;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 320");
			return true;
		}
		if (dvaGo == 330) {
			dvaStateMac = 330;
			Log.d(NOTIFICATION_SERVICE, "dvaStateMac = 330");
			return true;
		}
		if(dvaGo ==340) {
			Intent intent = new Intent(this, DVASettingsActivity.class);
			startActivity(intent);
		}

		return true;
	}

	public void dvb28StartNewTest(View view) {
		// onClick listener
		dvaTesting = DVATesting.ACTIVE;
		if (dvaTestType != DVATestType.STATIC)
			trigCapture = dvaRetrys;
		dvaStartTS = Double.valueOf(dvaTSFormat.format(System
				.currentTimeMillis()));
		onNavigate(160);
	}

	public void dvb29Left(View view) {
		// onClick listener
		if (dvaTestType == DVATestType.STATIC) {
			c_response = DVATestType.LEFT;
			dvaStaticRcrd(DVATestType.LEFT);
		} else {
			// dvaDynaBigRcrd();
			c_response = DVATestType.LEFT;
			dvaDynaSmlRcrd(DVATestType.LEFT);
		}
		trigCapture = dvaRetrys;
	}

	public void dvb22Dn(View view) {
		// onClick listener
		if (dvaTestType == DVATestType.STATIC) {
			c_response = DVATestType.DN;
			dvaStaticRcrd(DVATestType.DN);
		} else {
			c_response = DVATestType.DN;
			dvaDynaSmlRcrd(DVATestType.DN);
		}
		trigCapture = dvaRetrys;
	}

	public void dvb23Rght(View view) {
		// onClick listener
		if (dvaTestType == DVATestType.STATIC) {
			c_response = DVATestType.RIGHT;
			dvaStaticRcrd(DVATestType.RIGHT);
		} else {
			c_response = DVATestType.RIGHT;
			dvaDynaSmlRcrd(DVATestType.RIGHT);
		}
		trigCapture = dvaRetrys;
	}

	public void dvb24Up(View view) {
		// onClick listener
		if (dvaTestType == DVATestType.STATIC) {
			c_response = DVATestType.UP;
			dvaStaticRcrd(DVATestType.UP);
		} else {
			c_response = DVATestType.UP;
			dvaDynaSmlRcrd(DVATestType.UP);
		}
		trigCapture = dvaRetrys;
	}

	public void dvb30Return(View view) {
		// onClick listener
		onNavigate(140);
	}

	public void dvb1Static(View view) {
		// onClick listener - static test start
		dvaTestType = DVATestType.STATIC;
		onNavigate(210);
		return;

	}

	public void dvb2DynaUp(View view) {
		// onClick listener - dynaUp test start
		dvaTestType = DVATestType.UP;
		onNavigate(150);
		return;

	}

	public void dvb3DynaDn(View view) {
		// onClick listener - dynaDn test start
		dvaTestType = DVATestType.DN;
		onNavigate(150);
		return;
	}

	public void dvb4DynaRght(View view) {
		// onClick listener - dynaRght test start
		dvaTestType = DVATestType.RIGHT;
		onNavigate(150);
		return;
	}

	public void dvb5DynaLeft(View view) {
		// onClick listener - dynaLeft test start
		dvaTestType = DVATestType.LEFT;
		onNavigate(150);
		return;
	}

	public void dvb6ComputeDVA(View view) {
		// onClick listener
	}

	public void dvb7Quit(View view) {
		// onClick listener
		finish();
	}

	public void setFileName(DVAState dvaState) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		dvaClndr = dvaClndr.getInstance();
		dvaFileName = sdf.format(dvaClndr.getTimeInMillis());
		if (dvaTestType == DVATestType.STATIC)
			dvaFileName += "_DVA_static";
		else
			dvaFileName += "_DVA_dynamic";
		dvaFileName += Double.toString(gyroTrigger);
		editText21.setText(dvaFileName);
		File root = Environment.getExternalStorageDirectory();
		dvaFile = new File(root, ("DVA/" + dvaFileName));
		if (dvaState == DVAState.DVAStatic) {
			try {
				dvaStaticWriter = new BufferedWriter(new FileWriter(
						(dvaFile + ".csv"), true));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				dvaDynaSmlWriter = new BufferedWriter(new FileWriter((dvaFile
						+ "_" + dvaTestType + "Sml" + ".csv"), true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dvaDynaBigWriter = new BufferedWriter(new FileWriter((dvaFile
						+ "_" + dvaTestType + "Big" + ".csv"), true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			/** Shi Zhang modified start (add name writer for trigger file)*/
			
			try {
				dvaDynaTrgWriter = new BufferedWriter(new FileWriter((dvaFile
						+ "_" + dvaTestType + "Trg" + ".csv"), true));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/** modify end*/
		}

	}

	private void dvaStaticRcrd(DVATestType c_response) {
		// record the results of the test
		Long dvaElapse = (System.currentTimeMillis()) - dvaTstStrtTime;
		mImageView.setVisibility(ImageView.INVISIBLE);
		//this conditional is unnecessary and can be removed later
		if (dvaTestType == DVATestType.STATIC) {
			staticFileRcd = Long.toString(dvaElapse) + ",";
			staticFileRcd = staticFileRcd + acuityLvl + ",";
			staticFileRcd = staticFileRcd + c_shown + ",";
			staticFileRcd = staticFileRcd + c_response;
			for (int i = 0; i <= 2; i++)
				staticFileRcd = staticFileRcd + ","
						+ (Double.toString(dvaStaticArr[dvaStaticDex][i]));
			dvaTestCnt--;
			if(!c_shown.equals(c_response))
				dvaWrgCnt ++;
			if (dvaTestCnt > 0 && dvaWrgCnt < 5) {
				// the order of these two commands effects when the image
				// transitions occur.
				if ((dvaTestCnt % dvaTestPerAcuity) == 0)
				{
					acuityLvl--;
					dvaWrgCnt = 0;
				}
				setImage();
				try {
					dvaStaticWriter.write(staticFileRcd);
					dvaStaticWriter.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				// test cycle ended
				dvaCompute();
				try {
					dvaStaticWriter.write(staticFileRcd);
					dvaStaticWriter.newLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					dvaStaticWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				dvaTesting = DVATesting.INACTIVE;
				onNavigate(140);
			}
			return;
		}
	}
	/**Shi Zhang modified the method to write the same to trigger file*/
	private void dvaDynaSmlRcrd(DVATestType c_response) {
		// record the results of the test
		Long dvaElapse = (System.currentTimeMillis()) - dvaTstStrtTime;
		dynaSmlFileRcd = Long.toString(dvaElapse) + ",";
		dynaSmlFileRcd = dynaSmlFileRcd + acuityLvl + ",";
		dynaSmlFileRcd = dynaSmlFileRcd + c_shown + ",";
		dynaSmlFileRcd = dynaSmlFileRcd + c_response;
		// apply special computations to internal and external (see spec)
		// 100ms at approx 40ms sample rate is at least 3 samples average.
		// WARNING: a sample rate change for internal accel changes this
		for (int i = 0; i < 3; i++) {
			dynaSmlFileRcd += ","
					+ Double.toString(((dvaStaticArr[dvaStaticDex][i]
							+ dvaStaticArr[dvaStaticDex - 1][i] + dvaStaticArr[dvaStaticDex - 2][i]) / 3));
		}
		// this algo needs to be replaced with peak head velocity tracking
		// data
		for (int i = 0; i <= 6; i++)
			dynaSmlFileRcd += "," + (Double.toString(extCalibratedDataArr[i]));

		dvaTestCnt--;
		if(!c_shown.equals(c_response))
		{
			dvaWrgCnt ++;
			Log.v("Wrg", dvaWrgCnt+",");
		}
		if (dvaTestCnt > 0 && dvaWrgCnt < 5) {
			// the order of these two commands effects when the image
			// transitions occur.
			if ((dvaTestCnt % dvaTestPerAcuity) == 0)
			{
				acuityLvl--;
				dvaWrgCnt = 0;
			}
			try {
				dvaDynaSmlWriter.write(dynaSmlFileRcd);
				dvaDynaSmlWriter.newLine();
				//trigger file write 
				dvaDynaTrgWriter.write(dynaSmlFileRcd);
				dvaDynaTrgWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// test cycle ended
			dvaCompute();
			try {
				dvaDynaSmlWriter.write(dynaSmlFileRcd);
				dvaDynaSmlWriter.newLine();
				//trigger file write 
				dvaDynaTrgWriter.write(dynaSmlFileRcd);
				dvaDynaTrgWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dvaDynaSmlWriter.close();
				//close trigger file
				dvaDynaTrgWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// launch the big file array transformation and store thread
			storeBigFile();
			// release array memory after test suite completed and file data written
			sBigRcrd = null;
			dBigRcrd = null;
			dvaTesting = DVATesting.INACTIVE;
			onNavigate(140);
		}
		return;

	}
	/**TO DO: generate the trigger file content of trigger data*/
	private void dvaDynaTrgRcrd() {
		// record the results of the test
		Long dvaElapse = (System.currentTimeMillis()) - dvaTstStrtTime;
		dynaTrgFileRcd = Long.toString(dvaElapse) + ",";
		dynaTrgFileRcd += acuityLvl + ",";
		dynaTrgFileRcd += c_shown + ",";
		//Trg shown and chosen is the same currently
		dynaTrgFileRcd += dynaTrgMarker;
		// apply special computations to internal and external (see spec)
		// 100ms at approx 40ms sample rate is at least 3 samples average.
		// WARNING: a sample rate change for internal accel changes this
		for (int i = 0; i < 3; i++) {
			dynaTrgFileRcd += ","
					+ Double.toString(((dvaStaticArr[dvaStaticDex][i]
							+ dvaStaticArr[dvaStaticDex - 1][i] + dvaStaticArr[dvaStaticDex - 2][i]) / 3));
		}
		// this algo needs to be replaced with peak head velocity tracking
		// data
		for (int i = 0; i <= 6; i++)
			dynaTrgFileRcd += "," + (Double.toString(extCalibratedDataArr[i]));
		
		if (dvaTestCnt > 0) {
			try {
				//trigger file write 
				dvaDynaTrgWriter.write(dynaTrgFileRcd);
				dvaDynaTrgWriter.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void storeBigFile() {
		new Thread(new Runnable() {
			public void run() {
				// which arr is larger due to faster sampling rate
				CombineDblArr bigFileArr;
				// compare TS offset or delta
				if ((dvaDynaArr[dvaDynaDex][7] - dvaDynaArr[dvaDynaDex - 1][7]) < (dvaStaticArr[dvaStaticDex][3] - dvaStaticArr[dvaStaticDex - 1][3]))
					bigFileArr = new CombineDblArr(dvaDynaArr, 7, dvaStaticArr,
							3, dvaStartTS, dvaDynaBigWriter);
				else
					bigFileArr = new CombineDblArr(dvaStaticArr, 3, dvaDynaArr,
							7, dvaStartTS, dvaDynaBigWriter);
			}
		}).start();
	}

	final Runnable dvaBigStrng = new Runnable() {
		public void run() {
			// called by the scheduleAtFixedRate() to capture the dyna big file
			// data
			// store the dvaStatic and dvaDynaArr data in a string on timed
			// intervals for later storage
			sBigRcrd[bigRcrdDex] = c_shown.toString();
			dBigRcrd[bigRcrdDex][0] = (double) System.currentTimeMillis();
			dBigRcrd[bigRcrdDex][1] = (double) acuityLvl;
			for (int i = 2; i <= 4; i++)
				dBigRcrd[bigRcrdDex][i] = intrnlDataArr[i - 2];
			for (int i = 5; i <= 11; i++)
				dBigRcrd[bigRcrdDex][i] = extCalibratedDataArr[i - 5];
			bigRcrdDex++;

		}
	};

	private static void dvaCompute() {

	}

	private static void mDelay(int mDelay) {
		long mFuture = System.currentTimeMillis() + mDelay;
		while (System.currentTimeMillis() < mFuture) {
		}
	}

}