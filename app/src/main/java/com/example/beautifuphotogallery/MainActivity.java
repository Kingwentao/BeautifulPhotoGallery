package com.example.beautifuphotogallery;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.beautifuphotogallery.adapter.BeautifulPhotoAdapter;
import com.example.beautifuphotogallery.adapter.BeautifulPhotoAdapter.OnItemPhotoChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.BlurBitmapUtil;

public class MainActivity extends Activity implements OnItemPhotoChangedListener,PhotoGalleryView.OnItemClickListener {

    PhotoGalleryView mPhotoGalleryView;
    BeautifulPhotoAdapter mBeautifulPhotoAdapter;

    private SeekBar mSeekbar;
    private Map<String, Drawable> mTSDraCacheMap = new HashMap<>();
    private static final String KEY_PRE_DRAW = "key_pre_draw";

    /**
     * 获取虚化背景的位置
     */
    private int mLastDraPosition = -1;
    private RelativeLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mContainer=(RelativeLayout)findViewById(R.id.rl_container);
        mPhotoGalleryView=(PhotoGalleryView)findViewById(R.id.gallery_photo);
        mSeekbar=(SeekBar)findViewById(R.id.seekBar);

        mBeautifulPhotoAdapter = new BeautifulPhotoAdapter(MainActivity.this, getDatas());
        mBeautifulPhotoAdapter.setOnItemPhotoChangedListener(this);

        mPhotoGalleryView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mPhotoGalleryView.setAdapter(mBeautifulPhotoAdapter);

        mPhotoGalleryView.initFlingSpeed(6000)
                .initPageParams(0,25)
                .initPosition(0)
                .setOnItemClickListener(this)
                // 设置切换动画的参数因子
                .setAnimFactor(0f)
                // 设置切换动画类型，目前有AnimManager.ANIM_BOTTOM_TO_TOP和目前有AnimManager.ANIM_TOP_TO_BOTTOM
                .setAnimType(AnimManager.ANIM_BOTTOM_TO_TOP)
                // 设置点击事件
                .setUp();


        mPhotoGalleryView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    setBlurImage(true);
                    mSeekbar.setProgress(mPhotoGalleryView.getScrolledPosition());
                }
            }
        });
    }


    /**
     * 设置高斯模糊
     * @param forceUpdate
     */
    private void setBlurImage(boolean forceUpdate) {
        BeautifulPhotoAdapter adapter=(BeautifulPhotoAdapter) mPhotoGalleryView.getAdapter();
        final int mCurViewPosition=mPhotoGalleryView.getScrolledPosition();

        boolean isSamePosAndNotUpdate = (mCurViewPosition == mLastDraPosition) && !forceUpdate;
        if (adapter == null || mPhotoGalleryView == null || isSamePosAndNotUpdate) {
            return;
        }

        mPhotoGalleryView.post(new Runnable() {
            @Override
            public void run() {

            }
        });
        // 获取当前位置的图片资源ID
        int resourceId=adapter.getResId(mCurViewPosition);
        // 将该资源图片转为Bitmap
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),resourceId);
        // 将该Bitmap高斯模糊后返回到resBlurBmp
        Bitmap resBlurBmp = BlurBitmapUtil.blurBitmap(mPhotoGalleryView.getContext(),bitmap,15);
        // 再将resBlurBmp转为Drawable
        Drawable resBlurDrawable = new BitmapDrawable(resBlurBmp);
        // 获取前一页的Drawable
        Drawable preBlurDrawable = mTSDraCacheMap.get(KEY_PRE_DRAW) == null ? resBlurDrawable : mTSDraCacheMap.get(KEY_PRE_DRAW);
                /* 以下为淡入淡出效果 */
        Drawable[] drawableArr = {preBlurDrawable, resBlurDrawable};
        TransitionDrawable transitionDrawable = new TransitionDrawable(drawableArr);
        mContainer.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(500);

        // 存入到cache中
        mTSDraCacheMap.put(KEY_PRE_DRAW, resBlurDrawable);
        // 记录上一次高斯模糊的位置
        mLastDraPosition = mCurViewPosition;
    }


    /***
     * 测试数据
     * @return List<Integer>
     */
    public List<Integer> getDatas() {
        TypedArray ar = getResources().obtainTypedArray(R.array.test_arr);
        final int[] resIds = new int[ar.length()];
        for (int i = 0; i < ar.length(); i++) {
            resIds[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        List<Integer> tDatas = new ArrayList<>();
        for (int resId : resIds) {
            tDatas.add(resId);
        }
        return tDatas;
    }

    @Override
    public void onItemPhotoChanged() {
        setBlurImage(true);
    }

    @Override
    public void onItemClick(View view, int position) {
            Toast.makeText(getApplicationContext(), "单击了我......", Toast.LENGTH_SHORT).show();
    }



}
