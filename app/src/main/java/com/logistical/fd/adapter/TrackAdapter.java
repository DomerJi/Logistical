package com.logistical.fd.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.logistical.fd.R;
import com.logistical.fd.bean.TrackOrderBean;
import com.logistical.fd.interfaces.MyItemClickListener;

import java.util.List;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyViewHolder> {

    List<TrackOrderBean.ListBean> mListData;
    private MyItemClickListener mItemClickListener;

    public TrackAdapter(List<TrackOrderBean.ListBean> mListData) {
        this.mListData = mListData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,
                viewGroup, false);
        return new MyViewHolder(view,mItemClickListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        myViewHolder.title.setText(mListData.get(i).getOrdersn());
        myViewHolder.track.setText(mListData.get(i).getCompany());
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MyItemClickListener mListener;
        TextView title;
        TextView track;

        public MyViewHolder(View itemView,MyItemClickListener listener) {
            super(itemView);
            this.mListener = listener;
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.listitem_name);  //订单号
            track = (TextView) itemView.findViewById(R.id.listitem_track); //快递
        }

        @Override
        public void onClick(View v) {
            if(mListener!=null){
               // v.findViewById(R.id.listitem_name).setBackgroundResource(R.color.google_green);
                mListener.onItemClick(v,getPosition());
            }
        }
    }

    public void setItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}

