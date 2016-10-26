package com.logistical.fd.bean;

/**
 * Created by admin on 2016/9/20.
 */
public class TotalBean {


    /**
     * back_code : 10000
     * reason : 列表获取成功！
     * total : 0
     */

    public int back_code;
    public String reason;
    public String total;

    public int getBack_code() {
        return back_code;
    }

    public void setBack_code(int back_code) {
        this.back_code = back_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
