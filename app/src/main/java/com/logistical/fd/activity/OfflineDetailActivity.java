package com.logistical.fd.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.logistical.fd.R;
import com.logistical.fd.base.MyApplication;
import com.logistical.fd.base.ToolBarActivity;
import com.logistical.fd.bean.BatchRecordBean;
import com.logistical.fd.bean.LocalOrdersnBean;
import com.logistical.fd.set.KeyValues;
import com.logistical.fd.set.NetPath;
import com.logistical.fd.utils.ListUtils;
import com.logistical.fd.utils.LoadingUtil;
import com.logistical.fd.utils.PreferencesUtils;
import com.logistical.fd.utils.StringUtils;
import com.logistical.fd.utils.ToastUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class OfflineDetailActivity extends ToolBarActivity {
    private ListView mListView;
    private QuickAdapter mQuickAdapter;
    private List<LocalOrdersnBean> mList;
    private String PathOne;  //单个提交
    private String PathAll;  //全部处理
    private LiteOrm liteOrm;
    private String type;
    private List<String> companyidList;
    private int companyNumber; //公司数量
    private int index=0; //当前已查询的公司数量
    private int successCounts =0; //批量失败数量
    private int errorCounts = 0; //批量成功数量
    private Map<String,List<LocalOrdersnBean>> values;
    private Map<LocalOrdersnBean,String> mMap ;

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_offline_detail);
    }

    @Override
    public void initView() {
        mListView = (ListView) findViewById(R.id.offline_detail_lv);
        mList = new ArrayList<>();
        //接受上一个界面传的订单类型
        type = getIntent().getStringExtra("type");
        switch (type){
            case "1":
                PathOne = NetPath.SCAN;
                PathAll = NetPath.BATCH_RECORD;
                break;
            case "2":
                PathOne = NetPath.SCAN;
                PathAll = NetPath.BATCH_RECORD;
                break;
            case "4":
                PathOne = NetPath.PICKUP;
                PathAll = NetPath.BATCH_DELIVER;
                break;
        }

        MyApplication myApplication = (MyApplication) getApplication();
        liteOrm = myApplication.liteOrm;
        //由type查询对应的订单列表
         mList = liteOrm.query(new QueryBuilder<LocalOrdersnBean>(LocalOrdersnBean.class)
                 .where("type" + " LIKE ?", new String[]{type}));
        //排序
        Collections.sort(mList, new Comparator<LocalOrdersnBean>() {

            public int compare(LocalOrdersnBean o1, LocalOrdersnBean o2) {
                String Ordersn1 = filterUnNumber(o1.getOrdersn());
                String Ordersn2 = filterUnNumber(o2.getOrdersn());
                String numStr1 = Ordersn1;
                String numStr2 = Ordersn2;
                if (Ordersn1.length() > 18) {
                    numStr1 = o1.getOrdersn().substring(o1.getOrdersn().length() - 18, o1.getOrdersn().length());
                }
                if (Ordersn2.length() > 18) {
                    numStr2 = o2.getOrdersn().substring(o2.getOrdersn().length() - 18, o2.getOrdersn().length());
                }
                if (!StringUtils.isEmpty(numStr1) && !StringUtils.isEmpty(numStr2)) {
                    if (Long.valueOf(numStr1) > Long.valueOf(numStr2)) {
                        return 1;
                    }
                }

                return -1;
            }
        });
        mQuickAdapter = new QuickAdapter<LocalOrdersnBean>(this,R.layout.item_offlinedetail,mList) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final LocalOrdersnBean item) {
                helper.setText(R.id.item_offlinedetail_number,item.getOrdersn())
                .setText(R.id.item_offlinedetail_status,item.getFlag()==0?"未处理":"处理失败");
                helper.setOnClickListener(R.id.item_offlinedetail_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DealOne(item);
                    }
                });
                helper.setOnLongClickListener(R.id.item_offlinedetail, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LoadingUtil.showAlertDialog(OfflineDetailActivity.this, "此订单号处理失败,确认删除?", new LoadingUtil.MyDialogListenner() {
                            @Override
                            public void callBack(boolean isComfirm) {
                                if(isComfirm){
                                    liteOrm.delete(item);
                                    mList.remove(item);
                                    mQuickAdapter.remove(item);
                                    ToastUtil.show(OfflineDetailActivity.this, "删除成功!");
                                }else {

                                }
                            }
                        });
                        return false;
                    }
                });
            }

        };
        mListView.setAdapter(mQuickAdapter);
    }

    //设置toolbar
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        setCenterTitle("离线管理");
        setRightTitle("全部处理");
        //全部处理点击事件
        setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealAll();
            }
        });
    }

    //单个处理
    public void DealOne(final LocalOrdersnBean item){

        PostRequest request = OkHttpUtils.post(PathOne)
                .params("token", PreferencesUtils.getString(OfflineDetailActivity.this, "token"))
                .params("ordersn", item.getOrdersn())
                .params("type", item.getType()== KeyValues.SCAN_IN_J?"1":"0")
                .params("companyid", item.getCompanyid());
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
                        //从本地数据库中移除
                        liteOrm.delete(item);
                        mList.remove(item);
                        mQuickAdapter.remove(item);
                        ToastUtil.show(OfflineDetailActivity.this, "操作成功!");

                    } else {
                        //list中改变状态
                        item.setFlag(1);
                        mQuickAdapter.notifyDataSetChanged();
                        //数据库中改变状态
                        liteOrm.update(item);
                        ToastUtil.show(OfflineDetailActivity.this, reason);

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //处理全部
    public void dealAll(){
        successCounts = 0;
        errorCounts = 0;
        if (mList.size()>0){
            //弹出dialog
            LoadingUtil.backCancel = false;
            LoadingUtil.show(this, "处理中",R.color.white);
            index =0;
            companyidList = new ArrayList<>();

            //将list中的数据按公司id存到map中
            mMap = new HashMap<>();
            for (LocalOrdersnBean i:mList) {
                if (!companyidList.contains(i.getCompanyid())){
                    companyidList.add(i.getCompanyid());
                }
                mMap.put(i, i.getCompanyid());
            }
            values = new HashMap();
            List list = new ArrayList();
            Iterator iterator = mMap.keySet().iterator();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                String value = mMap.get(key);
                if (mMap.containsValue(value)) {
                    if (values.containsKey(value)) {
                        list = (List) values.get(value);
                    } else {
                        list = new ArrayList();
                    }
                    list.add(key);
                    values.put( value, list);
                }
            }
            companyNumber = companyidList.size();
            //访问网络进行处理
            if(!ListUtils.isEmpty(companyidList)&&companyNumber>0){
                deal(companyidList.get(index));
            }
        }


    }

    private void deal(final String id){
        //拼接ordersn
        List<LocalOrdersnBean> list = values.get(id);
        StringBuffer ordersnStr = new StringBuffer();
        for (int i=0;i<list.size();i++){
            ordersnStr.append(list.get(i).getOrdersn()+",");
        }

        PostRequest request = OkHttpUtils.post(PathAll)
                .params("token", PreferencesUtils.getString(OfflineDetailActivity.this, "token"))
                .params("ordersn", String.valueOf(ordersnStr))
                .params("type", type.equals(KeyValues.SCAN_IN_J + "")?"1":"0")
                .params("companyid", id);
        request.execute(new AbsCallback<Object>() {
            @Override
            public Object parseNetworkResponse(Response response) {
                return null;
            }


            @Override
            public void onAfter(boolean isFromCache, Object o, Call call, Response response, Exception e) {
                super.onAfter(isFromCache, o, call, response, e);
                index++;
                if (index<companyNumber){
                    deal(companyidList.get(index));
                }else {
                    mQuickAdapter.notifyDataSetChanged();
                    LoadingUtil.dismis();
                }

            }

            @Override
            public void onResponse(boolean isFromCache, Object o, Request request, Response response) {
                try {
                    String s = response.body().string();
                    JSONObject jsonObject = new JSONObject(s);
                    int back_code = jsonObject.optInt("back_code");
                    String reason = jsonObject.optString("reason");
                    int successCount = jsonObject.optInt("successCount");
                    int errorCount = jsonObject.optInt("errorCount");

                    if (back_code == 10000) {
                        if (index==0){
                            liteOrm.delete(mList);
                            mList.clear();
                        }

                        BatchRecordBean batchRecords = new Gson().fromJson(s,BatchRecordBean.class);
//                        List<BatchRecordBean.SuccessListBean> successList =  batchRecords.getSuccessList();
//                        for (BatchRecordBean.SuccessListBean i:successList){
//                            LocalOrdersnBean ordersnBean = new LocalOrdersnBean();
//                            ordersnBean.setCompanyid(id);
//                            ordersnBean.setType(Integer.valueOf(type));
//                            ordersnBean.setOrdersn(i.getOrdersn());
//                            ordersnBean.setOnly_id(i.getOrdersn() + id + type);
//                            liteOrm.delete(ordersnBean);
//                        }
                        List<BatchRecordBean.ErrorListBean> errorList =  batchRecords.getErrorList();
                        for (BatchRecordBean.ErrorListBean i:errorList){
                            LocalOrdersnBean ordersnBean = new LocalOrdersnBean();
                            ordersnBean.setCompanyid(id);
                            ordersnBean.setType(Integer.valueOf(type));
                            ordersnBean.setOrdersn(i.getOrdersn());
                            ordersnBean.setOnly_id(i.getOrdersn() + id + type);
                            ordersnBean.setFlag(1);
                            mList.add(ordersnBean);
                        }
                        successCounts+=successCount;
                        errorCounts+=errorCount;
                        if (index==companyNumber-1) {

                            //排序
                            Collections.sort(mList, new Comparator<LocalOrdersnBean>() {

                                public int compare(LocalOrdersnBean o1, LocalOrdersnBean o2) {
                                    String Ordersn1 = filterUnNumber(o1.getOrdersn());
                                    String Ordersn2 = filterUnNumber(o2.getOrdersn());
                                    String numStr1 = Ordersn1;
                                    String numStr2 = Ordersn2;
                                    if (Ordersn1.length() > 18) {
                                        numStr1 = o1.getOrdersn().substring(o1.getOrdersn().length() - 18, o1.getOrdersn().length());
                                    }
                                    if (Ordersn2.length() > 18) {
                                        numStr2 = o2.getOrdersn().substring(o2.getOrdersn().length() - 18, o2.getOrdersn().length());
                                    }
                                    if (!StringUtils.isEmpty(numStr1) && !StringUtils.isEmpty(numStr2)) {
                                        if (Long.valueOf(numStr1) > Long.valueOf(numStr2)) {
                                            return 1;
                                        }
                                    }

                                    return -1;
                                }
                            });
                            mQuickAdapter.replaceAll(mList);
                            liteOrm.save(mList);
                            ToastUtil.show(OfflineDetailActivity.this, "批量操作成功!成功" + successCounts + "个,失败" + errorCounts + "个");
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static String filterUnNumber(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        java.util.regex.Matcher m = p.matcher(str);
        return m.replaceAll("").trim();

    }


}
