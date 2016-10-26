package com.logistical.fd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.logistical.fd.activity.LoginActivity;
import com.logistical.fd.activity.ModifyPSDActivity;
import com.logistical.fd.activity.OfflineManageActivity;
import com.logistical.fd.activity.TrackActivity;
import com.logistical.fd.activity.UserInfoActivity;
import com.logistical.fd.notify.BaseObserverActivity;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.NetworkUtils;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;
import com.zbar.lib.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseObserverActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private TextView phone;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        requestNoToolBar();
        setContentView(R.layout.activity_main);
        initCompany();
    }

    /**
     * 本地保存快递公司
     */
    private void initCompany() {
        String jsonStr = PreferencesUtils.getString(this, PreferencesUtils.key_all_company, "");
        if(TextUtils.isEmpty(jsonStr)){
            initData();
        }
    }

    private void initData() {
        PostRequest postRequest = OkHttpUtils.post(NetPath.COMPANY)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""));

        postRequest.execute(new AbsCallback<Object>() {
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
                    if (LoginUtils.isLogin(MainActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))) {
                        PreferencesUtils.putString(MainActivity.this,PreferencesUtils.key_all_company,s);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.showOverflowMenu() ;
        getLayoutInflater().inflate(R.layout.toobar_button, toolbar) ;
        TextView center = (TextView) toolbar.findViewById(R.id.id_tool_title);
        center.setText("首页");
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headView = navigationView.getHeaderView(0);
        headView.findViewById(R.id.slide_back_login).setOnClickListener(this);
        headView.findViewById(R.id.slide_msg).setOnClickListener(this);
        headView.findViewById(R.id.slide_change_pas).setOnClickListener(this);
        headView.findViewById(R.id.slide_offline_manger).setOnClickListener(this);
        phone = (TextView) headView.findViewById(R.id.slide_phone);
        phone.setText(PreferencesUtils.getString(this, PreferencesUtils.key_mobile, ""));
        navigationView.setNavigationItemSelectedListener(this);



         findViewById(R.id.home_pickup).setOnClickListener(this);
         findViewById(R.id.home_track).setOnClickListener(this);
         findViewById(R.id.home_difficult).setOnClickListener(this);
         findViewById(R.id.home_send).setOnClickListener(this);
         findViewById(R.id.home_collect).setOnClickListener(this);
         findViewById(R.id.home_site).setOnClickListener(this);



    }

    @Override
    protected void onResume() {
        TextView homeType = (TextView) findViewById(R.id.home_type);
        String packet = PreferencesUtils.getString(this,PreferencesUtils.key_packet,"");
        /**
         * '1' => '总站',
         '2' => '司机',
         '3' => '分站'
         */
        if(packet.equals("1")){
            homeType.setText("当前总站");
            ((TextView)findViewById(R.id.home_current)).setText(PreferencesUtils.getString(this, PreferencesUtils.key_total_substation));
        }else if(packet.equals("2")){
            homeType.setText("当前线路");
            ((TextView)findViewById(R.id.home_current)).setText(
                    PreferencesUtils.getString(this, PreferencesUtils.key_total_substation)
            +" - "+PreferencesUtils.getString(this,PreferencesUtils.key_substation));
        }else if(packet.equals("3")){
            homeType.setText("当前分站");
            ((TextView)findViewById(R.id.home_current)).setText(PreferencesUtils.getString(this, PreferencesUtils.key_substation));
        }

        super.onResume();
    }

    @Override
    protected void onChange(String eventType) {

    }

    @Override
    protected String[] getObserverEventType() {
        return new String[0];
    }

    /**
     * 点击返回键关闭侧滑页面
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //打开侧滑
        if(item.getItemId()==android.R.id.home){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //退出登录
            case R.id.slide_back_login:
                LoadingUtil.showAlertDialog(this, "确定要退出登录吗", new LoadingUtil.MyDialogListenner() {
                    @Override
                    public void callBack(boolean isComfirm) {
                        if(isComfirm){
                            DrawerLayout drawer4 = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer4.closeDrawer(GravityCompat.START);
                            LoginUtils.backLogin(MainActivity.this);
                            startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra(KeyValues.IS_BACK_LOGIN, true));
                        }else {

                        }
                    }
                });

                break;
            //个人信息
            case R.id.slide_msg:
                DrawerLayout drawer1 = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer1.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this,UserInfoActivity.class));
                break;
            //离线管理
            case R.id.slide_offline_manger:
                DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer2.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, OfflineManageActivity.class));
                break;
            //修改密码
            case R.id.slide_change_pas:
                DrawerLayout drawer3 = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer3.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this,ModifyPSDActivity.class));
                break;
            //客户取件
            case R.id.home_pickup:
                startActivity(new Intent(this,CaptureActivity.class).putExtra(KeyValues.SCAN_STASTUS,KeyValues.SCAN_IN_PICKUP));
                break;
            //快递跟踪
            case R.id.home_track:
                startActivity(new Intent(this, TrackActivity.class));
                break;
            //录入收件信息
            case R.id.home_collect:
                startActivity(new Intent(this,CaptureActivity.class).putExtra(KeyValues.SCAN_STASTUS,KeyValues.SCAN_IN_S));
                break;
            //当前站点
            case R.id.home_site:
                break;
            //录入寄件信息
            case R.id.home_send:
                startActivity(new Intent(this,CaptureActivity.class).putExtra(KeyValues.SCAN_STASTUS,KeyValues.SCAN_IN_J));
                break;
            //疑难件处理
            case R.id.home_difficult:
                if(NetworkUtils.isNetworkAvailable(this)){
                    startActivity(new Intent(this,CaptureActivity.class).putExtra(KeyValues.SCAN_STASTUS,KeyValues.SCAN_IN_Y));
                }else {
                    ToastUtil.show(this,"请连接网络后处理疑难件");
                }
                break;
        }
    }
}
