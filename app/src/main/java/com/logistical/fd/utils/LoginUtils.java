package com.logistical.fd.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.logistical.fd.MainActivity;
import com.logistical.fd.activity.LoginActivity;
import com.logistical.fd.bean.UserInfo;
import com.logistical.fd.set.NetPath;
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
 * Created by admin on 2016/7/9.
 */
public class LoginUtils {


    public static final long LOGIN_FAIED_TIME = 1000 * 60 * 60 * 24 * 7; // 10小时

//    public static final long LOGIN_FAIED_TIME = 1000 * 10; // 1分钟

    /**
     * 是否登录 toKen是否失效
     */
    public static boolean isLogin(Context context) {
        long time = PreferencesUtils.getLong(context, PreferencesUtils.key_token_time, -1);
        if (time != -1) {
            //登录失效
            if ((System.currentTimeMillis() - time) > LOGIN_FAIED_TIME) {
                return false;
            } else {
                return true;
            }
        } else { //未登录
            return false;
        }
    }

    /**
     * 10103' => '登录失效',
     * 10104' => '您的账号已被登录',
     * @param code
     * @return
     */
    public static boolean isLogin(Context context,int code){
        if(code ==10000 ){
            return true;
        }else if(code == 10104){
            ToastUtil.show(context,"您的账号已被登录");
            return false;
        }else if(code == 10103){
            ToastUtil.show(context,"登录失效");
            return false;
        }else {
            return false;
        }
    }

    /**
     * 退出登录
     *
     * @param context
     * @return
     */
    public static boolean backLogin(Context context) {
        return PreferencesUtils.putLong(context, PreferencesUtils.key_token_time, -1);
    }

    /**
     * 登录方法
     *
     * @param context
     * @param uname
     * @param pas
     */
    public static void login(final Context context, final String uname, final String pas) {
        //判断手机号和密码输入是否为空
        if (TextUtils.isEmpty(uname)) {
            ToastUtil.show(context, "手机号不能为空");
            return;
        }
        if (TextUtils.isEmpty(pas)) {
            ToastUtil.show(context, "密码不能为空");
            return;
        }
        //验证电话号码
        if (ActivityUtil.isMobileNO(uname) == false) {
            ToastUtil.show(context, "您输入的号码有误");
            return;
        }


        //请求网络
        PostRequest postRequest = OkHttpUtils.post(NetPath.LOGIN_PATH)
                .params("username", uname)
                .params("password", MD5Utils.encode(uname+pas));

        postRequest.execute(new AbsCallback<Object>() {

            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                LoadingUtil.show(context);
            }

            @Override
            public void onAfter(boolean isFromCache, Object o, Call call, Response response, Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                LoadingUtil.dismis();
            }

            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {

                JSONObject jsonObject = null;
                Gson gson=null;
                String s;
                try {
                    s = response.body().string();
                    jsonObject = new JSONObject(s);
                    gson = new Gson();
                    if (jsonObject != null) {
                        if (jsonObject.getInt("back_code")==10000) {
                            UserInfo.UserinfoBean userInfo = gson.fromJson(s, UserInfo.class).getUserinfo();

                            Intent login = new Intent(context, MainActivity.class);
                            context.startActivity(login);

                            //保存token
                            PreferencesUtils.putString(context, PreferencesUtils.key_token, jsonObject.optString(PreferencesUtils.key_token));
                            //token 保存时间
                            PreferencesUtils.putLong(context, PreferencesUtils.key_token_time, System.currentTimeMillis());
                            // 用户名密码保存
                            PreferencesUtils.putString(context, PreferencesUtils.key_username, uname);
                            PreferencesUtils.putString(context, PreferencesUtils.key_passwd, pas);
                            // 保存登录信息
                            PreferencesUtils.putString(context, PreferencesUtils.key_uid, userInfo.getUid());  //uid
                            PreferencesUtils.putString(context, PreferencesUtils.key_mobile, userInfo.getMobile());  //mobile
                            PreferencesUtils.putString(context, PreferencesUtils.key_packet, userInfo.getPacket());  //packet
                            PreferencesUtils.putString(context, PreferencesUtils.key_realname, userInfo.getRealname());  //realname
                            PreferencesUtils.putString(context, PreferencesUtils.key_substation_id, userInfo.getSubstation_id());  //substation_id
                            PreferencesUtils.putString(context, PreferencesUtils.key_license, (String)userInfo.getLicense());  //license
                            PreferencesUtils.putString(context, PreferencesUtils.key_substation, (String)userInfo.getSubstation());  //substation
                            PreferencesUtils.putString(context, PreferencesUtils.key_total_substation, userInfo.getTotal_substation());//总站

                            LogUtil.i("token", PreferencesUtils.getString(context, PreferencesUtils.key_token));
                            LoadingUtil.dismis();

                            /*if (netGoCallBackListener != null) {
                                netGoCallBackListener.netOnClick();
                            }*/

                            if (context instanceof LoginActivity) {
                                ((LoginActivity) context).finish();
                            }

                        } else {
                            ToastUtil.show(context, jsonObject.getString("reason"));
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        });
    }

    /*
    * 网络请求 除 登录 接口
   * 都需经过接口回掉方可
   */
    public static LoginUtils.NetGoCallBackListener netGoCallBackListener;

    public interface NetGoCallBackListener {
        void netOnClick();
    }

}
