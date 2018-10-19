package com.example.beautifuphotogallery;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import util.OsUtil;

/**
 * Created by 金文韬 on 2018/10/16.
 */

public class ScrollManager {
    private static final String TAG = "ScrollManager";
    private PhotoGalleryView mPhotoGalleryView;
    private int mPosition=0;
    //x方向消耗的距离=左边距+可视化宽度
    private int mConsumeX=0;
    private int mConsumeY=0;

    public ScrollManager(PhotoGalleryView photoGalleryView){
        this.mPhotoGalleryView=photoGalleryView;
    }


    /**
     * 初始化snapHelper
     * @param helper
     */
    public void initSnapHelper(int helper){
        switch (helper){
            case PhotoGalleryView.LINEAR_SNAP_HELPER:
                LinearSnapHelper mLinearSnapHelper=new LinearSnapHelper();
                mLinearSnapHelper.attachToRecyclerView(mPhotoGalleryView);
                break;
            case PhotoGalleryView.PAGER_SNAP_HELPER:
                PagerSnapHelper mPagerSnapHelper=new PagerSnapHelper();
                mPagerSnapHelper.attachToRecyclerView(mPhotoGalleryView);
                break;
            default:
                break;
        }
    }

    public void initScrollListener() {
        GalleryScrollerListener mGalleryScrollerListener=new GalleryScrollerListener();
        mPhotoGalleryView.addOnScrollListener(mGalleryScrollerListener);
    }

    /**
     * 更新消耗的距离
     */
    public void updateConsume() {
        mConsumeX += OsUtil.dpToPx(mPhotoGalleryView.getDecoration().mPageVisibleWidth + mPhotoGalleryView.getDecoration().mPageMargin * 2);
        mConsumeY += OsUtil.dpToPx(mPhotoGalleryView.getDecoration().mPageVisibleWidth + mPhotoGalleryView.getDecoration().mPageMargin * 2);
        Log.d(TAG, "ScrollManager updateConsume mConsumeX=" + mConsumeX);
    }


    class GalleryScrollerListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            Log.d(TAG, "ScrollManager newState=" + newState);
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mPhotoGalleryView.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                onHorizontalScroll(recyclerView, dx);
            } else {
                onVerticalScroll(recyclerView, dy);
            }
        }
    }


    //水平滚动
    private void onHorizontalScroll(RecyclerView recyclerView, int dx) {
        mConsumeX+=dx;
        // 让RecyclerView测绘完成后再调用，避免GalleryAdapterHelper.mItemWidth的值拿不到
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int shouldConsumeX=mPhotoGalleryView.getDecoration().mItemConsumeX;
                // 位置浮点值（即总消耗距离 / 每一页理论消耗距离 = 一个浮点型的位置值）
                float offset=(float) mConsumeX/(float) shouldConsumeX;
                // 获取当前页移动的百分值
                float percent=offset-((int)offset);
                mPosition=(int) offset;
                Log.i(TAG, "ScrollManager offset=" + offset + "; percent=" + percent + "; mConsumeX=" + mConsumeX + "; shouldConsumeX=" + shouldConsumeX + "; position=" + mPosition);
                //设置由大到小的动画
                mPhotoGalleryView.getAnimManager().setAnimation(mPhotoGalleryView,mPosition,percent);

            }
        });
    }



    /**
     * 垂直滑动
     *
     * @param recyclerView RecyclerView
     * @param dy           int
     */
    private void onVerticalScroll(final RecyclerView recyclerView, int dy) {
        mConsumeY += dy;

        // 让RecyclerView测绘完成后再调用，避免GalleryAdapterHelper.mItemHeight的值拿不到
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int shouldConsumeY = mPhotoGalleryView.getDecoration().mItemConsumeY;

                // 位置浮点值（即总消耗距离 / 每一页理论消耗距离 = 一个浮点型的位置值）
                float offset = (float) mConsumeY / (float) shouldConsumeY;
                // 获取当前页移动的百分值
                float percent = offset - ((int) offset);

                mPosition = (int) offset;

                // 设置动画变化
                mPhotoGalleryView.getAnimManager().setAnimation(recyclerView, mPosition, percent);
            }
        });
    }

    public int getPosition(){
        return mPosition;
    }


}


