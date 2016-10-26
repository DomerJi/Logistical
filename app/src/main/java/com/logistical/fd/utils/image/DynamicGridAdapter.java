package com.logistical.fd.utils.image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.logistical.fd.R;
import com.logistical.fd.base.MyApplication;
import com.logistical.fd.set.NetPath;


import java.util.List;

public class DynamicGridAdapter extends BaseAdapter {
	private List<String> files;

	private LayoutInflater mLayoutInflater;

	public DynamicGridAdapter(List<String> files, Context context) {
		this.files = files;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return files == null ? 0 : files.size();
	}

	@Override
	public String getItem(int position) {
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyGridViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyGridViewHolder();
			if (getCount() == 1) {
				convertView = mLayoutInflater.inflate(R.layout.gridview_item1,
						parent, false);
			}else if (getCount() == 2 ||getCount() == 4) {
				convertView = mLayoutInflater.inflate(R.layout.gridview_item2,
						parent, false);			}
			else {
				convertView = mLayoutInflater.inflate(R.layout.gridview_item,
						parent, false);
			}


			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.album_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyGridViewHolder) convertView.getTag();
		}
		String url = getItem(position);

//		ImageLoader.getInstance().displayImage("http://laodao.fdwww.cn"+url, viewHolder.imageView);
		Glide.with(MyApplication.getAppContext()).load(NetPath.PATHS + url).centerCrop().into(viewHolder.imageView);

		return convertView;
	}

	private static class MyGridViewHolder {
		ImageView imageView;
	}
}
