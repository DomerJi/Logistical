package com.logistical.fd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.logistical.fd.MainActivity;
import com.logistical.fd.R;
import com.logistical.fd.base.BaseActivity;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;

/**
 * Created by LiFei on 2016/8/4.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mName, mPwd;
    private ImageView mPhoneclear, mSeePwd;
    private boolean isHidden = true; //是否隐藏密码，默认是

    @Override
    protected void initCreate(Bundle savedInstanceState) {

        boolean backLogin = getIntent().getBooleanExtra(KeyValues.IS_BACK_LOGIN,false);

        if(LoginUtils.isLogin(LoginActivity.this)&&!backLogin){
            Intent intent  = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initView() {
        mName = (EditText) findViewById(R.id.login_name);  //手机号
        if(PreferencesUtils.getString(this,PreferencesUtils.key_username)!=null){
            mName.setText(PreferencesUtils.getString(this,PreferencesUtils.key_username));
        }
        mPwd = (EditText) findViewById(R.id.login_pwd);  //登录密码
        mPhoneclear = (ImageView) findViewById(R.id.phone_clear);  //清除手机号
        mPhoneclear.setOnClickListener(this);
        mSeePwd = (ImageView) findViewById(R.id.see_pwd); //查看密码
        mSeePwd.setOnClickListener(this);
        Button mLogin = (Button) findViewById(R.id.login); //登录
        mLogin.setOnClickListener(this);
        TextView mForgetPwd = (TextView) findViewById(R.id.forget_pwd);  //忘记密码
        mForgetPwd.setOnClickListener(this);
        //手机号输入监听
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhoneclear.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mName.getSelectionStart()==0){
                    mPhoneclear.setVisibility(View.INVISIBLE);
                }
            }
        });
        //密码输入监听
        mPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSeePwd.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mPwd.length()==0){
                    mSeePwd.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_clear:  //清除登录名
                mName.setText("");
                mPhoneclear.setVisibility(View.INVISIBLE);
                break;
            case R.id.see_pwd:  //查看密码
                    mSeePwd.setSelected(!mSeePwd.isSelected());
                    if (isHidden) {
                        //设置EditText文本为可见的
                        mPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //设置EditText文本为隐藏的
                        mPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    isHidden = !isHidden;
                    mPwd.postInvalidate();
                    //切换后将EditText光标置于末尾
                    CharSequence charSequence = mPwd.getText();
                    if (charSequence instanceof Spannable) {
                        Spannable spanText = (Spannable) charSequence;
                        Selection.setSelection(spanText, charSequence.length());
                    }

                break;
            case R.id.login:  //登录
                //判断是否连接网络
                if(NetworkUtils.isNetworkAvailable(LoginActivity.this)){
                    //登录操作
                    LoginUtils.login(LoginActivity.this, mName.getText().toString().trim(), mPwd.getText().toString().trim());
                }else{
                    ToastUtil.show(LoginActivity.this, "请连接网络");
                }

                break;
            case R.id.forget_pwd:  //忘记密码
                Intent forget = new Intent(LoginActivity.this,ForgetPwdActivity.class);
                startActivity(forget);
                break;
        }
    }
}
