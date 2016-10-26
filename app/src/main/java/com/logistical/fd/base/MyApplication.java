package com.logistical.fd.base;

import android.app.Application;
import android.content.Context;


import com.litesuits.orm.LiteOrm;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.SDCardUtils;
import com.lzy.okhttputils.OkHttpUtils;


/**
 * Created by admin on 2016/7/3.
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    public LiteOrm liteOrm;

    public String DB_NAME;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //必须调用初始化
        OkHttpUtils.init(this);
        //以下都不是必须的，根据需要自行选择
        OkHttpUtils.getInstance()//
                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(15000)               //全局的连接超时时间
                .setReadTimeOut(15000)                  //全局的读取超时时间
                .setWriteTimeOut(15000);                 //全局的写入超时时间

        String  phonePicsPath;
        if(SDCardUtils.isSDCardEnable()){
            phonePicsPath = SDCardUtils.getSDCardPath();
        }else{
            phonePicsPath = getFilesDir().getAbsolutePath();
        }

        DB_NAME = phonePicsPath+"wl.db";
        LogUtil.e("DB_NAME",DB_NAME);
        liteOrm = LiteOrm.newCascadeInstance(instance, DB_NAME);
        liteOrm.setDebugged(true);

        PreferencesUtils.putString(getAppContext(), PreferencesUtils.key_current_user_phone, "0");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    public static Context getAppContext() {
        return instance;
    }


}
