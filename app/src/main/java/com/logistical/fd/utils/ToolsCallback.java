package com.logistical.fd.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by admin on 2016/4/27.
 */
public abstract class ToolsCallback<T> extends AbsCallback<T>{


    public ToolsCallback(Context activity) {
        LoadingUtil.show(activity,"正在加载");
    }

    public ToolsCallback(Context activity,String msg) {
        LoadingUtil.show(activity,msg);
    }

    @Override
    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onError(isFromCache, call, response, e);
        ToastUtil.show(OkHttpUtils.getContext(), "Error");
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable T t, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onAfter(isFromCache, t, call, response, e);
        //网络请求结束后关闭对话框
        LoadingUtil.dismis();
    }
}
