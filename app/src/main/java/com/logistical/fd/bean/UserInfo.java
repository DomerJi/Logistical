package com.logistical.fd.bean;

import java.io.Serializable;

/**
 * Created by LiFei on 2016/8/12.
 */
public class UserInfo implements Serializable {

    /**
     * back_code : 10000
     * reason : 操作成功！
     * token : h1ifd7ampdor5a0eh4cvicgtd3
     * tokentime : 1470968103
     * userinfo : {"uid":"8","username":"18677761300","mobile":"18677761300","packet":"3","realname":"wenwen","substation_id":"1","license":null,"substation":null}
     */

    private int back_code;
    private String reason;
    private String token;
    private int tokentime;
    /**
     * uid : 8
     * username : 18677761300
     * mobile : 18677761300
     * packet : 3
     * realname : wenwen
     * substation_id : 1
     * license : null
     * substation : null
     */

    private UserinfoBean userinfo;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTokentime() {
        return tokentime;
    }

    public void setTokentime(int tokentime) {
        this.tokentime = tokentime;
    }

    public UserinfoBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserinfoBean userinfo) {
        this.userinfo = userinfo;
    }

    public static class UserinfoBean {
        private String uid;
        private String username;
        private String mobile;
        private String packet;
        private String realname;
        private String substation_id;
        private Object license;
        private Object substation;
        private String total_substation;

        public String getTotal_substation() {
            return total_substation;
        }

        public void setTotal_substation(String total_substation) {
            this.total_substation = total_substation;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPacket() {
            return packet;
        }

        public void setPacket(String packet) {
            this.packet = packet;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getSubstation_id() {
            return substation_id;
        }

        public void setSubstation_id(String substation_id) {
            this.substation_id = substation_id;
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
