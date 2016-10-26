package com.logistical.fd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.logistical.fd.R;
import com.logistical.fd.base.MyApplication;
import com.logistical.fd.bean.ComPanyBean;
import com.logistical.fd.bean.LocalOrdersnBean;
import com.logistical.fd.notify.BaseObserverActivity;
import com.logistical.fd.notify.EventType;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.TimeUtils;
import com.logistical.fd.utils.ToastUtil;
import com.logistical.fd.utils.ToolsCallback;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;
import com.zbar.lib.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class ScanStatusActivity extends BaseObserverActivity {

    private int current;

    private ComPanyBean comPanyBean;

    private String ordersn;
    private TextView type01company;
    private TextView type01commit;
    private String orderid;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scan_status);
    }

    @Override
    public void initView() {

        setCenterTitle("录入状态");

        /**
         * status == 2  扫描成功
         *        == 3  扫描失败
         *        == 1  第一次扫描
         */
        int status = getIntent().getIntExtra("Status", 2);
        current = getIntent().getIntExtra(PreferencesUtils.key_current_scan, -1);
//        ToastUtil.show(this,current+" ____");
        ordersn = getIntent().getStringExtra(PreferencesUtils.key_ordersn);
        orderid = getIntent().getStringExtra("orderid");


        switch (status) {
            case 2:

                Type02OnListener type02 = new Type02OnListener();

                LinearLayout scanstatustype02 = (LinearLayout) findViewById(R.id.scan_status_type02);
                scanstatustype02.setVisibility(View.VISIBLE);
                TextView type02commit = (TextView) findViewById(R.id.type02_commit);//继续扫描
                TextView type02selectnocommit = (TextView) findViewById(R.id.type02_select_no_commit);//撤销此次录入
                TextView type02number = (TextView) findViewById(R.id.type02_number);//订单号
                type02number.setText(ordersn);
                type02commit.setOnClickListener(type02);
                type02selectnocommit.setOnClickListener(type02);

                showCompany();

                break;
            case 3:

                Type03OnListener type03 = new Type03OnListener();

                LinearLayout scanstatustype03 = (LinearLayout) findViewById(R.id.scan_status_type03);
                scanstatustype03.setVisibility(View.VISIBLE);
                TextView type03commit = (TextView) findViewById(R.id.type03_commit);//手工录入
                TextView type03rescan = (TextView) findViewById(R.id.type03_re_scan);//重新扫描

                type03commit.setOnClickListener(type03);
                type03rescan.setOnClickListener(type03);

                break;
            case 1:

                Type01OnListener type01 = new Type01OnListener();

                LinearLayout scanstatustype01 = (LinearLayout) findViewById(R.id.scan_status_type01);
                scanstatustype01.setVisibility(View.VISIBLE);

                type01commit = (TextView) findViewById(R.id.type01_commit);
                TextView type01selectcompany = (TextView) findViewById(R.id.type01_select_company);//选择快递公司
                type01company = (TextView) findViewById(R.id.type01_company);

                TextView type01number = (TextView) findViewById(R.id.type01_number);//订单号
                type01number.setText(ordersn);

                type01commit.setOnClickListener(type01);
                type01selectcompany.setOnClickListener(type01);
                break;
            default:
                break;
        }


    }

    /**
     * 显示当前选择的快递公司
     */
    private void showCompany(){
        int current = PreferencesUtils.getInt(ScanStatusActivity.this,PreferencesUtils.key_current_company_int,-1);
        if(current!=-1){
            String s = PreferencesUtils.getString(ScanStatusActivity.this,PreferencesUtils.key_current_company_obj,"");
            if(s.length()>0){
                comPanyBean = new Gson().fromJson(s, ComPanyBean.class);
                if(comPanyBean!=null){
                        TextView textView = (TextView) findViewById(R.id.type02_company);
                        textView.setText(comPanyBean.getName());
                        //收件/寄件数量跟踪
                        if(this.current==KeyValues.SCAN_IN_S){
                            getTotal("0");
                        }else if(this.current==KeyValues.SCAN_IN_J){
                            getTotal("1");
                        }

                }
            }
        }
    }

    @Override
    protected void onChange(String eventType) {
        if(EventType.UPDATE_COMPANY==eventType){
            initCompany();

        }
    }

    private void initCompany() {
        int current = PreferencesUtils.getInt(ScanStatusActivity.this, PreferencesUtils.key_current_company_int, -1);
        if (current != -1) {
            String s = PreferencesUtils.getString(ScanStatusActivity.this, PreferencesUtils.key_current_company_obj, "");
            if (s.length() > 0) {
                comPanyBean = new Gson().fromJson(s, ComPanyBean.class);
                if(type01company!=null&&type01commit!=null){
                    type01company.setText("当前已选择："+comPanyBean.getName());
                    type01commit.setEnabled(true);
                    type01commit.setTextColor(Color.BLACK);
                }
            }
        }
    }

    @Override
    protected String[] getObserverEventType() {
        return new String[]{EventType.UPDATE_COMPANY};
    }

    class Type01OnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.type01_select_company){
                //选择快递公司
                startActivity(new Intent(ScanStatusActivity.this,SelectCompanyActivity.class));
            }else {

                MyApplication app = (MyApplication) MyApplication.getAppContext();
                LocalOrdersnBean localOrder = new LocalOrdersnBean();
                localOrder.ordersn = ordersn;
                localOrder.type = current;
                localOrder.companyid = comPanyBean.getId();
                localOrder.only_id = ordersn + comPanyBean.getId() + current ;
                app.liteOrm.save(localOrder);
                LoadingUtil.showAlertDialog(ScanStatusActivity.this, "本地录入成功", new LoadingUtil.MyDialogListenner() {
                    @Override
                    public void callBack(boolean isComfirm) {
                        if (isComfirm) {
                            finish();
                        }
                    }
                });

            }

        }
    }

    private void getTotal(final String type){
        if(comPanyBean==null)return;

        PostRequest request = OkHttpUtils.post(NetPath.COUNT_TOTAL)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""))
                .params("companyid",comPanyBean.getId())
                .params("type", type);
        request.execute(new AbsCallback<Object>() {
            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
                try {
                    String s = response.body().string();
                    JSONObject jsonobj = new JSONObject(s);
                    LogUtil.e(CaptureActivity.class.getSimpleName(), s);
                    if(LoginUtils.isLogin(ScanStatusActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))) {
                        String total = jsonobj.optString("total", "0");
                        int ont = Integer.parseInt(total);
                        TextView totaltext = (TextView) findViewById(R.id.type02_company_total);
                        totaltext.setText("今天第"+ont+"件");
                        totaltext.setVisibility(View.VISIBLE);

                    }else {
                        ToastUtil.show(ScanStatusActivity.this, jsonobj.optString("reason"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void request(){
        PostRequest request = null;
        switch (current){
            case KeyValues.SCAN_IN_J://寄件
                request = OkHttpUtils.post(NetPath.SCAN)
                        .params("type","1");
                break;
            case KeyValues.SCAN_IN_S://收件
                request = OkHttpUtils.post(NetPath.SCAN)
                        .params("type", "0");
                break;
            case KeyValues.SCAN_IN_PICKUP://取件
                request = OkHttpUtils.post(NetPath.PICKUP);
                break;
            case KeyValues.SCAN_IN_Y://疑难件
                startActivity(new Intent(ScanStatusActivity.this, DifficultActivity.class)
                        .putExtra(KeyValues.ORDERSN, ordersn));
                finish();
                return;
            default:
                break;
        }

        request.params("token",PreferencesUtils.getString(this,PreferencesUtils.key_token,""))
                .params("ordersn", ordersn);
        if(comPanyBean!=null){
            request.params("companyid", comPanyBean.getId());
        }

        request.execute(new ToolsCallback<Object>(this,"正在录入") {
            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                super.onError(isFromCache, call, response, e);
                ToastUtil.show(ScanStatusActivity.this, "失败！！！");
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                try {
                    String s = response.body().string();
                    JSONObject jsonobj = new JSONObject(s);
                    LogUtil.e(CaptureActivity.class.getSimpleName(), s);
                    //判断登录是否成功
                    if(LoginUtils.isLogin(ScanStatusActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))){
                        startActivity(new Intent(ScanStatusActivity.this, ScanStatusActivity.class)
                                .putExtra("Status", 2));
                        finish();
                    }else {
                        ToastUtil.show(ScanStatusActivity.this,jsonobj.optString("reason"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    class Type02OnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.type02_commit){
                //继续扫描
                finish();
            }else {
                //撤销此次扫描结果
                cancel();
            }


        }
    }

    private void cancel(){
        PostRequest request = OkHttpUtils.post(NetPath.CANCEL_ORDER)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""))
                .params("orderid", orderid);
        request.execute(new ToolsCallback<Object>(this,"正在撤销"){

            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
                try {
                    String s = response.body().string();
                    JSONObject jsonobj = new JSONObject(s);
                    LogUtil.e(CaptureActivity.class.getSimpleName(), s);
                    //判断登录是否成功
                    if(LoginUtils.isLogin(ScanStatusActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))){
                        ToastUtil.show(ScanStatusActivity.this, jsonobj.optString("reason"));
                        Toast.makeText(ScanStatusActivity.this,"撤销成功",Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        ToastUtil.show(ScanStatusActivity.this,jsonobj.optString("reason"));
                        Toast.makeText(ScanStatusActivity.this,"撤销失败",Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class Type03OnListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.type03_re_scan){
                //重新扫描

            }else {
                //手工录入
                startActivity(new Intent(ScanStatusActivity.this,CaptureActivity.class).putExtra("IsSd",false));
            }

            finish();

        }
    }

}
