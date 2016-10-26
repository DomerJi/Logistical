/*************************************************************************************************
 * 版权所有 (C)2012,  深圳市康佳集团股份有限公司 
 *
 * 文件名称：UpdateService.java
 * 内容摘要：升级服务
 * 当前版本：
 * 作         者： hexiaoming
 * 完成日期：2012-12-24
 * 修改记录：
 * 修改日期：
 * 版   本  号：
 * 修   改  人：
 * 修改内容：
 ************************************************************************************************/
package com.logistical.fd.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.logistical.fd.R;
import com.logistical.fd.utils.FileUtil;
import com.logistical.fd.utils.LogUtil;


/***
 * 升级服务
 *
 * @author hexiaoming
 *
 */
public class UpdateService extends Service {

	public static final String Install_Apk = "Install_Apk";
	/********download progress step*********/
	private static final int down_step_custom = 1;

	private static final int TIMEOUT = 10 * 1000;// 超时
	private static String down_url;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;

	private String app_name;

	private NotificationManager notificationManager;
	private Notification notification;
	private Intent updateIntent;
	private PendingIntent pendingIntent;
	private RemoteViews contentView;


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 方法描述：onStartCommand方法
	 * @param   Intent intent, int flags, int startId
	 * @return    int
	 * @see     UpdateService
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		app_name = intent.getStringExtra("Key_App_Name");
		down_url = intent.getStringExtra("Key_Down_Url");

		// create file,应该在这个地方加一个返回值的判断SD卡是否准备好，文件是否创建成功，等等！
		FileUtil.createFile(app_name);

		if(FileUtil.isCreateFileSucess == true){
			createNotification();
			createThread();
		}else{
			Toast.makeText(this, R.string.insert_card, Toast.LENGTH_SHORT).show();
			/***************stop service************/
			stopSelf();

		}

		return super.onStartCommand(intent, flags, startId);
	}



	/********* update UI******/
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DOWN_OK:

					/*********下载完成，点击安装***********/
					Uri uri = Uri.fromFile(FileUtil.updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri,"application/vnd.android.package-archive");
					pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
					//关闭通知
					notification.flags = Notification.FLAG_AUTO_CANCEL;
					notificationManager.notify(R.layout.notification_item, notification);
					notificationManager.cancel(R.layout.notification_item);

					/*****安装APK******/
					installApk();

					//stopService(updateIntent);
					/***stop service*****/
					stopSelf();
					break;

				case DOWN_ERROR:
					notification.flags = Notification.FLAG_AUTO_CANCEL;

					/***stop service*****/
					//onDestroy();
					stopSelf();
					break;

				default:
					//stopService(updateIntent);
					/******Stop service******/
					//stopService(intentname)
					//stopSelf();
					break;
			}
		}
	};

	private void installApk() {
		// TODO Auto-generated method stub
		/*********下载完成，点击安装***********/
		Uri uri = Uri.fromFile(FileUtil.updateFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);

		/**********加这个属性是因为使用Context的startActivity方法的话，就需要开启一个新的task**********/
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.setDataAndType(uri,"application/vnd.android.package-archive");
		UpdateService.this.startActivity(intent);
	}

	/**
	 * 方法描述：createThread方法, 开线程下载
	 * @param
	 * @return
	 * @see     UpdateService
	 */
	public void createThread() {
		new DownLoadThread().start();
	}


	private class DownLoadThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message message = new Message();
			try {
				long downloadSize = downloadUpdateFile(down_url,FileUtil.updateFile.toString());
				if (downloadSize > 0) {
					LogUtil.e("DOWN_OK", "__________________++++++++++++++++++++++++++++++++++++=");
					// down success										
					message.what = DOWN_OK;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}
		}
	}



	/**
	 * 方法描述：createNotification方法
	 * @param
	 * @return
	 * @see     UpdateService
	 */
	public void createNotification() {

		//notification = new Notification(R.drawable.dot_enable,app_name + getString(R.string.is_downing) ,System.currentTimeMillis());


		 notification = new Notification.Builder(UpdateService.this)
				.setAutoCancel(true)
				.setContentTitle("title")
				.setContentText(app_name + getString(R.string.is_downing))
				.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setWhen(System.currentTimeMillis())
				.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		//notification.flags = Notification.FLAG_AUTO_CANCEL;

		/*** 自定义  Notification 的显示****/
		contentView = new RemoteViews(getPackageName(),R.layout.notification_item);
		contentView.setTextViewText(R.id.notificationTitle, app_name + getString(R.string.is_downing));
		contentView.setTextViewText(R.id.notificationPercent, "0%");
		contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
		notification.contentView = contentView;

//		updateIntent = new Intent(this, AboutActivity.class);
//		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		//updateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
//		notification.contentIntent = pendingIntent;

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(R.layout.notification_item, notification);
	}

	/***
	 * down file
	 *
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)throws Exception {

		int down_step = down_step_custom;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小

		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();

		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
			//这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,
			//所以不用处理						
		}

		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉

		byte buffer[] = new byte[1024];
		int readsize = 0;

		while ((readsize = inputStream.read(buffer)) != -1) {

//			/*********如果下载过程中出现错误，就弹出错误提示，并且把notificationManager取消*********/
//			if (httpURLConnection.getResponseCode() == 404) {
//				notificationManager.cancel(R.layout.notification_item);
//				throw new Exception("fail!");
//				//这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,
//				//所以不用处理						
//			}

			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 时时获取下载到的大小
			int i = (int) (((float) downloadCount / totalSize) * 100);
			/*** 每次增张3%**/

			if (System.currentTimeMillis() - temp>1000) {
				temp=System.currentTimeMillis();
				// 改变通知栏
				contentView.setTextViewText(R.id.notificationPercent,i + "%");
				contentView.setProgressBar(R.id.notificationProgress, 100,i, false);
				notification.contentView = contentView;
				notificationManager.notify(R.layout.notification_item, notification);
			}
		}

		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();
		LogUtil.e("DOWN_OK" + downloadCount);
		return downloadCount;
	}
	long temp = 0;
}