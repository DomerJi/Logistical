package com.logistical.fd.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;


import com.logistical.fd.utils.AppManager;
import com.logistical.fd.utils.ToastUtil;

import java.lang.ref.WeakReference;


public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // setContentView(android.R.layout.demo);
        initCreate(savedInstanceState);
        //初始化控件
        initView();
        //将 Activity 添加到栈
        AppManager.addActivity(this);
    }


    /**
     * 设置布局文件
     * @param savedInstanceState
     */
    protected abstract void initCreate(Bundle savedInstanceState);

    /**
     * 初始化控件
     */
    public abstract void initView();
}
