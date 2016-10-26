package com.logistical.fd.fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.Gson;
import com.logistical.fd.activity.TrackDetailActivity;
import com.logistical.fd.base.BaseFragment;
import com.logistical.fd.bean.TrackOrderBean;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LiFei on 2016/8/8.
 */
public class SendFragment extends BaseFragment {
    private int page = 1;
    @Override
    protected void initData(final int isLoad) {
        page = (isLoad == 1) ? 1 : page + 1;
        PostRequest postRequest = OkHttpUtils.post(NetPath.SEARCH)
                .params("ordersn", "")  //快递单号
                .params("token", PreferencesUtils.getString(getActivity(), PreferencesUtils.key_token))  //登录token
                .params("page", page + "")
                .params("type", 1 + "");   //类型：0-收件  1-寄件
        postRequest.execute(new AbsCallback<Object>() {
            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
                String s;
                JSONObject json = null;
                try {
                    s = response.body().string();
                    json = new JSONObject(s);
                    if(LoginUtils.isLogin(getActivity(),json.optInt(KeyValues.BACK_CODE))){
                        if(isLoad==1){
                            list.clear();
                            list.addAll(new Gson().fromJson(s, TrackOrderBean.class).getList());
                        }else{
                            list.addAll(new Gson().fromJson(s, TrackOrderBean.class).getList());
                            if(json.getJSONArray("list").length()==0){
                                ToastUtil.show(getActivity(),"没有数据啦");
                                stopRefresh();
                            }
                        }

                    }else{
                        ToastUtil.show(getActivity(), json.getString("reason"));
                    }
                    mAdapter.notifyDataSetChanged();
                    stopRefresh();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLoadMore() {
        initData(2);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 5000);
    }

    @Override
    public void onRefresh() {
        initData(1);
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(false);
            }
        }, 3000);
    }

    @Override
    public void onItemClick(View view, int postion) {
        LogUtil.i("L1", list.get(postion).getOrdersn());
        Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
        intent.putExtra("order_id", list.get(postion).getId());
        startActivity(intent);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            stopRefresh();
        }
        super.onHiddenChanged(hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        stopRefresh();
        super.onPause();
    }


}
