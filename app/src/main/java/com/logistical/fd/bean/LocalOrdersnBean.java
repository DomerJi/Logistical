package com.logistical.fd.bean;

import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * Created by admin on 2016/8/15.
 */
public class LocalOrdersnBean {

    /**
     *
        only_id = ordersn + companyid + type;
     */
    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.BY_MYSELF)
    // 非空字段
    @NotNull
    public String only_id;
    // 非空字段
    @NotNull
    public String ordersn;
    // 非空字段
    @NotNull
    public String companyid;

    public int flag = 0;
    @NotNull
    public String yyyyMMdd;

    /**
     *    public final static int SCAN_IN_S = 1;//收件

            public final static int SCAN_IN_J = 2;//寄件

            public final static int SCAN_IN_Y = 3;//疑难件

            public final static int SCAN_IN_PICKUP = 4;//取件
     */
    // 非空字段
    @NotNull
    public int type;

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getOnly_id() {
        return only_id;
    }

    public void setOnly_id(String only_id) {
        this.only_id = only_id;
    }

    public String getOrdersn() {
        return ordersn;
    }

    public void setOrdersn(String ordersn) {
        this.ordersn = ordersn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
