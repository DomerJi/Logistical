package com.logistical.fd.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.logistical.fd.R;
import com.logistical.fd.adapter.NodeProgressAdapter;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.LogisticsData;
import com.logistical.fd.bean.OrderInfoBean;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.TimeUtils;
import com.logistical.fd.utils.ToastUtil;
import com.logistical.fd.widget.view.NodeProgressView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LiFei on 2016/8/9.
 */
public class TrackDetailActivity extends ToolBarActivity {
    private List<LogisticsData> logisticsDatas = new ArrayList<LogisticsData>();  //数据
    private List<OrderInfoBean.ListBean> mList = new ArrayList<OrderInfoBean.ListBean>();  //数据
    private NodeProgressView nodeProgressView;
    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.track_detail);
        setCenterTitle("快递详情");
    }

    @Override
    public void initView() {

        //获取数据
        initData();
//        initAdapter();
    }



    private void initData() {
        PostRequest request = OkHttpUtils.post(NetPath.TRACK_INFO)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token))
                .params("orderid",getIntent().getStringExtra("order_id"));  //订单id
        request.execute(new AbsCallback<Object>() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                LoadingUtil.show(TrackDetailActivity.this);
            }

            @Override
            public void onAfter(boolean isFromCache, Object o, Call call, Response response, Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                LoadingUtil.dismis();
            }

            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                String s;
                JSONObject json;
                try {
                    s = response.body().string();
                    json = new JSONObject(s);
                    if (LoginUtils.isLogin(TrackDetailActivity.this, json.optInt(KeyValues.BACK_CODE))) {
                        List<OrderInfoBean.ListBean> orderlist = new Gson().fromJson(s, OrderInfoBean.class).getList();

                        for (OrderInfoBean.ListBean listBean : orderlist) {
                            logisticsDatas.add(new LogisticsData().setTime(TimeUtils.getTime(Long.parseLong(listBean.getCreatetime()+"000"))).setContext(listBean.getDescription()));
                        }
                        mHandler.sendMessage(new Message());
                        initAdapter();

                    } else {
                        ToastUtil.show(TrackDetailActivity.this, json.getString("reason"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            initAdapter();
            super.handleMessage(msg);
        }
    };


    private void initAdapter(){
        nodeProgressView = (NodeProgressView) findViewById(R.id.npv_NodeProgressView);
        nodeProgressView.nodeProgressAdapter = null;
        nodeProgressView.setNodeProgressAdapter(new NodeProgressAdapter() {

            @Override
            public int getCount() {
                return logisticsDatas.size();
            }

            @Override
            public List<LogisticsData> getData() {
                return logisticsDatas;
            }
        });
        ///
        nodeProgressView.invalidate();
        ScrollView scrollView = (ScrollView) findViewById(R.id.sss);
        scrollView.setMinimumHeight(1600);

    }
}
