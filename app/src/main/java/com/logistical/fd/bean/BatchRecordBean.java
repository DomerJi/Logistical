package com.logistical.fd.bean;

import java.util.List;

/**
 * Created by Feics on 2016/8/15.
 */
public class BatchRecordBean {


    /**
     * back_code : 10000
     * reason : 批量操作成功！
     * successList : [{"ordersn":"EA005","reason":"操作成功！"}]
     * errorList : [{"ordersn":"EA001","reason":"该单号已经扫描，请不要重复扫描！"},{"ordersn":"EA002","reason":"该单号已经扫描，请不要重复扫描！"},{"ordersn":"EA003","reason":"该单号已经扫描，请不要重复扫描！"},{"ordersn":"EA004","reason":"该单号已经扫描，请不要重复扫描！"}]
     * count : 5
     * successCount : 1
     * errorCount : 4
     */

    private int back_code;
    private String reason;
    private int count;
    private int successCount;
    private int errorCount;
    /**
     * ordersn : EA005
     * reason : 操作成功！
     */

    private List<SuccessListBean> successList;
    /**
     * ordersn : EA001
     * reason : 该单号已经扫描，请不要重复扫描！
     */

    private List<ErrorListBean> errorList;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<SuccessListBean> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<SuccessListBean> successList) {
        this.successList = successList;
    }

    public List<ErrorListBean> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorListBean> errorList) {
        this.errorList = errorList;
    }

    public static class SuccessListBean {
        private String ordersn;
        private String reason;

        public String getOrdersn() {
            return ordersn;
        }

        public void setOrdersn(String ordersn) {
            this.ordersn = ordersn;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class ErrorListBean {
        private String ordersn;
        private String reason;

        public String getOrdersn() {
            return ordersn;
        }

        public void setOrdersn(String ordersn) {
            this.ordersn = ordersn;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
