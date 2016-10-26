package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.Gson;
import com.logistical.fd.R;
import com.logistical.fd.adapter.TrackAdapter;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.TrackOrderBean;
import com.logistical.fd.interfaces.MyItemClickListener;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.LoginUtils;
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
 * Created by LiFei on 2016/8/15.
 */
public class SearchResultActivity extends ToolBarActivity implements View.OnClickListener, OnLoadMoreListener, MyItemClickListener {
    public SwipeToLoadLayout swipeToLoadLayout;
    private MyRecyclerView recyclerView;
    private TextView tvInfo;
    private List<TrackOrderBean.ListBean> mList = new ArrayList<TrackOrderBean.ListBean>();
    private TrackAdapter mAdapter;
    private int page=1;
    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.search_result);
        //设置ToolBar
        setCenterTitle("搜索结果");
    }

    @Override
    public void initView() {
        tvInfo = (TextView) findViewById(R.id.search_info); //收入等信息
        swipeToLoadLayout = (SwipeToLoadLayout)findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setRefreshing(false);
        recyclerView = (MyRecyclerView)findViewById(R.id.swipe_target);
        recyclerView.mSwipeToLoadLayout = swipeToLoadLayout;
        //为RecyclerView设置控制器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TrackAdapter(mList);
        //RecyclerView条目点击事件
        mAdapter.setItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setEmptyView(findViewById(R.id.msg_empty));

        //请求数据
        initData(1);

        swipeToLoadLayout.setOnLoadMoreListener(this);
        /*recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });*/


    }

    /**
     * 获取数据
     * @param i
     */
    private void initData(final int i) {
        page=(i==1)?1:page+1;
        PostRequest request = OkHttpUtils.post(NetPath.SEARCH)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token))
                .params("companyid",getIntent().getStringExtra("u_id"))  //公司id
                .params("page",page+"")
                .params("type",getIntent().getIntExtra("type",0)+"")
                .params("starttime", getIntent().getStringExtra("begintime"))  //开始时间
                .params("endtime", getIntent().getStringExtra("stoptime"));  //截止时间

        request.execute(new AbsCallback<Object>() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                if (i == 1) {
                    LoadingUtil.show(SearchResultActivity.this);
                }
            }

            @Override
            public void onAfter(boolean isFromCache, Object o, Call call, Response response, Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                if(i==1){
                    LoadingUtil.dismis();
                }
            }

            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                String s;
                JSONObject json;
                try {
                    s = response.body().string();
                    json = new JSONObject(s);
                    if(LoginUtils.isLogin(SearchResultActivity.this, json.optInt(KeyValues.BACK_CODE))){
                        tvInfo.setText("共"+json.optString("total")+"件  "+"预计收入："+json.optString("income")+"元");
                        if(i==2 && json.getJSONArray("list").length()==0){
                            ToastUtil.show(SearchResultActivity.this,"没有数据啦");
                            stopRefresh();
                        }
                        mList.addAll(new Gson().fromJson(s, TrackOrderBean.class).getList());
                    }else{
                        ToastUtil.show(SearchResultActivity.this,json.getString("reason"));
                    }
                    mAdapter.notifyDataSetChanged();
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
        LogUtil.i("L1", mList.get(postion).getOrdersn());
        Intent intent = new Intent(this, TrackDetailActivity.class);
        intent.putExtra("order_id",mList.get(postion).getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPause() {
        stopRefresh();
        super.onPause();
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


    /**
     * 停止刷新
     */
    protected void stopRefresh() {
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }
}
