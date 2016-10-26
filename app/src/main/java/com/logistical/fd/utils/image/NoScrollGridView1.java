package com.logistical.fd.utils.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NoScrollGridView1 extends GridView {

	public NoScrollGridView1(Context context) {
		super(context);
	}

	public NoScrollGridView1(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int  expandSpec = 0;
	    int  size = getAdapter().getCount();
	    
		if (size == 1) {

			setNumColumns(1);
		}
		if ( size==2 || size == 4  ) {
			setNumColumns(2);
		}
		else {
			setNumColumns(3);
		}
		expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec,expandSpec );
	}

}
