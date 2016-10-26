package com.logistical.fd.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.logistical.fd.R;
import com.logistical.fd.adapter.ImagePublishAdapter;
import com.logistical.fd.bean.ComPanyBean;
import com.logistical.fd.model.ImageItem;
import com.logistical.fd.notify.BaseObserverActivity;
import com.logistical.fd.notify.EventType;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.LogUtil;
import com.logistical.fd.utils.LoginUtils;
import com.logistical.fd.utils.PictureUtil;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.ToastUtil;
import com.logistical.fd.utils.ToolsCallback;
import com.logistical.fd.utils.image.DynamicGridAdapter;
import com.logistical.fd.utils.image.ImageBucketChooseActivity;
import com.logistical.fd.utils.image.ImageChooseActivity;
import com.logistical.fd.utils.image.ImagePagerActivity;
import com.logistical.fd.utils.image.ImageZoomActivity;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import photoview.CustomConstants;
import photoview.IntentConstants;

public class DifficultActivity extends BaseObserverActivity implements View.OnClickListener{
    private GridView mGridView;                       //网格显示缩略图


    private EditText deoptions;
    private CheckedTextView selectType;
    private TextView company;
    private EditText ordersn;
    private String ordersnStr;

    private ComPanyBean comPanyBean;
    private JSONArray list;

    private String dealtype;

    private SparseArray  typeArrayName = new SparseArray();

    private SparseArray  typeArray = new SparseArray();

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_difficult);

        setCenterTitle("疑难件处理");
        setRightTitle("提交");

        setRightOnClickListener(this);


        addImageInitData();
        mGridView = (GridView) findViewById(R.id.post_gridView);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        addImageInitView(mGridView, this);

    }

    @Override
    public void initView() {

        ordersnStr = getIntent().getStringExtra(KeyValues.ORDERSN);

        deoptions = (EditText) findViewById(R.id.y_deoptions);

        selectType = (CheckedTextView) findViewById(R.id.y_select_type);
        selectType.setOnClickListener(this);

        company = (TextView) findViewById(R.id.y_company);
        company.setOnClickListener(this);

        ordersn = (EditText) findViewById(R.id.y_ordersn);
        ordersn.setText(ordersnStr);
        ordersn.setOnClickListener(this);

        showCompany();
    }


    /**
     * 显示当前选择的快递公司
     */
    private void showCompany(){
        int current = PreferencesUtils.getInt(DifficultActivity.this, PreferencesUtils.key_current_company_int, -1);
        if(current!=-1){
            String s = PreferencesUtils.getString(DifficultActivity.this,PreferencesUtils.key_current_company_obj,"");
            if(s.length()>0){
                comPanyBean = new Gson().fromJson(s, ComPanyBean.class);
                if(comPanyBean!=null){
                    if (company!=null){
                        company.setText(comPanyBean.getName());
                    }
                }
            }
        }
    }


    @Override
    protected void onChange(String eventType) {
//        if(eventType == EventType.UPDATE_COMPANY){
//            showCompany();
//        }
    }


    /**
     * 初始化带条目的Dialog
     * @param cs
     */
    private void initItemDialog(CharSequence[] cs){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择疑难件类型");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dealtype = typeArray.get(which).toString();
                dialog.dismiss();
                selectType.setText(typeArrayName.get(which).toString());
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    protected String[] getObserverEventType() {
        return new String[]{EventType.UPDATE_COMPANY};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.y_company://选择公司
                startActivityForResult(
                        new Intent(this,SelectCompanyActivity.class)
                                .putExtra(KeyValues.SOMPANY_SELECTED_K,KeyValues.SOMPANY_SELECTED_V),10);
                break;
            case R.id.y_ordersn://订单号
                break;
            case R.id.y_select_type://选择疑难件
                requestType();
                break;
            case R.id.id_right://提交
                requestSubmit();
                break;
            default:
                break;
        }
    }

    private void requestSubmit(){

        RequestBody requestBody = null;
        MultipartBody.Builder  builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""));
        String orderS = ordersn.getText().toString().trim();
        if (TextUtils.isEmpty(orderS)){
            ToastUtil.show(DifficultActivity.this,"请输入快递单号");
            return;
        }else {
            builder.addFormDataPart("ordersn", orderS);
        }


        if(TextUtils.isEmpty(dealtype)){
            ToastUtil.show(DifficultActivity.this,"请选择疑难类型");
            return;
        }else {
            builder.addFormDataPart("dealtype", dealtype);
        }

        String deoptionStr = deoptions.getText().toString().trim();
        if(deoptionStr.length()>0){
            builder.addFormDataPart("description", deoptionStr);
        }


        if(addImageList.size()>0){
            int size = addImageList.size();
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

            for(int i = 0;i < size; i++){
                LogUtil.e("image","dddddddddddddddddddddddd____"+i);
                File file = null;
                FileOutputStream fos = null;
                try {
                    File f = new File(addImageList.get(i).sourcePath);
                    String filePath = "/data/data/" + getPackageName() + "/" + "cache/" + f.getName();
                    file = new File(addImageList.get(i).sourcePath);
//                    Bitmap bm = PictureUtil.getSmallBitmap(addImageList.get(i).sourcePath);
//                    fos = new FileOutputStream(file);
//                    bm.compress(Bitmap.CompressFormat.PNG, 70, fos);
                        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_PNG, file);
                        builder.addFormDataPart("image[]", file.getName(),fileBody);
                } catch (Exception e) {
                }finally {
                    try {
                        if(fos!=null){
                            fos.flush();
                            fos.close();
                        }

                    } catch (IOException e) {
                    }

                }

            }
        }
        requestBody = builder.build();
        final String IMGUR_CLIENT_ID = "...";

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .url(NetPath.DIFFICULT)
                .post(requestBody)
                .build();

        LoadingUtil.show(this,"正在提交");
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.obj = "提交失败";
                msg.what = 0;
                handler.sendMessage(msg);
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String s = response.body().string();
                    JSONObject jsonobj = new JSONObject(s);
                    LogUtil.e("UP___________", s);
                    //判断登录是否成功s
                    if (LoginUtils.isLogin(DifficultActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))) {

                        Message msg = new Message();
                        msg.obj = jsonobj.optString("reason");
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.obj = jsonobj.optString("reason");
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            LoadingUtil.dismis();
            ToastUtil.show(DifficultActivity.this, (String) msg.obj);
            if(msg.what==1){//成功

            }else if(msg.what == 2){//失败

            }
            super.handleMessage(msg);
        }
    };

    private void requestType(){

        PostRequest request = OkHttpUtils.post(NetPath.DIFFICULT_TYPE)
                .params("token", PreferencesUtils.getString(this, PreferencesUtils.key_token, ""));

        request.execute(new ToolsCallback<Object>(DifficultActivity.this, "正在加载") {
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
                    if(LoginUtils.isLogin(DifficultActivity.this, jsonobj.optInt(KeyValues.BACK_CODE, 0))){

                        ToastUtil.show(DifficultActivity.this,jsonobj.optString("reason"));

                        list = jsonobj.optJSONArray("list");
                        int len = list.length();

                        CharSequence[] cs = new CharSequence[len];

                        for (int i = 0;i < len; i++){
                            cs[i] =  list.optJSONObject(i).optString("name");
                            typeArray.put(i, list.optJSONObject(i).optString("id"));
                            typeArrayName.put(i,cs[i]);
                        }
                        initItemDialog(cs);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    //添加数据到集合
    @SuppressWarnings("unchecked")
    public void addImageInitData() {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
        if (incomingDataList != null) {
            addImageList.addAll(incomingDataList);
        }
    }

    //得到集合元素个数
    public int getDataSize() {
        return addImageList == null ? 0 : addImageList.size();
    }

    //得到还可以添加的元素个数
    public int getAvailableSize() {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - addImageList.size();
        if (availSize >= 0) {
            return availSize;
        }
        return 0;
    }

    //拍照
    public void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        addImagePath = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(final Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.item_popupwindow, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    takePhoto();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,
                            ImageBucketChooseActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                            getAvailableSize());
                    startActivityForResult(intent, IMAGE_OPEN);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }



    //添加图片功能
    public static List<ImageItem> addImageList = new ArrayList<ImageItem>();   //图片信息存储,用于适配器
    public static String addImagePath = "";
    public static final int TAKE_PICTURE = 0x000010;              //拍照标记
    public static final int IMAGE_OPEN = 0x000011;                        //相册标记
    public static final int GET_DATA = 0x000012;                          //返回数据标记
    public static ImagePublishAdapter addImageAdapter;           //图片适配器

    public List<ImagePublishAdapter> adapterList = new ArrayList<>();

    //存储集合中数据
    public void saveTempToPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = JSON.toJSONString(addImageList);
        sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();

    }

    //移除存储的数据
    public void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    //得到存储的数据
    public void getTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
        if (!TextUtils.isEmpty(prefStr)) {
            List<ImageItem> tempImages = JSON.parseArray(prefStr,
                    ImageItem.class);
            addImageList = tempImages;
        }
    }

    //异常结束时保存集合里的数据
    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveTempToPref();
    }

    @Override
    protected void onResume() {
        for (int i = 0; i < adapterList.size(); i++) {
            adapterList.get(i).notifyDataSetChanged(); //当在ImageZoomActivity中删除图片时，返回这里需要刷新
        }
        super.onResume();
    }

    public void addImageInitView(final GridView mGridView, final Context mContext) {
        addImageAdapter = new ImagePublishAdapter(this, addImageList);
        adapterList.add(addImageAdapter);
        mGridView.setAdapter(addImageAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    new PopupWindows(mContext, mGridView);
                } else {
                    Intent intent = new Intent(mContext,
                            ImageZoomActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                            (Serializable) addImageList);
                    intent.putExtra("type", 0);
                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION, position);
                    startActivity(intent);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (addImageList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(addImagePath)) {
                    ImageItem item = new ImageItem();
                    item.sourcePath = addImagePath;
                    addImageList.add(item);
                }
                break;

            case IMAGE_OPEN:
                if (addImageList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1) {
                    data.setClass(this, ImageChooseActivity.class);
                    startActivityForResult(data, GET_DATA);
                }
                break;
            case GET_DATA:
                if (addImageList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1) {
                    List<ImageItem> DataList = (List<ImageItem>) data.getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
                    addImageList.addAll(DataList);
                }
                break;
        }

        if(requestCode==10&&resultCode==KeyValues.RESULT_OK&&data!=null){
            comPanyBean = data.getParcelableExtra(KeyValues.RESULT_COMPANY);
            company.setText(comPanyBean.getName());
        }


    }

    //-------------------------------------------------------------------------
    //九宫格展示图片
    public void loadImage(final ArrayList<String> urls, final Context mContext, GridView gv_image) {
        if (urls == null) {
            gv_image.setVisibility(View.GONE);
        } else {
            if(urls.size()<=0){
                gv_image.setVisibility(View.GONE);
            }else if(TextUtils.isEmpty(urls.get(0))){
                gv_image.setVisibility(View.GONE);
            }else {
                gv_image.setVisibility(View.VISIBLE);
                DynamicGridAdapter adapter = new DynamicGridAdapter(urls, mContext);
                gv_image.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(mContext, ImagePagerActivity.class);
                        intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                        startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        addImageList.clear();
        removeTempFromPref();
        super.onDestroy();
    }
}