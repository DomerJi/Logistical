package com.logistical.fd.set;

/**
 * Created by admin on 2016/7/20.
 */
public class NetPath {

    public static  String PATHS ="http://www.laodaolife.com";

    public static  String PATH ="http://wl.fd.monihe.com";

    public static String APP_VISION = PATH+"/update/android";//更新版本

    public static  String LOGIN_PATH = PATH+"/app/login/dologin";  //登录

    public static  String USER_INFO = PATH+"/app/user/info";  //个人信息

    public static  String LOGOUT = PATH+"/app/user/logout";  //退出登录

    public static  String ModifyPSD = PATH+"/app/user/editPass";  //修改密码

    public static String  COMPANY = PATH+"/app/express/companyList";   //快递公司列表

    public static String PICKUP = PATH+"/app/express/deliver";//客户取件

    public static String BATCH_RECORD = PATH+"/app/express/batchRecord"; //批量扫描/手工录入

    public static String SCAN = PATH+"/app/express/record";//快递信息扫描录入

    public static String DIFFICULT = PATH+"/app/express/difficult";//疑难件处理

    public static String DIFFICULT_TYPE = PATH+"/app/express/difficultType";//疑难件处理类型列表

    public static String SEARCH = PATH+"/app/express/search";//快递跟踪-收件/寄件列表，快递单查询接口

    public static String BATCH_DELIVER = PATH+"/app/express/batchDeliver";  //用户批量取件

    public static String TRACK_INFO = PATH+"/app/express/info";  //快递单详情

    public static String SMS = PATH+"/app/sms/index";//发送短信

    public static String RESET_PASSWORD = PATH+"/app/public/restPassword";  //忘记密码

    public static String CANCEL_ORDER = PATH+"/app/express/cancel";//撤销本次扫描

    public static String COUNT_TOTAL = PATH+"/app/express/gettotal";//收件 寄件数量

}
