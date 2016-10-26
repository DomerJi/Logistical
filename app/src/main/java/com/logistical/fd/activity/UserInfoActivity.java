package com.logistical.fd.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logistical.fd.R;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.utils.PreferencesUtils;

public class UserInfoActivity extends ToolBarActivity {
    private TextView tvUserid,tvName,tvPhone,tvSite,tvCarId,tvFromCity,tvToCity;
//    private ImageView ivHead;
    private LinearLayout llDriverInfo;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user_info);
    }

    @Override
    public void initView() {
//        ivHead = (ImageView) findViewById(R.id.userinfo_head);      //头像
        tvUserid = (TextView) findViewById(R.id.userinfo_userid);   //用户id
        tvName = (TextView) findViewById(R.id.userinfo_name);       //姓名
        tvPhone = (TextView) findViewById(R.id.userinfo_phone);     //手机号
        tvSite = (TextView) findViewById(R.id.userinfo_site);       //所在站点
        tvCarId = (TextView) findViewById(R.id.userinfo_carid);     //车牌号
        tvFromCity = (TextView) findViewById(R.id.userinfo_fromcity);       //路线城市1
        tvToCity = (TextView) findViewById(R.id.userinfo_tocity);       //路线城市2
        llDriverInfo = (LinearLayout) findViewById(R.id.userinfo_driver);//用户为司机时的信息

        tvUserid.setText(PreferencesUtils.getString(this,"realname",""));
        tvName.setText(PreferencesUtils.getString(this,"realname",""));
        tvPhone.setText(PreferencesUtils.getString(this,"mobile",""));
        tvSite.setText(PreferencesUtils.getString(this,"substation",""));

        String packet =PreferencesUtils.getString(this,"packet");
        if ("2".equals(packet)){
            llDriverInfo.setVisibility(View.VISIBLE);
            tvCarId.setText(PreferencesUtils.getString(this,"license",""));
            tvFromCity.setText(PreferencesUtils.getString(this,"substation",""));
            tvToCity.setText(PreferencesUtils.getString(this, "total_substation", ""));

        }

    }

    //设置toolbar标题
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        setCenterTitle("个人信息");
    }
}
