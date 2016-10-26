package com.logistical.fd.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2016/8/11.
 */
public class ComPanyBean extends BaseBean<ComPanyBean> implements Parcelable {


    /**
     * id : 1
     * name : 申通快递
     * displayorder : 0
     * status : 1
     * remark :
     * createtime : 1470726652
     */

    private String id;
    private String name;
    private String displayorder;
    private String status;
    private String remark;
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayorder() {
        return displayorder;
    }

    public void setDisplayorder(String displayorder) {
        this.displayorder = displayorder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.displayorder);
        dest.writeString(this.status);
        dest.writeString(this.remark);
        dest.writeString(this.createtime);
    }

    public ComPanyBean() {
    }

    protected ComPanyBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.displayorder = in.readString();
        this.status = in.readString();
        this.remark = in.readString();
        this.createtime = in.readString();
    }

    public static final Parcelable.Creator<ComPanyBean> CREATOR = new Parcelable.Creator<ComPanyBean>() {
        @Override
        public ComPanyBean createFromParcel(Parcel source) {
            return new ComPanyBean(source);
        }

        @Override
        public ComPanyBean[] newArray(int size) {
            return new ComPanyBean[size];
        }
    };
}
