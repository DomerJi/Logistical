package com.logistical.fd.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.logistical.fd.R;
import com.logistical.fd.adapter.TrackAdapter;
import com.logistical.fd.bean.TrackOrderBean;
import com.logistical.fd.interfaces.MyItemClickListener;
import com.logistical.fd.widget.view.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 2016/7/9.
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment,View.OnClickListener, OnRefreshListener, OnLoadMoreListener, MyItemClickListener {
    public SwipeToLoadLayout swipeToLoadLayout;
    public MyRecyclerView recyclerView;
    public int isLoad=1;  //1-上拉加载  2-加载更多

    protected List<TrackOrderBean.ListBean> list = new ArrayList<TrackOrderBean.ListBean>();
    protected TrackAdapter mAdapter;

    /**
     * 刷新界面
     */
    @Override
    public void refreshUI() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_receive_send, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        recyclerView = (MyRecyclerView) view.findViewById(R.id.swipe_target);
        //为RecyclerView设置控制器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setEmptyView(View.inflate(getActivity(), R.layout.empty_show_msg, null));

        mAdapter = new TrackAdapter(list);
        //RecyclerView条目点击事件
        mAdapter.setItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        //设置空布局
        recyclerView.setEmptyView(getActivity().findViewById(R.id.msg_empty));

        //请求数据
        initData(1);

        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });
    }

    protected abstract void initData(int isLoad);

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
