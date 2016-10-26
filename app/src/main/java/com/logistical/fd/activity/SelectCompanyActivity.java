package com.logistical.fd.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.logistical.fd.R;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.BaseBean;
import com.logistical.fd.bean.ComPanyBean;
import com.logistical.fd.notify.EventType;
import com.logistical.fd.notify.Notify;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;
import com.logistical.fd.utils.ToolsCallback;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class SelectCompanyActivity extends ToolBarActivity {

    private ComPanyBean companys;
    private int current = 0;

    private int type = 0;
    private ListView companyListView;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_company);
        type = getIntent().getIntExtra(KeyValues.SOMPANY_SELECTED_K, 0);

        if(type == KeyValues.SOMPANY_SELECTED_V){
            setCenterTitle("选择快递公司");
        }else {
            setCenterTitle("快递公司设置");
            current = PreferencesUtils.getInt(SelectCompanyActivity.this, PreferencesUtils.key_current_company_int, current);
        }
    }

    @Override
    public void initView() {


        companyListView = (ListView) findViewById(R.id.company_listView);

        companyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) companyListView.getChildAt(current).findViewById(R.id.company_checked);
                checkedTextView.setChecked(false);
                ((CheckedTextView) view.findViewById(R.id.company_checked)).setChecked(true);
                current = position;
                if(type == 0){ //保存当前公司信息
                    PreferencesUtils.putString(SelectCompanyActivity.this, PreferencesUtils.key_current_company_obj, new Gson().toJson(companys.list.get(current)));
                    PreferencesUtils.putInt(SelectCompanyActivity.this, PreferencesUtils.key_current_company_int, current);
                    Notify.getInstance().NotifyActivity(EventType.UPDATE_COMPANY);
                }

                //设置返回信息
                setResult(KeyValues.RESULT_OK,getIntent().putExtra(KeyValues.RESULT_COMPANY,companys.list.get(current)));
                Notify.getInstance().NotifyActivity(EventType.UPDATE_COMPANY);
                finish();
            }
        });

        boolean isNet = NetworkUtils.isNetworkAvailable(this);
        if(isNet){
            initData();
        }else {
            initLocalData();
        }

    }

    private void initLocalData() {
        String jsonStr = PreferencesUtils.getString(this, PreferencesUtils.key_all_company, "");
        if(TextUtils.isEmpty(jsonStr)){
            ToastUtil.show(this, "无法获取公司列表\n请连接网络后重试");
        }else {
            initAdapter(jsonStr);
        }
    }

    private void initData(){
        PostRequest postRequest = OkHttpUtils.post(NetPath.COMPANY)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""));
        
        postRequest.execute(new ToolsCallback<Object>(this) {
            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                try {
                    String s = response.body().string();
                    JSONObject jsonobj = new JSONObject(s);
                    //判断登录是否成功
                    if(LoginUtils.isLogin(SelectCompanyActivity.this,jsonobj.optInt(KeyValues.BACK_CODE, 0))){
                        initAdapter(s);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initAdapter(String jsonStr){
        companys = new Gson().fromJson(jsonStr, ComPanyBean.class);
        companyListView.setAdapter(new QuickAdapter<ComPanyBean>(SelectCompanyActivity.this, R.layout.list_company_imp, companys.list) {

            @Override
            protected void convert(BaseAdapterHelper helper, ComPanyBean item) {
                if (helper.getPosition() == current) {
                    if(type != KeyValues.SOMPANY_SELECTED_V){
                        helper.setChecked(R.id.company_checked, true);
                    }
                }
                helper.setText(R.id.company_checked, item.getName());
            }
        });
    }

}
