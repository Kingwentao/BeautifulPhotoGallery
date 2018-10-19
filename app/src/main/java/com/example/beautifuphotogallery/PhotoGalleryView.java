package com.example.beautifuphotogallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 金文韬 on 2018/10/16.
 */

public class PhotoGalleryView extends RecyclerView implements GalleryItemDecoration.OnItemSizeMeasuredListener{

    private static final String TAG = "PhotoGalleryView";
    //滑动方式:一次可以滑动多个item
    public static final int LINEAR_SNAP_HELPER = 0;
    //滑动方式:一次只滑动一个item
    public static final int PAGER_SNAP_HELPER = 1;
    //默认滑行速度
    private int mFlingSpeed=5000;
    private GalleryItemDecoration mDecoration;

    private ScrollManager mScrollManager;
    //图片的默认值
    private int mInitPos=-1;

    private AnimManager mAnimManager;
    public PhotoGalleryView(Context context) {
       this(context,null);
    }

    public PhotoGalleryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PhotoGalleryView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PhotoGalleryView);
        int helper = ta.getInteger(R.styleable.PhotoGalleryView_helper,LINEAR_SNAP_HELPER);
        ta.recycle();
        Log.d(TAG, "PhotoGalleryView 的构造方法: ");
        mAnimManager=new AnimManager();
        //添加上装饰
        attachDecoration();
        //helper 滑行的方式
        attachToRecyclerHelper(helper);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /**
     * 获取布局方向
     * @return
     */
    public int getOrientation() {
        if (getLayoutManager() instanceof LinearLayoutManager) {
            if (getLayoutManager() instanceof GridLayoutManager) {
                throw new RuntimeException("请设置LayoutManager为LinearLayoutManager");
            } else {
                return ((LinearLayoutManager) getLayoutManager()).getOrientation();
            }
        } else {
            throw new RuntimeException("请设置LayoutManager为LinearLayoutManager");
        }
    }


    //重新定义滑行速度,取两者较小的那个
    @Override
    public boolean fling(int velocityX, int velocityY) {
        if(velocityX>0) velocityX=Math.min(velocityX,mFlingSpeed);
            else velocityX=Math.max(velocityX,-mFlingSpeed);
        if(velocityY>0) velocityY=Math.min(velocityY,mFlingSpeed);
            else velocityY=Math.max(velocityY,-mFlingSpeed);
        return super.fling(velocityX, velocityY);
    }

    public PhotoGalleryView initFlingSpeed(int mFlingSpeed){
            this.mFlingSpeed=mFlingSpeed;
            return  this;
    }

    //item大小改变
    @Override
    public void onItemSizeMeasured(int size) {

    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);  //点击事件
    }

    public GalleryItemDecoration getDecoration() {
        return mDecoration;
    }

    /**
     * 添加装饰
     */
    public void attachDecoration(){
        Log.d(TAG, "attachDecoration: -->");
        mDecoration=new GalleryItemDecoration();
        mDecoration.setOnItemSizeMeasuredListener(this);
        addItemDecoration(mDecoration);
    }

    public void attachToRecyclerHelper(int helper){
        Log.d(TAG, "attachToRecyclerHelper: -->");
        mScrollManager=new ScrollManager(this);
        mScrollManager.initScrollListener();
        mScrollManager.initSnapHelper(helper);
    }

    /**
     * 装载
     *
     * @return GalleryRecyclerView
     */
    public PhotoGalleryView setUp() {
        if (getAdapter().getItemCount() <= 0) {
            return this;
        }
        smoothScrollToPosition(0);
        mScrollManager.updateConsume();
        //autoPlayGallery();
        return this;
    }


    /**
     * 设置初始位置
     */
    public PhotoGalleryView initPosition(int i) {
        if (i >= getAdapter().getItemCount()) {
            i = getAdapter().getItemCount() - 1;
        } else if (i < 0) {
            i = 0;
        }
        mInitPos = i;
        return this;
    }


    /**
     * 设置页面参数，单位dp
     */
    public PhotoGalleryView initPageParams(int pageMargin, int pageVisibleWidth) {
        mDecoration.mPageMargin = pageMargin;
        mDecoration.mPageVisibleWidth=pageVisibleWidth;
        return this;
    }


    public GalleryItemDecoration getmDecoration() {
        return mDecoration;
    }

    /**
     * 返回动画管理者
     */
    public AnimManager getAnimManager(){
        if(mAnimManager==null) mAnimManager=new AnimManager();
        return  mAnimManager;
    }



    /**
     * 设置动画因子
     */
    public PhotoGalleryView setAnimFactor(float factor) {
        mAnimManager.setAnimFactor(factor);
        return this;
    }

    /**
     * 设置动画类型
     */
    public PhotoGalleryView setAnimType(int type) {
        mAnimManager.setAnimType(type);
        return this;
    }


    public int getScrolledPosition() {
        if (mScrollManager == null) {
            return 0;
        } else {
            return mScrollManager.getPosition();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Log.w(TAG, "PhotoGalleryView onSaveInstanceState()");
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        // 如果是横竖屏切换（Fragment销毁），不应该走smoothScrollToPosition(0)，因为这个方法会导致ScrollManager的onHorizontalScroll不断执行，而ScrollManager.mConsumeX已经重置，会导致这个值紊乱
        // 而如果走scrollToPosition(0)方法，则不会导致ScrollManager的onHorizontalScroll执行，所以ScrollManager.mConsumeX这个值不会错误
        scrollToPosition(0);
        // 但是因为不走ScrollManager的onHorizontalScroll，所以不会执行切换动画，所以就调用smoothScrollBy(int dx, int dy)，让item轻微滑动，触发动画
        smoothScrollBy(10, 0);
        smoothScrollBy(0, 0);

       // autoPlayGallery();
    }

    /**
     * 设置点击事件
     *
     * @param mListener OnItemClickListener
     */
    public PhotoGalleryView setOnItemClickListener(OnItemClickListener mListener) {
        if (mDecoration != null) {
            mDecoration.setOnItemClickListener(mListener);
        }
        return this;
    }

}
