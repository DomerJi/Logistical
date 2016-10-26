package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.logistical.fd.R;
import com.logistical.fd.base.MyApplication;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.LocalOrdersnBean;

import java.util.List;

public class OfflineManageActivity extends ToolBarActivity implements View.OnClickListener {
    private Button btnReceive,btnSend,btnPickup;
    private TextView tvReceive,tvSend,tvPickup;
    public LiteOrm liteOrm;


    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_offline_manage);
        MyApplication myApplication = (MyApplication) getApplication();
        liteOrm = myApplication.liteOrm;
    }

    @Override
    public void initView() {
        btnReceive = (Button) findViewById(R.id.offline_manage_receive); //收件信息处理按钮
        btnSend = (Button) findViewById(R.id.offline_manage_send);      //寄件信息处理按钮
        btnPickup = (Button) findViewById(R.id.offline_manage_pickup);  //取件信息处理按钮
        btnReceive.setOnClickListener(this);
        btnPickup.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        tvReceive = (TextView) findViewById(R.id.offline_manage_receiveNum);
        tvSend = (TextView) findViewById(R.id.offline_manage_sendNum);
        tvPickup = (TextView) findViewById(R.id.offline_manage_pickupNum);

    }
    //设置toolbar标题
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        setCenterTitle("离线管理");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.offline_manage_receive:
                Intent intent = new Intent(this,OfflineDetailActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
                break;
            case R.id.offline_manage_send:
                Intent intent1 = new Intent(this,OfflineDetailActivity.class);
                intent1.putExtra("type", "2");
                startActivity(intent1);
                break;
            case R.id.offline_manage_pickup:
                Intent intent2 = new Intent(this,OfflineDetailActivity.class);
                intent2.putExtra("type","4");
                startActivity(intent2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<LocalOrdersnBean> RList = liteOrm.query(new QueryBuilder<LocalOrdersnBean>(LocalOrdersnBean.class)
                .where("type" + " LIKE ?", new String[]{"1"}));
        List<LocalOrdersnBean> SList = liteOrm.query(new QueryBuilder<LocalOrdersnBean>(LocalOrdersnBean.class)
                .where("type" + " LIKE ?", new String[]{"2"}));
        List<LocalOrdersnBean> PList = liteOrm.query(new QueryBuilder<LocalOrdersnBean>(LocalOrdersnBean.class)
                .where("type" + " LIKE ?", new String[]{"4"}));
        tvReceive.setText(RList.size()+"");
        tvSend.setText(SList.size()+"");
        tvPickup.setText(PList.size()+"");
        BgChange(tvReceive, btnReceive, RList.size());
        BgChange(tvSend,btnSend,SList.size());
        BgChange(tvPickup,btnPickup,PList.size());
    }

    //设置背景和文字颜色
    private void BgChange(TextView tv,Button button,int size){
        if (size==0){
            tv.setTextColor(getResources().getColor(R.color.grayDeep));
            button.setBackgroundResource(R.drawable.base_btn_gray);
            button.setTextColor(getResources().getColor(R.color.grayDeep));
            button.setClickable(false);
        }else {
            tv.setTextColor(getResources().getColor(R.color.google_yellow));
            button.setBackgroundResource(R.drawable.base_btn_01_selector);
            button.setTextColor(getResources().getColor(R.color.colorLogin));
            button.setClickable(true);
        }
    }
}
