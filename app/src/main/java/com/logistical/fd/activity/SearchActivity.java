package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.logistical.fd.R;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.ComPanyBean;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.utils.DateTimePickDialogUtil;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.TimeUtils;
import com.logistical.fd.utils.ToastUtil;

/**
 * Created by LiFei on 2016/8/8.
 */
public class SearchActivity extends ToolBarActivity implements View.OnClickListener{
    private TextView mBegin;
    private TextView mStop;
    private TextView tvCompany;
    private Button mSubmit;
    private Boolean isBegin = false; //是否开始搜索，默认不是
    private Boolean hasMeasured=false;
    private LinearLayout searchCom;
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private String u_id;
    private int type=0;  //类型，默认为0-收件
    private RadioButton searReceive,searSend;
    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        //设置ToolBar
        setCenterTitle("选择搜索");
    }

    @Override
    public void initView() {
        LinearLayout searchNumber = (LinearLayout) findViewById(R.id.search_number); //按号码搜索
        searchNumber.setOnClickListener(this);
        searchCom = (LinearLayout) findViewById(R.id.search_com);
        LinearLayout mCompany = (LinearLayout) findViewById(R.id.ex_company); //选择快递公司
        mCompany.setOnClickListener(this);
        tvCompany = (TextView) findViewById(R.id.tv_company);  //快递公司

        searReceive = (RadioButton) findViewById(R.id.search_receive);  //寄件
        searSend = (RadioButton) findViewById(R.id.search_receive);  //收件




        mBegin = (TextView) findViewById(R.id.time_begin);  //开始时间
        mBegin.setText(DateTimePickDialogUtil.getStrTime(System.currentTimeMillis()));
        mBegin.setOnClickListener(this);
        mStop = (TextView) findViewById(R.id.time_stop); //截止时间
        mStop.setText(DateTimePickDialogUtil.getStrTime(System.currentTimeMillis()));
        mStop.setOnClickListener(this);
        mSubmit = (Button) findViewById(R.id.btn_submit);  //开始搜索
        mSubmit.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_number:  //号码搜索
                Intent intent = new Intent(SearchActivity.this,SearchNumberActivity.class);
                startActivity(intent);
                break;
            case R.id.ex_company:  //选择快递公司
                Intent com = new Intent(SearchActivity.this,SelectCompanyActivity.class).putExtra(KeyValues.SOMPANY_SELECTED_K, KeyValues.SOMPANY_SELECTED_V);
                startActivityForResult(com,15);
                break;
            case R.id.time_begin:  //开始时间
                DateTimePickDialogUtil timeBegin = new DateTimePickDialogUtil(SearchActivity.this, TimeUtils.getTime(System.currentTimeMillis(), TimeUtils.DATE_FORMAT_DATE));
                timeBegin.dateTimePicKDialog(mBegin,0);
                break;
            case R.id.time_stop:  //截止时间
                DateTimePickDialogUtil timeStop = new DateTimePickDialogUtil(SearchActivity.this,TimeUtils.getTime(System.currentTimeMillis(),TimeUtils.DATE_FORMAT_DATE));
                timeStop.dateTimePicKDialog(mStop,0);
                break;
            case R.id.btn_submit:  //开始搜索
                /*if(isBegin){
                    mSubmit.setText("开始搜索");
                    //显示动画
                    comShow();
                }else {
                    mSubmit.setText("重新搜索");
                    //隐藏动画
                    comHide();
                }
                isBegin = !isBegin;*/

                if(Double.parseDouble(DateTimePickDialogUtil.getTime(mStop.getText().toString()))<Double.parseDouble(DateTimePickDialogUtil.getTime(mBegin.getText().toString()))){
                    ToastUtil.show(this,"截止日期不能小于开始时间");
                    return;
                }

                /*//快递公司必填
                if(tvCompany.getText().toString().trim().equals("")){
                    ToastUtil.show(this,"请选择快递公司");
                    return;
                }*/
                LogUtil.e("time",mBegin.getText().toString()+"--"+DateTimePickDialogUtil.getTime(mBegin.getText().toString()));
                Intent search = new Intent(this,SearchResultActivity.class);
                search.putExtra("begintime",DateTimePickDialogUtil.getTime(mBegin.getText().toString()));  //开始时间
                search.putExtra("stoptime",DateTimePickDialogUtil.getTime(mStop.getText().toString()));//截止时间
                search.putExtra("u_id",u_id);
                search.putExtra("type", type = searReceive.isChecked() ? 0 : 1);
                startActivity(search);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==15&& resultCode==KeyValues.RESULT_OK&&data!=null){
            ComPanyBean comPanyBean = data.getParcelableExtra(KeyValues.RESULT_COMPANY);
            tvCompany.setText(comPanyBean.getName());
            u_id = comPanyBean.getId();  //快递id
        }
    }

    /**
     * 显示动画
     */
    private void comShow() {
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        searchCom.setAnimation(mShowAction);
        searchCom.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏动画
     */
    private void comHide() {
        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(500);
        searchCom.setAnimation(mHiddenAction);
        searchCom.setVisibility(View.GONE);
    }

}
