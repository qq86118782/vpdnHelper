package com.example.vpdnhelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import util.HttpHelper;
import util.IPUtils;
import util.LteHelper;
import util.SIMCardInfo;
import util.SqlLiteHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView tv;
	public ProgressDialog pBar;
	public TelephonyManager tm;

	private TextView imsiView;
	private TextView cdmaView;
	private TextView evdoView;
	private TextView lteView;
	private TextView apnNameView;
	private TextView ipView;
	private TextView detailView;
	private TextView networkTypeView;
	private Button setBtn;
	private Button pingBtn;
	private Button gaojiBtn;
	private Handler handler;
	private int versionCode;
	private String comment = "";
	private Handler handler1 = new Handler();
	SqlLiteHelper sh;
	public static final String NETWORK_CDMA = "CDMA: Either IS95A or IS95B (2G)";
	public static final String NETWORK_EDGE = "EDGE (2.75G)";
	public static final String NETWORK_GPRS = "GPRS (2.5G)";
	public static final String NETWORK_UMTS = "UMTS (3G)";
	public static final String NETWORK_EVDO_0 = "EVDO revision 0 (3G)";
	public static final String NETWORK_EVDO_A = "EVDO revision A (3G - Transitional)";
	public static final String NETWORK_EVDO_B = "EVDO revision B (3G - Transitional)";
	public static final String NETWORK_1X_RTT = "1xRTT  (2G - Transitional)";
	public static final String NETWORK_HSDPA = "HSDPA (3G - Transitional)";
	public static final String NETWORK_HSUPA = "HSUPA (3G - Transitional)";
	public static final String NETWORK_HSPA = "HSPA (3G - Transitional)";
	public static final String NETWORK_IDEN = "iDen (2G)";
	public static final String NETWORK_LTE = "LTE (4G)";
	public static final String NETWORK_EHRPD = "EHRPD (3G)";
	public static final String NETWORK_HSPAP = "HSPAP (3G)";
	public static final String NETWORK_UNKOWN = "Unknown";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		VersionTask vt = new VersionTask(this);
		vt.execute();
		sh = new SqlLiteHelper(this);

		imsiView = (TextView) findViewById(R.id.imsiView);
		cdmaView = (TextView) findViewById(R.id.cdmaView);
		evdoView = (TextView) findViewById(R.id.evdoView);
		lteView = (TextView) findViewById(R.id.lteView);
		apnNameView = (TextView) findViewById(R.id.apnNameView);
		ipView = (TextView) findViewById(R.id.ipView);
		detailView = (TextView) findViewById(R.id.detailView);
		networkTypeView = (TextView) findViewById(R.id.networkTypeView);
		setBtn = (Button) findViewById(R.id.setBtn);
		pingBtn = (Button) findViewById(R.id.pingBtn);
		gaojiBtn = (Button) findViewById(R.id.gaojiBtn);

		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		// 输入号码信息，报障需要报出该号码
		SIMCardInfo siminfo = new SIMCardInfo(MainActivity.this);

		if (siminfo != null) {
			if (siminfo.getIMSI() != null) {

				imsiView.setText((siminfo.getIMSI() != null ? siminfo.getIMSI()
						: ""));
				sh.addNewImsi(siminfo.getIMSI());

			} else
				imsiView.setText("获取不到");

		}

		setBtn.setOnClickListener(new OnClickListener() {// 弹出系统设置APN页面
			public void onClick(View e) {
				Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
				startActivity(intent);

			}
		});

		pingBtn.setOnClickListener(new OnClickListener() {//
			public void onClick(View e) {
				Intent intent = new Intent();

				intent.setClass(MainActivity.this, PingsActivity.class);

				startActivity(intent);// 无返回值的调用,启动一个明确的activity

			}
		});
		gaojiBtn.setOnClickListener(new OnClickListener() {//
			public void onClick(View e) {
				// 高级
			}
		});

		handler = new Handler();

		Runnable runnable = new Runnable() {
			public void run() {
				ConnectivityManager

				conManager = (ConnectivityManager) MainActivity.this
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				if (conManager != null) {
					NetworkInfo ni = conManager.getActiveNetworkInfo();
					if (ni != null) {
						apnNameView.setText(ni.getExtraInfo());
						if ((!sh.getAllRecords().equals(""))
								&& (apnNameView.getText().toString()
										.equalsIgnoreCase("ctlte")
										|| apnNameView.getText().toString()
												.equalsIgnoreCase("ctnet") || apnNameView
										.getText().toString()
										.equalsIgnoreCase("ctwap") || apnNameView
										.getText().toString()
										.equalsIgnoreCase("#777"))) {
							SubmitIMSITask submitTask = new SubmitIMSITask(
									MainActivity.this);
							submitTask.execute();
						}
					}
				}

				handler.postDelayed(this, 500);

			}
		};

		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 500);

		TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tel.listen(new PhoneStateMonitor(this),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

		// Toast.makeText(this, sh.getAllRecords(), Toast.LENGTH_LONG).show();
		// checkVersion();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 信号检测类
	 * 
	 * @author Administrator
	 * 
	 */
	public class PhoneStateMonitor extends PhoneStateListener {
		Context context;

		public PhoneStateMonitor(Context context1) {
			this.context = context1;

		}

		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);

			/*
			 * signalStrength.isGsm() 是否GSM信号 2G or 3G
			 * signalStrength.getCdmaDbm(); 联通3G 信号强度
			 * signalStrength.getCdmaEcio(); 联通3G 载干比
			 * signalStrength.getEvdoDbm(); 电信3G 信号强度
			 * signalStrength.getEvdoEcio(); 电信3G 载干比
			 * signalStrength.getEvdoSnr(); 电信3G 信噪比
			 * signalStrength.getGsmSignalStrength(); 2G 信号强度
			 * signalStrength.getGsmBitErrorRate(); 2G 误码率
			 * 
			 * 载干比 ，它是指空中模拟电波中的信号与噪声的比值
			 */
			/*
			 * mLabel3G.setText("IsGsm : " + signalStrength.isGsm() +
			 * "\nCDMA Dbm : " + signalStrength.getCdmaDbm() + "Dbm" +
			 * "\nCDMA Ecio : " + signalStrength.getCdmaEcio() + "dB*10" +
			 * "\nEvdo Dbm : " + signalStrength.getEvdoDbm() + "Dbm" +
			 * "\nEvdo Ecio : " + signalStrength.getEvdoEcio() + "dB*10" +
			 * "\nGsm SignalStrength : " + signalStrength.getGsmSignalStrength()
			 * + "\nGsm BitErrorRate : " + signalStrength.getGsmBitErrorRate());
			 */

			String ooo = "";
			LteHelper lh = new LteHelper(signalStrength);

			cdmaView.setText(this.getCdmaLevel(signalStrength) + "级(1弱->4强)");

			evdoView.setText(this.getEvdoLevel(signalStrength) + "级(1弱->4强)");
			if (lh.getLteSignalStrength() != null) {
				String ltes = lh.getLteSignalStrength().toString();
				lteView.setText(ltes + "(63弱->0强)");
			} else {
				lteView.setText("无");
			}
			detailView.setText("1X dBm:" + signalStrength.getCdmaDbm()
					+ ";1X ecio:" + signalStrength.getCdmaEcio()
					+ "\nEVDO dBm:" + signalStrength.getEvdoDbm()
					+ ";EVDO ecio:" + signalStrength.getEvdoEcio()
					+ "\nLTE rsrp:" + lh.getLteRsrp() + "\nLTE rsrq:"
					+ lh.getLteRsrq()

					+ "\nLTE cqi:" + lh.getLteCqi() + "\nLTE rssnr:"
					+ lh.getLteRssnr());
		}

		/*
		 * public void onServiceStateChanged(ServiceState serviceState){
		 * super.onServiceStateChanged(serviceState);
		 */

		/*
		 * ServiceState.STATE_EMERGENCY_ONLY 仅限紧急呼叫
		 * ServiceState.STATE_IN_SERVICE 信号正常 ServiceState.STATE_OUT_OF_SERVICE
		 * 不在服务区 ServiceState.STATE_POWER_OFF 断电
		 */
		/*
		 * switch(serviceState.getState()) { case
		 * ServiceState.STATE_EMERGENCY_ONLY: Log.d("dd",
		 * "3G STATUS : STATE_EMERGENCY_ONLY"); Toast.makeText(context,
		 * "请稍等，只能紧急电话", Toast.LENGTH_SHORT).show(); break; case
		 * ServiceState.STATE_IN_SERVICE: Log.d("dd",
		 * "3G STATUS : STATE_IN_SERVICE"); Toast.makeText(context, "3G 已经连接",
		 * Toast.LENGTH_SHORT).show(); break; case
		 * ServiceState.STATE_OUT_OF_SERVICE: Log.d("dd",
		 * "3G STATUS : STATE_OUT_OF_SERVICE"); Toast.makeText(context,
		 * "3G 不能提供服务", Toast.LENGTH_SHORT).show(); break; case
		 * ServiceState.STATE_POWER_OFF: Log.d("dd",
		 * "3G STATUS : STATE_POWER_OFF"); Toast.makeText(context, "3G 已经关闭",
		 * Toast.LENGTH_SHORT).show(); break; default: break; }
		 * 
		 * 
		 * }
		 */

		public void onDataConnectionStateChanged(int state) {
			/**
			 * 显示APN连接名称 显示IP地址
			 */

			// apnNameView.setText(au.getApnName());
			// apnNameView.setText(au.getCurrentAPN().getName());
			ipView.setText(IPUtils.getIPAddress(true));

			networkTypeView.setText(mapNetworkTypeToName(tm.getNetworkType())
					+ "\n说明:LTE(4G)和EHRPD(3G)使用的是4G VPDN通道，其它是3G VPDN通道");

			if (apnNameView.getText().toString().equalsIgnoreCase("ctlte")
					|| apnNameView.getText().toString()
							.equalsIgnoreCase("ctnet")
					|| apnNameView.getText().toString()
							.equalsIgnoreCase("ctwap")|| apnNameView
							.getText().toString()
							.equalsIgnoreCase("#777")) {

				SubmitIMSITask submitTask = new SubmitIMSITask(
						MainActivity.this);
				submitTask.execute();
			}

		}

		/**
		 * 4信号极强 3信号较强 2信号一般 1信号弱 0信号极弱（无）
		 * 
		 * @param mSignalStrength
		 * @return
		 */
		private int getCdmaLevel(SignalStrength mSignalStrength) { // CDMA信号显示
			final int cdmaDbm = mSignalStrength.getCdmaDbm();
			final int cdmaEcio = mSignalStrength.getCdmaEcio();
			int levelDbm = 0;
			int levelEcio = 0;

			if (cdmaDbm >= -75)
				levelDbm = 4;
			else if (cdmaDbm >= -85)
				levelDbm = 3;
			else if (cdmaDbm >= -95)
				levelDbm = 2;
			else if (cdmaDbm >= -100)
				levelDbm = 1;
			else
				levelDbm = 0;

			// Ec/Io are in dB*10
			if (cdmaEcio >= -90)
				levelEcio = 4;
			else if (cdmaEcio >= -110)
				levelEcio = 3;
			else if (cdmaEcio >= -130)
				levelEcio = 2;
			else if (cdmaEcio >= -150)
				levelEcio = 1;
			else
				levelEcio = 0;

			return (levelDbm < levelEcio) ? levelDbm : levelEcio;
		}

		private int getLteLevel(Object lteV) {
			return (Integer) lteV;
			/*
			 * if(lteV!=null){ int lteValue=Integer.parseInt(lteV.toString());
			 * /* int level=0; if (lteValue > 63) level=0;
			 * 
			 * else if (lteValue >= 12) level=4; else if (lteValue >= 8)
			 * level=3; else if (lteValue >= 5) level=2; else if (lteValue >= 0)
			 * level=1; else level=0;
			 * 
			 * return level;
			 * 
			 * return lteValue;} else return 0;
			 */
		}

		private int getEvdoLevel(SignalStrength mSignalStrength) { // EVDO网络显示
																	// CDMA2000
																	// 3G信号显示（例如电信天翼3G）
			int evdoDbm = mSignalStrength.getEvdoDbm();
			int evdoSnr = mSignalStrength.getEvdoSnr();
			int levelEvdoDbm = 0;
			int levelEvdoSnr = 0;

			if (evdoDbm >= -65)
				levelEvdoDbm = 4;
			else if (evdoDbm >= -75)
				levelEvdoDbm = 3;
			else if (evdoDbm >= -90)
				levelEvdoDbm = 2;
			else if (evdoDbm >= -105)
				levelEvdoDbm = 1;
			else
				levelEvdoDbm = 0;

			if (evdoSnr >= 7)
				levelEvdoSnr = 4;
			else if (evdoSnr >= 5)
				levelEvdoSnr = 3;
			else if (evdoSnr >= 3)
				levelEvdoSnr = 2;
			else if (evdoSnr >= 1)
				levelEvdoSnr = 1;
			else
				levelEvdoSnr = 0;

			return (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
		}
	}

	public static String mapNetworkTypeToName(int networkType) {

		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_CDMA:
			return NETWORK_CDMA;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return NETWORK_EDGE;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			return NETWORK_EDGE;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			return NETWORK_UMTS;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			return NETWORK_EVDO_0;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			return NETWORK_EVDO_A;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			return NETWORK_EVDO_B;
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return NETWORK_1X_RTT;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			return NETWORK_HSDPA;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return NETWORK_HSPA;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			return NETWORK_HSUPA;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return NETWORK_IDEN;
		case TelephonyManager.NETWORK_TYPE_LTE:
			return NETWORK_LTE;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			return NETWORK_EHRPD;
			// case TelephonyManager.NETWORK_TYPE_HSPAP:
			// return NETWORK_HSPAP;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
		default:
			return NETWORK_UNKOWN;
		}
	}

	/*
	 * public void submitRecord() { String imsi=sh.getAllRecords();
	 * if(imsi!=null&&!imsi.equals("")){ String httpUrl =
	 * HttpHelper.getServerPath() + "addImsi.action"; HttpClient httpclient=new
	 * DefaultHttpClient(); HttpPost httppost=new HttpPost(httpUrl);
	 * 
	 * // httppost.addHeader("Content-Type",
	 * "application/x-www-form-urlencoded;");
	 * httppost.addHeader("charset",HTTP.UTF_8); //MultipartEntity mulentity=new
	 * MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE); List
	 * <NameValuePair> params=new ArrayList<NameValuePair>();
	 * 
	 * params.add(new BasicNameValuePair("imsiString",imsi));
	 * 
	 * 
	 * try { httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
	 * 
	 * // mulentity.addPart("sessionid", ((SessionApplication)
	 * this.getApplication()) // .getSessionid())); //
	 * mulentity.addPart("name",new StringBody(nameEt.getText().toString())); //
	 * mulentity.addPart("sex",new
	 * StringBody(sexEt.getText().toString(),HTTP.UTF_8)); //
	 * mulentity.addPart("icomment",new
	 * StringBody(icommentEt.getText().toString())); //
	 * mulentity.addPart("schoolId",new StringBody(schoolId+"")); //
	 * httppost.setEntity(mulentity);
	 * 
	 * 
	 * 
	 * HttpResponse response=httpclient.execute(httppost);
	 * 
	 * if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
	 * //添加成功，删除记录 sh.deleteAll();
	 * 
	 * }else{ //什么都不做
	 * 
	 * } } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } catch (ClientProtocolException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); }}
	 * 
	 * }
	 */

	public int getVerCode() {
		try {
			return MainActivity.this.getApplicationContext()
					.getPackageManager()
					.getPackageInfo("com.example.vpdnhelper", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	void downFile(final String url) {
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				// params[0]代表连接的url
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						File file = new File(
								Environment.getExternalStorageDirectory(),
								"vpdnhelper.apk");
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							// baos.write(buf, 0, ch);
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {

							}

						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}

					down();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.start();

	}

	void down() {
		handler1.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});

	}

	void update() {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File("/sdcard/vpdnHelper.apk")),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/*
	 * public void checkVersion() { try { String httpUrl =
	 * HttpHelper.getServerPath() + "getVersion.action";
	 * 
	 * HttpGet request = new HttpGet(httpUrl); HttpClient client = new
	 * DefaultHttpClient(); HttpResponse response = client.execute(request); if
	 * (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { String
	 * res = EntityUtils.toString(response.getEntity());
	 * 
	 * if (res.length() != 0) { JSONObject json = new JSONObject(res);
	 * 
	 * int serverVersionCode =
	 * Integer.parseInt(json.get("versionCode").toString().trim()); String
	 * comment=(String)json.get("comment"); int myVerCode = getVerCode();
	 * 
	 * if (serverVersionCode > myVerCode) {
	 * 
	 * // /////弹出更新的对话框 Dialog dialog = new
	 * AlertDialog.Builder(MainActivity.this)
	 * .setTitle("系统更新").setMessage("发现新版本,"+comment+",请更新！")// 设置内容
	 * .setPositiveButton("确定",// 设置确定按钮 new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick( DialogInterface dialog, int which) { pBar
	 * = new ProgressDialog( MainActivity.this); pBar.setTitle("正在下载");
	 * pBar.setMessage("请稍候..."+HttpHelper .getServerPath() + "vpdnhelper.apk");
	 * pBar .setProgressStyle(ProgressDialog.STYLE_SPINNER); downFile(HttpHelper
	 * .getServerPath() + "vpdnhelper.apk");
	 * 
	 * }
	 * 
	 * }).setNegativeButton("取消", new DialogInterface.OnClickListener() { public
	 * void onClick( DialogInterface dialog, int whichButton) { //
	 * 点击"取消"按钮之后退出程序
	 * 
	 * } }).create();// 创建 // 显示对话框 dialog.show();
	 * 
	 * }
	 * 
	 * } else { //Toast.makeText(MainActivity.this, "信息错误,请重新输入!", 8000).show();
	 * }
	 * 
	 * } else { // Toast.makeText(MainActivity.this, "系统问题!", 8000).show(); } }
	 * catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (ClientProtocolException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } }
	 */

	class VersionTask extends AsyncTask<Integer, Integer, String> {
		private Context context;

		VersionTask(Context context) {
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {
			//Toast.makeText(context, "开始执行", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected String doInBackground(Integer... params) {
			try {
				String httpUrl = HttpHelper.getServerPath()
						+ "getVersion.action";

				HttpGet request = new HttpGet(httpUrl);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String res = EntityUtils.toString(response.getEntity());

					if (res.length() != 0) {
						JSONObject json = new JSONObject(res);

						versionCode = Integer.parseInt(json.get("versionCode")
								.toString().trim());
						comment = (String) json.get("comment");

					}

				} else {
					// Toast.makeText(MainActivity.this, "信息错误,请重新输入!",
					// 8000).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(String r) {
			int myVerCode = getVerCode();

			if (versionCode > myVerCode) {

				// /////弹出更新的对话框
				Dialog dialog = new AlertDialog.Builder(MainActivity.this)
						.setTitle("系统更新")
						.setMessage("发现新版本," + comment + ",请更新！")
						// 设置内容
						.setPositiveButton("确定",// 设置确定按钮
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										pBar = new ProgressDialog(
												MainActivity.this);
										pBar.setTitle("正在下载");
										pBar.setMessage("请稍候..."
												+ HttpHelper.getServerPath()
												+ "vpdnhelper.apk");
										pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
										downFile(HttpHelper.getServerPath()
												+ "vpdnhelper.apk");

									}

								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// 点击"取消"按钮之后退出程序

									}
								}).create();// 创建
				// 显示对话框
				dialog.show();

			}

			super.onPostExecute(r);
		}

	}

	class SubmitIMSITask extends AsyncTask<Integer, Integer, String> {
		private Context context;

		SubmitIMSITask(Context context) {
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {
		//	Toast.makeText(context, "开始上传", Toast.LENGTH_SHORT).show();
		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected String doInBackground(Integer... params) {
			String imsi = sh.getAllRecords();
			if (imsi != null && !imsi.equals("")) {
				String httpUrl = HttpHelper.getServerPath() + "addImsi.action";
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(httpUrl);

				// httppost.addHeader("Content-Type",
				// "application/x-www-form-urlencoded;");
				httppost.addHeader("charset", HTTP.UTF_8);
				// MultipartEntity mulentity=new
				// MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();

				params1.add(new BasicNameValuePair("imsiString", imsi));

				try {
					httppost.setEntity(new UrlEncodedFormEntity(params1,
							HTTP.UTF_8));

					// mulentity.addPart("sessionid", ((SessionApplication)
					// this.getApplication())
					// .getSessionid()));
					// mulentity.addPart("name",new
					// StringBody(nameEt.getText().toString()));
					// mulentity.addPart("sex",new
					// StringBody(sexEt.getText().toString(),HTTP.UTF_8));
					// mulentity.addPart("icomment",new
					// StringBody(icommentEt.getText().toString()));
					// mulentity.addPart("schoolId",new
					// StringBody(schoolId+""));
					// httppost.setEntity(mulentity);

					HttpResponse response = httpclient.execute(httppost);

					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						// 添加成功，删除记录
						sh.deleteAll();

					} else {
						// 什么都不做

					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(String r) {

			super.onPostExecute(r);
		}

	}
}
