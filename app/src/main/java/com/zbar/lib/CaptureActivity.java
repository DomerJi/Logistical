package com.zbar.lib;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.logistical.fd.R;
import com.logistical.fd.activity.DifficultActivity;
import com.logistical.fd.activity.ScanStatusActivity;
import com.logistical.fd.activity.SelectCompanyActivity;
import com.logistical.fd.base.MyApplication;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.ComPanyBean;
import com.logistical.fd.bean.LocalOrdersnBean;
import com.logistical.fd.notify.BaseObserverActivity;
import com.logistical.fd.notify.EventType;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.TimeUtils;
import com.logistical.fd.utils.ToastUtil;
import com.logistical.fd.utils.ToolsCallback;
import com.logistical.fd.utils.TotalUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者: 陈涛(1076559197@qq.com)
 * 
 * 时间: 2014年5月9日 下午12:25:31
 * 
 * 版本: V_1.0.0
 * 
 * 描述: 扫描界面
 */
public class CaptureActivity extends BaseObserverActivity implements Callback,View.OnClickListener {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;

	private int scrrenLight = 0;
	private boolean isNet;//是否联网
	private boolean isSd = true;// 是否手动 true否 false是
	private TextView noScanAdd;
	private EditText noScanSearch;

	private int screenLight = 200;//初始化 亮度信息

	private int currentStatus = 0;//当前扫描类型
	private LinearLayout companybj;
	private TextView companymsg;
	private ComPanyBean comPanyBean;
	private MyApplication app;
	private TextView companyTotal;


	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}


	@Override
	protected void initCreate(Bundle savedInstanceState) {
		isNet = NetworkUtils.isNetworkAvailable(this);
		isSd = getIntent().getBooleanExtra("IsSd", true);//是否 手工录入

		currentStatus = getScanTatus();
		if(isNet&&isSd){
			newTrue();
		}else {
			newFalse();
		}

		showCompany();//初始化快递公司

		ToastUtil.show(this,"录入状态:"+currentStatus);
	}

	private void getTotal(final String type){
		if(comPanyBean==null)return;
		MyApplication myApplication = (MyApplication) getApplication();
		final LiteOrm  liteOrm = myApplication.liteOrm;
		PostRequest request = OkHttpUtils.post(NetPath.COUNT_TOTAL)
				.params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""))
				.params("companyid",comPanyBean.getId())
				.params("type", type);
		request.execute(new AbsCallback<Object>() {
			@Override
			public Object parseNetworkResponse(Response response) throws Exception {
				return null;
			}

			@Override
			public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
				try {
					String s = response.body().string();
					JSONObject jsonobj = new JSONObject(s);
					LogUtil.e(CaptureActivity.class.getSimpleName(), s);
					if(LoginUtils.isLogin(CaptureActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))) {
							String total = jsonobj.optString("total", "0");
							int ont = Integer.parseInt(total);
						List<LocalOrdersnBean> list = liteOrm.query(new QueryBuilder<LocalOrdersnBean>(LocalOrdersnBean.class)
								.where("type" + " = ? and" + " yyyyMMdd = ?", new String[]{(Integer.parseInt(type) + 1) + "", TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE)}));

						companyTotal.setText("今天已扫描:"+(ont+list.size())+"件（含离线"+list.size()+"件）");

					}else {
						ToastUtil.show(CaptureActivity.this, jsonobj.optString("reason"));
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 当前录入类型状态
	 * @return
	 */
	private int getScanTatus(){
		return getIntent().getIntExtra(KeyValues.SCAN_STASTUS,0);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		isNet = NetworkUtils.isNetworkAvailable(this);
		isSd = intent.getBooleanExtra("IsSd", true);//是否 手工录入

		if(isNet&&isSd){
			newTrue();
		}else {
			newFalse();
		}
	}

	private void newFalse(){
		setContentView(R.layout.activity_no_scan);
		if(isNet){//手动录入 连接网络时 设置的提示信息
			TextView textView = (TextView) findViewById(R.id.noscan_sd_msg);
			textView.setText(getString(R.string.sd_luru_msg_showsd));
		}else {

		}

		// 初始化 右侧设置按钮
		setRightIcon(R.drawable.setup_scan);
		setRightOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CaptureActivity.this, SelectCompanyActivity.class));
			}
		});

		noScanAdd = (TextView) findViewById(R.id.no_scan_add);//添加
		noScanAdd.setOnClickListener(this);
		noScanSearch = (EditText) findViewById(R.id.no_scan_search);//快递单号
		final TextView clear = (TextView) findViewById(R.id.no_scan_ordersn_clear);
		noScanSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				clear.setVisibility(View.VISIBLE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(noScanSearch.getText().toString().trim().length()>0){
					clear.setVisibility(View.INVISIBLE);
				}
			}
		});
		setCenterTitle("手动录入");
	}

	private void newTrue(){
		setContentView(R.layout.activity_qr_scan);
		PercentRelativeLayout capturecontainter = (PercentRelativeLayout) findViewById(R.id.capture_containter);

		companyTotal = (TextView) findViewById(R.id.company_total);


		setCenterTitle("扫描");
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		// 初始化 右侧设置按钮
		setRightIcon(R.drawable.setup_scan);
		setRightOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CaptureActivity.this,SelectCompanyActivity.class));
			}
		});

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
		scrrenLight = getScreenBrightness();
		setScreenBrightness(screenLight);

		TextView sdLuru = (TextView) findViewById(R.id.sd_luru);
		sdLuru.setOnClickListener(this);
	}

	/**
	 * 显示当前选择的快递公司
	 */
	private void showCompany(){
		int current = PreferencesUtils.getInt(CaptureActivity.this,PreferencesUtils.key_current_company_int,-1);
		if(current!=-1){
			String s = PreferencesUtils.getString(CaptureActivity.this,PreferencesUtils.key_current_company_obj,"");
			if(s.length()>0){
				comPanyBean = new Gson().fromJson(s, ComPanyBean.class);
				if(comPanyBean!=null){
					companybj = (LinearLayout) findViewById(R.id.company_bj);
					companymsg = (TextView) findViewById(R.id.company_msg);
					if (companybj!=null&&companymsg!=null){
						companymsg.setText(comPanyBean.getName());
						companybj.setVisibility(View.VISIBLE);

						//收件/寄件数量跟踪
						if(currentStatus==KeyValues.SCAN_IN_S){
							getTotal("0");
						}else if(currentStatus==KeyValues.SCAN_IN_J){
							getTotal("1");
						}
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sd_luru: //手动录入
				myOnPause();
				isSd = false;
				if(currentStatus==KeyValues.SCAN_IN_Y){
					startActivity(new Intent(CaptureActivity.this, DifficultActivity.class));
				}else {
					newFalse();
				}
				break;

			case R.id.no_scan_add://添加
				String searchKey = noScanSearch.getText().toString().trim();
				if(searchKey.length()>0){

					handleDecode(searchKey);

				}else {
					ToastUtil.show(CaptureActivity.this, "请输入快递单号！");
				}

				break;
		}
	}

	/**
	 * 保存当前的屏幕亮度值，并使之生效
	 */
	private void setScreenBrightness(int paramInt){
		Window localWindow = getWindow();
		WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
		float f = paramInt / 255.0F;
		localLayoutParams.screenBrightness = f;
		localWindow.setAttributes(localLayoutParams);
	}


	/**
	 * 获得当前屏幕亮度值 0--255
	 */
	private int getScreenBrightness(){
		int screenBrightness=screenLight;
		try{
			screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		}
		catch (Exception localException){

		}
		return screenBrightness;
	}

	@Override
	public void initView() {

	}

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if(isNet&&isSd){
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			if (hasSurface) {
				initCamera(surfaceHolder);
			} else {
				surfaceHolder.addCallback(this);
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
			playBeep = true;
			AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				playBeep = false;
			}
			initBeepSound();
			vibrate = true;
			//收件/寄件数量跟踪
			if(currentStatus==KeyValues.SCAN_IN_S){
				getTotal("0");
			}else if(currentStatus==KeyValues.SCAN_IN_J){
				getTotal("1");
			}
		}else {

		}

	}

	@Override
	protected void onPause() {
		super.onPause();

		myOnPause();

	}

	/**
	 * 保存亮度 释放资源
	 */
	private void myOnPause(){
		if(isNet&&isSd){
			if (handler != null) {
				handler.quitSynchronously();
				handler = null;
			}
			setScreenBrightness(scrrenLight);
			CameraManager.get().closeDriver();
		}else {
		}
	}

	@Override
	protected void onDestroy() {

		if(isNet&&isSd){
			inactivityTimer.shutdown();
		}else {
		}

		super.onDestroy();
	}

	@Override
	protected void onChange(String eventType) {
		if(eventType==EventType.UPDATE_COMPANY){
			showCompany();
		}
	}

	@Override
	protected String[] getObserverEventType() {
		return new String[]{EventType.UPDATE_COMPANY};
	}

	public void handleDecode(final String result) {
		if(isNet&&isSd){
			inactivityTimer.onActivity();
			playBeepSoundAndVibrate();
			request(result);//请求网络
		}else {
			if(isNet){
				request(result);//请求网络
			}else {
				local(result);//本地录入
			}

		}



	}

	private void local(String result) {
		if(-1 == PreferencesUtils.getInt(CaptureActivity.this,PreferencesUtils.key_current_company_int,-1)){
			startActivity(new Intent(CaptureActivity.this, ScanStatusActivity.class)
					.putExtra("Status", 1)
					.putExtra(PreferencesUtils.key_current_scan,currentStatus)
					.putExtra(PreferencesUtils.key_ordersn,result));
		}else {

			MyApplication app = (MyApplication) MyApplication.getAppContext();
			LocalOrdersnBean localOrder = new LocalOrdersnBean();
			localOrder.ordersn = result;
			localOrder.type = currentStatus;
			localOrder.companyid = comPanyBean.getId();
			localOrder.only_id = result + comPanyBean.getId() + currentStatus;
			localOrder.yyyyMMdd = TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE);
			app.liteOrm.save(localOrder);

			LoadingUtil.showAlertDialog(this, "本地录入成功", new LoadingUtil.MyDialogListenner() {
				@Override
				public void callBack(boolean isComfirm) {
					if (isComfirm) {

					}
				}
			});
		}

	}

	private void request(final String result){
		if(-1 == PreferencesUtils.getInt(CaptureActivity.this,PreferencesUtils.key_current_company_int,-1)
				&&currentStatus!=KeyValues.SCAN_IN_Y){
			startActivity(new Intent(CaptureActivity.this, ScanStatusActivity.class)
					.putExtra("Status", 1)
			.putExtra(PreferencesUtils.key_current_scan,currentStatus)
			.putExtra(PreferencesUtils.key_ordersn,result));
			return;
		}

		PostRequest request = null;

		switch (currentStatus){
			case KeyValues.SCAN_IN_J://寄件
				request = OkHttpUtils.post(NetPath.SCAN)
				.params("type","1");
				break;
			case KeyValues.SCAN_IN_S://收件
				request = OkHttpUtils.post(NetPath.SCAN)
				.params("type", "0");
				break;
			case KeyValues.SCAN_IN_PICKUP://取件
				request = OkHttpUtils.post(NetPath.PICKUP);
				break;
			case KeyValues.SCAN_IN_Y://疑难件
				startActivity(new Intent(CaptureActivity.this, DifficultActivity.class)
				.putExtra(KeyValues.ORDERSN,result));
				// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
				handler.sendEmptyMessage(R.id.restart_preview);
				return;
			default:
				break;
		}

		request.params("token",PreferencesUtils.getString(this,PreferencesUtils.key_token,""))
		.params("ordersn", result);
		if(comPanyBean!=null){
			request.params("companyid", comPanyBean.getId());
		}

		request.execute(new ToolsCallback<Object>(this,"正在录入") {
			@Override
			public Object parseNetworkResponse(Response response) {
				return null;
			}

			@Override
			public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
				super.onError(isFromCache, call, response, e);
				startActivity(new Intent(CaptureActivity.this, ScanStatusActivity.class)
						.putExtra("Status", 3)
						.putExtra(PreferencesUtils.key_ordersn, result));
				// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
				if(handler!=null){
					handler.sendEmptyMessage(R.id.restart_preview);
				}
			}

			@Override
			public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
				try {
					String s = response.body().string();
					JSONObject jsonobj = new JSONObject(s);
					LogUtil.e("ORDERID",s);
					LogUtil.e(CaptureActivity.class.getSimpleName(), s);
					if(LoginUtils.isLogin(CaptureActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))) {

						startActivity(new Intent(CaptureActivity.this, ScanStatusActivity.class)
								.putExtra("Status", 2)
								.putExtra(PreferencesUtils.key_current_scan, currentStatus)
								.putExtra(PreferencesUtils.key_ordersn, result)
								.putExtra("orderid",jsonobj.optString("order_id")));
					}else {
						ToastUtil.show(CaptureActivity.this, jsonobj.optString("reason"));
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}finally {
					// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
					if(handler!=null){
						handler.sendEmptyMessage(R.id.restart_preview);
					}
				}

			}
		});



	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();
			;
			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();



			setX(width/20*3);
			setY(height/20*3);
			setCropWidth(width/10*7);//自定义识别区域
			setCropHeight(height/10*7);//自定义识别区域
			// 设置是否需要截图
			setNeedCapture(true);
			

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}
	private boolean flag1 = true;
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}