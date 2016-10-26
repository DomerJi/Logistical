package com.logistical.fd.bean;

import java.util.List;

/**
 * Created by Feics on 2016/8/15.
 */
public class TrackOrderBean {

    /**
     * back_code : 10000
     * reason : 列表获取成功！
     * list : [{"id":"25","company_id":"1","ordersn":"EA001","status":"2","type":"0","remark":null,"createtime":"1471223619","updatetime":"1471231098","handle_user_id":"2","iserror":"0","company":"申通快递"},{"id":"26","company_id":"1","ordersn":"EA002","status":"1","type":"0","remark":null,"createtime":"1471223619","updatetime":"1471223619","handle_user_id":"2","iserror":"0","company":"申通快递"},{"id":"27","company_id":"1","ordersn":"EA003","status":"1","type":"0","remark":null,"createtime":"1471223640","updatetime":"1471223640","handle_user_id":"2","iserror":"0","company":"申通快递"},{"id":"28","company_id":"1","ordersn":"EA004","status":"1","type":"0","remark":null,"createtime":"1471223640","updatetime":"1471223640","handle_user_id":"2","iserror":"0","company":"申通快递"},{"id":"29","company_id":"1","ordersn":"EA005","status":"1","type":"0","remark":null,"createtime":"1471228394","updatetime":"1471228394","handle_user_id":"2","iserror":"0","company":"申通快递"},{"id":"30","company_id":"1","ordersn":"6901028080439","status":"1","type":"0","remark":"","createtime":"1471228604","updatetime":"1471228604","handle_user_id":"7","iserror":"0","company":"申通快递"},{"id":"32","company_id":"2","ordersn":"6901028189040","status":"2","type":"0","remark":"","createtime":"1471231021","updatetime":"1471231073","handle_user_id":"7","iserror":"0","company":"顺丰快递"},{"id":"31","company_id":"3","ordersn":"6901028065559","status":"2","type":"0","remark":"","createtime":"1471230568","updatetime":"1471230669","handle_user_id":"7","iserror":"0","company":"EMS"}]
     * page : 1
     * size : 10
     */

    private int back_code;
    private String reason;
    private String page;
    private String size;
    /**
     * id : 25
     * company_id : 1
     * ordersn : EA001
     * status : 2
     * type : 0
     * remark : null
     * createtime : 1471223619
     * updatetime : 1471231098
     * handle_user_id : 2
     * iserror : 0
     * company : 申通快递
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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String id;
        private String company_id;
        private String ordersn;
        private String status;
        private String type;
        private Object remark;
        private String createtime;
        private String updatetime;
        private String handle_user_id;
        private String iserror;
        private String company;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getOrdersn() {
            return ordersn;
        }

        public void setOrdersn(String ordersn) {
            this.ordersn = ordersn;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getRemark() {
            return remark;
        }

        public void setRemark(Object remark) {
            this.remark = remark;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getHandle_user_id() {
            return handle_user_id;
        }

        public void setHandle_user_id(String handle_user_id) {
            this.handle_user_id = handle_user_id;
        }

        public String getIserror() {
            return iserror;
        }

        public void setIserror(String iserror) {
            this.iserror = iserror;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }
    }
}
