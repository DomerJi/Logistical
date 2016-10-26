package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.logistical.fd.R;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.fragment.ReceiveFragment;
import com.logistical.fd.fragment.SendFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiFei on 2016/8/8.
 */
public class TrackActivity extends ToolBarActivity implements View.OnClickListener{
    private Map<Integer, Fragment> fmap = new HashMap<Integer, Fragment>();
    private FrameLayout flMain;
    private RadioButton mReceive;
    private RadioButton mSend;
    private RadioGroup mGroup;
    private int currentRadio;
    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_track);
        fmap.clear();
        fmap.put(R.id.track_receive, new ReceiveFragment());
        fmap.put(R.id.track_send, new SendFragment());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_main,fmap.get(R.id.track_receive)).commit();

        }
    }

    @Override
    public void initView() {
        //设置ToolBar
        setCenterTitle("快递跟踪");
        setRightIcon(R.drawable.search);
        setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //搜索
                Intent search = new Intent(TrackActivity.this,SearchActivity.class);
                startActivity(search);
            }
        });

        mGroup = (RadioGroup) findViewById(R.id.track_group);
        mReceive = (RadioButton) findViewById(R.id.track_receive);
        mSend = (RadioButton) findViewById(R.id.track_send);
        flMain = (FrameLayout) findViewById(R.id.fl_main);
        currentRadio = R.id.track_receive;
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment temp = fmap.get(checkedId);
                switch (checkedId) {
                    case R.id.track_receive:
                        switchContent(temp,checkedId);
                        break;
                    case R.id.track_send:
                        switchContent(temp,checkedId);
                        break;

                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    /* 修改显示的内容 不会重新加载 */
    private Fragment mContent;
    public void switchContent(Fragment to, int id) {
        mContent = fmap.get(currentRadio);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (to != mContent) {

            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(mContent).add(R.id.fl_main, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mContent).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            currentRadio = id;
        }
    }

}
