package com.logistical.fd.utils;

import android.content.Context;

/**
 * Created by admin on 2016/9/20.
 */
public class TotalUtil {

    public static int getOffLineTotal(String type,Context context){
        return PreferencesUtils.getInt(context,TimeUtils.getTime(System.currentTimeMillis(),TimeUtils.DATE_FORMAT_DATE)+"_"+type,0);
    }

    public static void setOffLineTotal(String type,Context context){
        PreferencesUtils.putInt(context,
                TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE) + "_" + type,
                getOffLineTotal(type,context)+1);

    }

}
