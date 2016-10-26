package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.logistical.fd.R;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class ModifyPSDActivity extends ToolBarActivity implements View.OnClickListener {
    private EditText etOldPd, etNewPd1, etNewPd2;
    private Button btnEnsure;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_modify_pd);
    }

    @Override
    public void initView() {
        etOldPd = (EditText) findViewById(R.id.modifypd_oldpd);     //旧密码
        etNewPd1 = (EditText) findViewById(R.id.modifypd_newpd1);   //新密码
        etNewPd2 = (EditText) findViewById(R.id.modifypd_newpd2);   //确认新密码
        btnEnsure = (Button) findViewById(R.id.modifypd_ensure);    //确定按钮
        btnEnsure.setOnClickListener(this);
    }

    //设置toolbar标题
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        setCenterTitle("修改密码");
    }

    @Override
    public void onClick(View v) {
        if (!etNewPd1.getText().toString().equals(etNewPd2.getText().toString())){
            ToastUtil.show(ModifyPSDActivity.this,"两次输入密码不一致，请重新输入");
            return;
        }
        PostRequest request = OkHttpUtils.post(NetPath.ModifyPSD)
                .params("token", PreferencesUtils.getString(this, "token"))
                .params("password", etNewPd1.getText().toString())
                .params("repassword", etNewPd2.getText().toString())
                .params("oldpassword", etOldPd.getText().toString());
        request.execute(new AbsCallback<Object>() {
            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                try {
                    String s = response.body().string();
                    JSONObject jsonObject = new JSONObject(s);
                    int back_code = jsonObject.optInt("back_code");
                    String reason = jsonObject.optString("reason");
                    if (back_code == 10000) {
                        ToastUtil.show(ModifyPSDActivity.this, "密码修改成功，请重新登录！");
                        PreferencesUtils.putLong(ModifyPSDActivity.this,PreferencesUtils.key_token_time,-1);
                        startActivity(new Intent(ModifyPSDActivity.this, LoginActivity.class));
                        finish();
                    }else {
                        ToastUtil.show(ModifyPSDActivity.this, reason);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
