package com.logistical.fd.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 2016/7/3.
 * 全局只有一个Toast 解决连续不断弹出Toast
 * 有正式 和 测试 模式 MyApplication里设置 debug 为false 测试模式则不显示
 */
public class ToastUtil {

    private static Toast toast;

    public static boolean debug = false;

    /**
     * 不可关闭
     * @param context
     * @param message
     */
    public static void show(Context context, String message){
        if(toast == null){
            toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        }else {
            toast.setText(message);
        }
       toast.show();
    }

    /**
     * 可关闭
     * @param context
     * @param message
     */
    public static void showDebug(Context context, String message){
        if(!debug)
            return;
        if(toast == null){
            toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        }else {
            toast.setText(message);
        }
        toast.show();
    }

}
