package com.logistical.fd.bean;

import java.util.List;

/**
 * Created by LiFei on 2016/8/16.
 */
public class OrderInfoBean {

    /**
     * back_code : 10000
     * reason : 详情信息获取成功！
     * list : [{"createtime":"1471253414","description":"bowen收货","iserror":"0","ordersn":"6901028300056","remark":"","type":"0","status":"1","company":"顺丰快递","realname":"bowen","license":null,"substation":null},{"createtime":"1471253622","description":"bowen变更疑难件","iserror":"1","ordersn":"6901028300056","remark":"","type":"0","status":"2","company":"顺丰快递","realname":"bowen","license":null,"substation":null}]
     */

    private int back_code;
    private String reason;
    /**
     * createtime : 1471253414
     * description : bowen收货
     * iserror : 0
     * ordersn : 6901028300056
     * remark :
     * type : 0
     * status : 1
     * company : 顺丰快递
     * realname : bowen
     * license : null
     * substation : null
     */

    private List<ListBean> list;

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

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String createtime;
        private String description;
        private String iserror;
        private String ordersn;
        private String remark;
        private String type;
        private String status;
        private String company;
        private String realname;
        private Object license;
        private Object substation;

        public String getCreatetime() {
            return createtime;
        }

        public ListBean setCreatetime(String createtime) {
            this.createtime = createtime;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public ListBean setDescription(String description) {
            this.description = description;
            return this;
        }

        public String getIserror() {
            return iserror;
        }

        public void setIserror(String iserror) {
            this.iserror = iserror;
        }

        public String getOrdersn() {
            return ordersn;
        }

        public void setOrdersn(String ordersn) {
            this.ordersn = ordersn;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public Object getLicense() {
            return license;
        }

        public void setLicense(Object license) {
            this.license = license;
        }

        public Object getSubstation() {
            return substation;
        }

        public void setSubstation(Object substation) {
            this.substation = substation;
        }
    }


}
