package com.logistical.fd.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logistical.fd.R;


/**
 *
 * @ClassName: com.example.animationloading.LoadingDialog
 * @Description: 动画加载Dialog
 * @author zhaokaiqiang
 * @date 2014-10-27 下午4:42:52
 *
 */
public class LoadingUtil extends Dialog {

    private static String msg = null;
    private static Context context;
    private static LoadingUtil load;
    private static int color = -1;
    private static boolean flag = true;

    public static boolean backCancel = true;//默认可取消


    /**
     * 默认 无提示信息 无背景
     */
    public static void show(Context context){
        load = new LoadingUtil(context);
        dismis();
        load.show();
    }

    /**
     * 有提示信息
     */
    public static void show(Context context,String msg){
        load = new LoadingUtil(context,msg);
        dismis();
        load.show();
    }
    /*
     *有提示信息 有背景
      */
    public static void show(Context context,String msg,int resuse){
        load = new LoadingUtil(context,msg,resuse);
        dismis();
        load.show();
    }

    /**
     *  flag true 无动画  false 有动画 有背景 有文字
     */
    public static void show(Context context,String msg,int resuse,boolean flag){
        load = new LoadingUtil(context,msg,resuse,flag);
        dismis();
        load.show();
        if(!flag){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoadingUtil.dismis();
                }
            },1000);
        }
    }

    /**
     * 无提示信息 有背景
      */
    public static void show(Context context,int resuse){
        load = new LoadingUtil(context,resuse);
        dismis();
        load.show();
    }

    public LoadingUtil(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    public LoadingUtil(Context context,String msg) {
        super(context, R.style.dialog);
        this.context = context;
        this.msg = msg;
    }

    public LoadingUtil(Context context,int resuse) {
        super(context, R.style.dialog);
        this.context = context;
        this.color = resuse;
    }

    public LoadingUtil(Context context,String msg,int resuse) {
        super(context, R.style.dialog);
        this.context = context;
        this.msg = msg;
        this.color = resuse;
    }

    public LoadingUtil(Context context,String msg,int resuse,boolean flag) {
        super(context, R.style.dialog);
        this.context = context;
        this.msg = msg;
        this.color = resuse;
        this.flag = flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(backCancel);//点击外部不消失
        TextView text = (TextView)findViewById(R.id.dialog_text);
        if(!StringUtils.isEmpty(msg)){
            text.setText(msg);
            text.setVisibility(View.VISIBLE);
        }else {
            text.setVisibility(View.GONE);
        }

        if(flag){ //是否显示进度条
            findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
        }else {
            LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_view);
            layout.setGravity(Gravity.BOTTOM);
            findViewById(R.id.loading_progress).setVisibility(View.GONE);
            text.setTextColor(Color.WHITE);
        }

        if(color!=-1){
            findViewById(R.id.dialog_view).setBackgroundResource(color);
        }
    }

    public static void dismis(){
        if(load!=null){
            if(load.isShowing()){
                load.dismiss();
                load = null;
                msg = null;
                color = -1;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!flag){
            // 关闭应用程序
            LoadingUtil.dismis();
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(context.getPackageName());
            AppManager.getAppManager().finishAllActivity();
            System.exit(0);
//            load.setOnKeyListener(new OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                    return true;
//                }
//            });

            return true;
        }else {
            if(backCancel){
                LoadingUtil.dismis();
            }
            return true;
        }
    }

    public static void showAlertDialog(final Context context,String title,String message,String confirmBtn,String cancelBtn, final MyDialogListenner listenner){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(StringUtils.isEmpty(title)?"提示":title);
        builder.setMessage(StringUtils.isEmpty(message)?"Message wait fill":message);
        builder.setPositiveButton(StringUtils.isEmpty(confirmBtn)?"确定":confirmBtn, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(listenner!=null){
                    listenner.callBack(true);
                }
            }
        });
        builder.setNegativeButton(StringUtils.isEmpty(cancelBtn) ? "取消" : cancelBtn, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(listenner!=null){
                    listenner.callBack(false);
                }
            }
        });
        builder.create();
        builder.show();
    }


    public static void showAlertDialog(Context context,MyDialogListenner listenner){
        showAlertDialog(context, null, null, null, null, listenner);
    }

    public static void showAlertDialog(Context context, String message ,MyDialogListenner listenner){
        showAlertDialog(context,null,message,null,null,listenner);
    }

    public static void showAlertDialog(Context context,String title, String message ,MyDialogListenner listenner){
        showAlertDialog(context,title,message,null,null,listenner);
    }

    /**
     * 简单Dialog确定取消回掉接口
     */
    public interface MyDialogListenner{
        /**
         * @param isComfirm true 点击确认     false 取消
         */
        void callBack(boolean isComfirm);
    }


}