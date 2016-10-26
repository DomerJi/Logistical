package com.logistical.fd.base;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.logistical.fd.R;


/**
 * Created by jsp on 2016/8/3.
 * time : 10:26
 */
public abstract class ToolBarActivity extends BaseActivity {
    private ToolBarHelper mToolBarHelper ;
    public Toolbar toolbar ;

    public TextView right;
    public TextView center;

    private  boolean showToolBar = true;

    private LeftOnClickListener clickListener;
    private ImageView rightImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        if(showToolBar){
            mToolBarHelper = new ToolBarHelper(this,layoutResID) ;
            toolbar = mToolBarHelper.getToolBar() ;
            setContentView(mToolBarHelper.getContentView());
        /*把 toolbar 设置到Activity 中*/
            setSupportActionBar(toolbar);
        /*自定义的一些操作*/
            onCreateCustomToolBar(toolbar) ;
        }else {
            super.setContentView(layoutResID);
        }

    }

    /**
     *  禁止显示ToolBar 需要在 setContentView()前设置
     * */
    public void requestNoToolBar(){
        showToolBar = false;
    }

    public void onCreateCustomToolBar(Toolbar toolbar){
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.showOverflowMenu() ;
        getLayoutInflater().inflate(R.layout.toobar_button, toolbar) ;
        right = (TextView) toolbar.findViewById(R.id.id_right);
        center = (TextView) toolbar.findViewById(R.id.id_tool_title);
        rightImg = (ImageView) toolbar.findViewById(R.id.id_right_img);
    }

    public void setLeftOnClickListener(LeftOnClickListener onClickListener){
       this.clickListener = onClickListener;
    }

    public interface LeftOnClickListener{
        void onClick();
    }

    public void setCenterOnClickListener(View.OnClickListener onClickListener){
        center.setOnClickListener(onClickListener);
    }

    public void setRightOnClickListener(View.OnClickListener onClickListener){
        if(right.getVisibility()==View.VISIBLE){
            right.setOnClickListener(onClickListener);
        }else {
            rightImg.setOnClickListener(onClickListener);
        }

    }

    public void setLeftTitle(String title){
        toolbar.setSubtitle(title);
    }

    public void setLeftHide(){
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
    }

    public void setLeftIcon(int res){
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(res);
    }

    public void setCenterTitle(String title) {
        center.setText(title);
    }

    public void setCenterTitleColor(int color){
        center.setTextColor(color);
    }

    public void setCenterIcon(int res){
        center.setBackgroundResource(res);
    }

    public void setRightTitle(String title) {
        right.setText(title);
    }

    public void setRightTitleColor(int color){
        right.setTextColor(color);
    }

    public void setRightIcon(int res){

        right.setVisibility(View.GONE);
        rightImg.setImageResource(res);
        rightImg.setVisibility(View.VISIBLE);
        rightImg.setColorFilter(Color.WHITE);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRightIconByTitleLeft(int res){
        right.setCompoundDrawables(getDrawable(res), null, null, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRightIconByTitleTop(int res){
        right.setCompoundDrawables(null,getDrawable(res),null,null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRightIconByTitleRight(int res){
        right.setCompoundDrawables(null,null,getDrawable(res),null);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRightIconByTitleBottom(int res){
        right.setCompoundDrawables(null,null,null,getDrawable(res));
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if(null == clickListener){
                finish();
            }else {
                clickListener.onClick();
            }

            return true ;
        }
        return super.onOptionsItemSelected(item);
    }
}