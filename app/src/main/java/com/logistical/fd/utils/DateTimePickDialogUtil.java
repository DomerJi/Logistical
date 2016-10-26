package com.logistical.fd.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.logistical.fd.R;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author
 */
public class DateTimePickDialogUtil  {
	private DatePicker datePicker;
	//private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	public Long d = null;
	private int day=-1;

	/**
	 * 时间选择完成 回掉接口
	 */
	private DateCompleteFace dateCompleteFace;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;
	}



	public void init(DatePicker datePicker) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(initDateTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(day>0){
			calendar.setTime(new Date(System.currentTimeMillis()+(day*60*60*1000*24)));
			dateTime = sdf.format(calendar.getTime());
		}else{
			dateTime = sdf.format(calendar.getTime());
		}

		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "-"
					+ calendar.get(Calendar.MONTH) + "-"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "- ";
					/*+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);*/
		}


		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if (day > 0) {
					Calendar mCalendar = Calendar.getInstance();
					mCalendar.setTime(new Date(System.currentTimeMillis() + (day * 60 * 60 * 1000 * 24)));
					// 获得日历实例
					Calendar calendar = Calendar.getInstance();
					calendar.set(view.getYear(), view.getMonth(),
							view.getDayOfMonth());
					if (System.currentTimeMillis() + (day * 60 * 60 * 1000 * 24) > calendar.getTimeInMillis()) {
						view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
						dateTime = sdf.format(mCalendar.getTime());
						ad.setTitle(dateTime);
					} else {
						// 获得日历实例
						dateTime = sdf.format(calendar.getTime());
						ad.setTitle(dateTime);
					}
				} else {

					if (isDateAfter(view)) {
						Calendar mCalendar = Calendar.getInstance();
						view.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
						dateTime = sdf.format(mCalendar.getTime());
						ad.setTitle(dateTime);
					} else {
						// 获得日历实例
						Calendar calendar = Calendar.getInstance();
						calendar.set(view.getYear(), view.getMonth(),
								view.getDayOfMonth());
						dateTime = sdf.format(calendar.getTime());
						ad.setTitle(dateTime);
					}

				}

			}


			private boolean isDateAfter(DatePicker tempView) {

				Calendar mCalendar = Calendar.getInstance();

				Calendar tempCalendar = Calendar.getInstance();
				tempCalendar.set(tempView.getYear(), tempView.getMonth(), tempView.getDayOfMonth(), 0, 0, 0);
				if (tempCalendar.after(mCalendar))
					return true;
				else
					return false;

			}
		});
	}

	/**
	 * 弹出日期时间选择框方法
	 * 当前时间的下一天
	 *
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final TextView inputDate,int day) {
		this.day = day;

		LinearLayout dateTimeLayout = null;
			dateTimeLayout = (LinearLayout) activity
					.getLayoutInflater().inflate(R.layout.common_datetime, null);
			datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		//datePicker.setSpinnersShown(true);
		//timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker);
		//timePicker.setIs24HourView(true);
		//timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity)
				.setTitle(initDateTime)
				.setView(dateTimeLayout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						Long diff = null;
						try {
							Date d1 = df.parse(TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE));
							Date d2 = df.parse(dateTime); //前的时间
							diff = d1.getTime() - d2.getTime();   //两时间差
							d = d1.getTime();
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if (diff >= 0) {
							inputDate.setText(dateTime);
							if (dateCompleteFace != null) {
								dateCompleteFace.dateCompleteListener(true);
							}

						} else {
							try {
								Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
							ToastUtil.show(activity, "请选择小于现在的日期");
						}

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (dateCompleteFace != null) {
							dateCompleteFace.dateCompleteListener(false);
						}
						try {
							Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, true);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}).show();

		ad.setTitle(dateTime);
		return ad;
	}


	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();
		String[] date = initDateTime.split("-");

		int currentYear = Integer.valueOf(date[0]).intValue();
		int currentMonth = Integer.valueOf(date[1]).intValue() - 1;
		int currentDay = Integer.valueOf(date[2]).intValue();

		calendar.set(currentYear, currentMonth, currentDay);
		return calendar;
	}

	/**
	 * 时间 选择回掉监听
	 */
	public interface DateCompleteFace{
		void dateCompleteListener(boolean flag);
	}

	/**
	 * 设置时间回掉监听
	 * @param dateCompleteFace
	 */
	public void setDateCompleteFace(DateCompleteFace dateCompleteFace){
		this.dateCompleteFace = dateCompleteFace;
	}


	/**
	 * 时间戳转换成字符串
	 * @param l
	 */
	public static String getStrTime(long l) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date(l));
		return time;
	}

	public static String getStrTiemL(long l){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(l));
	}

	/**
	 * 字符串转时间戳
	 * @param user_time
	 * @return
	 */
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		try {
			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
        // TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re_time;
	}
}
