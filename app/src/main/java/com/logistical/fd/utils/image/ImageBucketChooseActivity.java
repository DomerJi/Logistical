package com.logistical.fd.utils.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


import com.logistical.fd.MainActivity;
import com.logistical.fd.R;
import com.logistical.fd.adapter.ImageBucketAdapter;
import com.logistical.fd.model.ImageBucket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import photoview.CustomConstants;
import photoview.IntentConstants;

/**
 * 选择相册
 * 
 */

public class ImageBucketChooseActivity extends Activity
{
	private ImageFetcher mHelper;
	private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
	private ListView mListView;
	private ImageBucketAdapter mAdapter;
	private int availableSize;
	private static final int IMAGE_OPEN = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_image_bucket_choose);
//		mHelper = ImageFetcher.getInstance(getApplicationContext());
		mHelper = new ImageFetcher(ImageBucketChooseActivity.this);
		initData();
		initView();
	}

	private void initData()
	{
		mDataList = mHelper.getImagesBucketList(false);
		availableSize = getIntent().getIntExtra(
				IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
				CustomConstants.MAX_IMAGE_SIZE);
	}

	private void initView()
	{
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ImageBucketAdapter(this, mDataList);
		mListView.setAdapter(mAdapter);
		TextView titleTv  = (TextView) findViewById(R.id.title);
		titleTv.setText("相册");
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				selectOne(position);

				Intent intent = new Intent(ImageBucketChooseActivity.this,
						ImageChooseActivity.class);
				intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
						(Serializable) mDataList.get(position).imageList);
				intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
						mDataList.get(position).bucketName);
				intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
						availableSize);

				setResult(RESULT_OK, intent);
				ImageBucketChooseActivity.this.finish();
			}
		});

		TextView cancelTv = (TextView) findViewById(R.id.action);
		cancelTv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(ImageBucketChooseActivity.this,
						MainActivity.class);

				startActivity(intent);
				ImageBucketChooseActivity.this.finish();
			}
		});
	}

	private void selectOne(int position)
	{
		int size = mDataList.size();
		for (int i = 0; i != size; i++)
		{
			if (i == position) mDataList.get(i).selected = true;
			else
			{
				mDataList.get(i).selected = false;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

}
