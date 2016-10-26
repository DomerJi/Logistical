package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.logistical.fd.R;
import com.logistical.fd.adapter.TrackAdapter;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.TrackOrderBean;
import com.logistical.fd.interfaces.MyItemClickListener;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;
import com.logistical.fd.widget.view.MyRecyclerView;
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
 * Created by LiFei on 2016/8/10.
 */
public class SearchNumberActivity extends ToolBarActivity implements MyItemClickListener {
    private List<TrackOrderBean.ListBean> mList = new ArrayList<TrackOrderBean.ListBean>();
    private List<TrackOrderBean.ListBean> mLists = new ArrayList<TrackOrderBean.ListBean>();
    private MyRecyclerView recyclerView;
    private static TrackAdapter mAdapter;
    private static TrackAdapter mAdapters;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        requestNoToolBar();
        setContentView(R.layout.search_number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.number_toolbar);
        toolbar.showOverflowMenu();
        getLayoutInflater().inflate(R.layout.toobar_button, toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void initView() {
        recyclerView = (MyRecyclerView) findViewById(R.id.swipe_target);
        //为RecyclerView设置控制器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置空布局
        recyclerView.setEmptyView(View.inflate(this, R.layout.empty_show_msg, null));

        if(NetworkUtils.isNetworkAvailable(this)){
            //获取数据
            initData();
        }else{
            ToastUtil.show(this,"请连接网络");
            return;
        }


        EditText numberSearch = (EditText) findViewById(R.id.number_search);

        numberSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mLists.clear();
                    for (TrackOrderBean.ListBean fb : mList) {
                        if (fb.getOrdersn().indexOf(s + "") != -1) {
                            mLists.add(fb);
                        }

                    }
                    if (mLists.size() > 0) {
                        mAdapter = new TrackAdapter(mLists);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.setItemClickListener(SearchNumberActivity.this);
                    } else {

                    }
                } else {
                    mAdapters = new TrackAdapter(mLists);
                    recyclerView.setAdapter(mAdapters);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initData() {
        PostRequest postRequest = OkHttpUtils.post(NetPath.SEARCH)
                .params("ordersn", "")  //快递单号
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token)); //登录token
        postRequest.execute(new AbsCallback<Object>() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                LoadingUtil.show(SearchNumberActivity.this);
            }

            @Override
            public void onAfter(boolean isFromCache, @Nullable Object o, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                LoadingUtil.dismis();
            }

            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
                String s;
                JSONObject json;
                try {
                    s = response.body().string();
                    json = new JSONObject(s);
                    if (LoginUtils.isLogin(SearchNumberActivity.this, json.optInt(KeyValues.BACK_CODE))) {
                        mList.addAll(new Gson().fromJson(s, TrackOrderBean.class).getList());
                        //recyclerView.setAdapter(new TrackAdapter(mList));
                    } else {
                        ToastUtil.show(SearchNumberActivity.this, json.getString("reason"));
                    }
                    //mAdapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent(this, TrackDetailActivity.class);
        intent.putExtra("order_id", mLists.get(postion).getId());
        startActivity(intent);
    }
}
