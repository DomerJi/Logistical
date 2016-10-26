package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.logistical.fd.R;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.ActivityUtil;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.TimeCountUtil;
import com.logistical.fd.utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LiFei on 2016/8/8.
 */
public class ForgetPwdActivity extends ToolBarActivity implements View.OnClickListener {
    private EditText mPhone, mMsg, mNewPwd;
    private TimeCountUtil timeCountUtil;
    private Button btnMsg;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.forget_pwd);
    }

    @Override
    public void initView() {
        //设置ToolBar
        setCenterTitle("忘记密码");
        mPhone = (EditText) findViewById(R.id.et_number); //手机号
        mMsg = (EditText) findViewById(R.id.et_msg);  //验证码
        mNewPwd = (EditText) findViewById(R.id.new_pwd);  //新密码
        btnMsg = (Button) findViewById(R.id.btn_msg);  //获取验证码
        btnMsg.setOnClickListener(this);
        Button btnSubmit = (Button) findViewById(R.id.btn_submit); //提交
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_msg:  //获取验证码
                if(NetworkUtils.isNetworkAvailable(this)){
                    if(common()){
                        timeCountUtil = new TimeCountUtil(this, 60000, 1000, btnMsg);
                        timeCountUtil.start();
                        //获取验证码
                        getMsg(mPhone.getText().toString().trim());
                    }

                }else{
                    ToastUtil.show(this, "请连接网络");
                }

                break;
            case R.id.btn_submit:  //提交
                if(NetworkUtils.isNetworkAvailable(this)){
                    if(common()){
                        if(!TextUtils.isEmpty(mMsg.getText().toString().trim())){
                            if(!TextUtils.isEmpty(mNewPwd.getText().toString().trim())){
                                //提交
                                submit();
                            }else{
                                ToastUtil.show(this,"请输入新密码");
                            }
                        }else{
                            ToastUtil.show(this,"请输入验证码");
                        }
                    }
                }else{
                    ToastUtil.show(this, "请连接网络");
                }
                break;
        }
    }

    private void submit() {
        PostRequest request = OkHttpUtils.post(NetPath.RESET_PASSWORD)
                .params("mobile",mPhone.getText().toString().trim())
                .params("pincode",mMsg.getText().toString().trim())
                .params("password",mNewPwd.getText().toString().trim());
        request.execute(new AbsCallback<Object>() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                LoadingUtil.show(ForgetPwdActivity.this);
            }

            @Override
            public void onAfter(boolean isFromCache, Object o, Call call, Response response, Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                LoadingUtil.dismis();
            }

            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                String s;
                JSONObject json;
                try {
                    s = response.body().string();
                    json = new JSONObject(s);
                    if (json.getInt("back_code") == 10000) {
                        ToastUtil.show(ForgetPwdActivity.this, "修改成功");
                        timeCountUtil.onFinish();
                        timeCountUtil.cancel();
                        Intent intent = new Intent(ForgetPwdActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.show(ForgetPwdActivity.this, json.getString("reason"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取验证码
     * @param trim
     */
    private void getMsg(String trim) {
        PostRequest request = OkHttpUtils.post(NetPath.SMS)
                .params("mobile",trim)
                .params("typeid",2+"");  //短信模板id，2为忘记密码获取验证码
        request.execute(new AbsCallback<Object>() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                LoadingUtil.show(ForgetPwdActivity.this);
            }

            @Override
            public void onAfter(boolean isFromCache, Object o, Call call, Response response, Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                LoadingUtil.dismis();
            }

            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                String s;
                JSONObject json;
                try {
                    s = response.body().string();
                    json = new JSONObject(s);
                    if(json.getInt("back_code")==10000){
                        ToastUtil.show(ForgetPwdActivity.this,"验证码已发送");
                    }else{
                        ToastUtil.show(ForgetPwdActivity.this, json.getString("reason"));
                        timeCountUtil.onFinish();
                        timeCountUtil.cancel();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean common() {
            if (!TextUtils.isEmpty(mPhone.getText().toString().trim())) {
                if(ActivityUtil.isMobileNO(mPhone.getText().toString().trim())){
                    return true;
                }else{
                    ToastUtil.show(this,"您输入的手机号码有误");
                }
            } else {
                ToastUtil.show(this, "请输入手机号");
            }

        return false;
    }


}
