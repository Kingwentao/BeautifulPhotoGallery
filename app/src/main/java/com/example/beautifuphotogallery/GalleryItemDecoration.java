package com.example.beautifuphotogallery;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import util.OsUtil;

/**
 * Created by 金文韬 on 2018/10/16.
 */

public class GalleryItemDecoration extends RecyclerView.ItemDecoration {

    //每一页的默认页边距
    int mPageMargin=0;
    //滑动到屏幕中间左右两边的可见宽度
    int mPageVisibleWidth=50;
    //滑动一页实际消耗的宽度
    public int mItemConsumeX=0;
    public int mItemConsumeY=0;
    private OnItemSizeMeasuredListener mOnItemSizeMeasuredListener;
    private PhotoGalleryView.OnItemClickListener onItemClickListener;
    private static final String TAG = "GalleryItemDecoration";

    public GalleryItemDecoration(){}

    /**
     * 动态修改页面的宽度
     * @param outRect   Rect to receive the output.
     * @param itemView  The child view to decorate
     * @param parent    RecyclerView this ItemDecoration is decorating
     * @param state     The current state of RecyclerView.
     */
    @Override
    public void getItemOffsets(Rect outRect, final View itemView, final RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, itemView, parent, state);

        final int position=parent.getChildAdapterPosition(itemView);
        final int itemCount=state.getItemCount();

        parent.post(new Runnable() {
            @Override
            public void run() {
                if(((PhotoGalleryView)parent).getOrientation()== LinearLayoutManager.HORIZONTAL){
                    onSetHorizontalParams(parent, itemView, position, itemCount);
                }else{
                    onSetVerticalParams(parent, itemView, position, itemCount);
                }

            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });



    }


    /**
     * 设置水平滚动的参数
     *
     * @param parent    ViewGroup
     * @param itemView  View
     * @param position  int
     * @param itemCount int
     */
    private void onSetHorizontalParams(ViewGroup parent, View itemView, int position, int itemCount) {
        int itemNewWidth = parent.getWidth() - OsUtil.dpToPx(4 * mPageMargin + 2 * mPageVisibleWidth);
        int itemNewHeight = parent.getHeight();

        mItemConsumeX = itemNewWidth + OsUtil.dpToPx(2 * mPageMargin);

        if (mOnItemSizeMeasuredListener != null) {
            mOnItemSizeMeasuredListener.onItemSizeMeasured(mItemConsumeX);
        }

        // 适配第0页和最后一页没有左页面和右页面，让他们保持左边距和右边距和其他项一样
        int leftMargin = position == 0 ? OsUtil.dpToPx(mPageVisibleWidth + 2 * mPageMargin) : OsUtil.dpToPx(mPageMargin);
        int rightMargin = position == itemCount - 1 ? OsUtil.dpToPx(mPageVisibleWidth + 2 * mPageMargin) : OsUtil.dpToPx(mPageMargin);
        Log.d(TAG, "getItemOffsets---> postion="+position+"itemCount="+itemCount+"leftMargin->"+leftMargin);
        setLayoutParams(itemView, leftMargin, 0, rightMargin, 0, itemNewWidth, itemNewHeight);
    }


    private void onSetVerticalParams(ViewGroup parent, View itemView, int position, int itemCount) {
        int itemNewWidth = parent.getWidth();
        int itemNewHeight = parent.getHeight() - OsUtil.dpToPx(4 * mPageMargin + 2 * mPageVisibleWidth);

        mItemConsumeY = itemNewHeight + OsUtil.dpToPx(2 * mPageMargin);

        if (mOnItemSizeMeasuredListener != null) {
            mOnItemSizeMeasuredListener.onItemSizeMeasured(mItemConsumeY);
        }

        // 适配第0页和最后一页没有左页面和右页面，让他们保持左边距和右边距和其他项一样
        int topMargin = position == 0 ? OsUtil.dpToPx(mPageVisibleWidth + 2 * mPageMargin) : OsUtil.dpToPx(mPageMargin);
        int bottomMargin = position == itemCount - 1 ? OsUtil.dpToPx(mPageVisibleWidth + 2 * mPageMargin) : OsUtil.dpToPx(mPageMargin);

        setLayoutParams(itemView, 0, topMargin, 0, bottomMargin, itemNewWidth, itemNewHeight);
    }


    private void setLayoutParams(View itemView, int left, int top, int right, int bottom, int itemWidth, int itemHeight) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        boolean mMarginChange = false;
        boolean mWidthChange = false;
        boolean mHeightChange = false;

        if(lp.leftMargin!=left||lp.topMargin!=top||lp.rightMargin!=right||lp.bottomMargin!=bottom){
            lp.setMargins(left,top,right,bottom);
            mMarginChange=true;
        }
        if (lp.width!=itemWidth){
            lp.width=itemWidth;
            mWidthChange=true;
        }
        if(lp.height!=itemHeight){
            lp.height=itemHeight;
            mHeightChange=true;
        }

        if(mOnItemSizeMeasuredListener!=null){
            mOnItemSizeMeasuredListener.onItemSizeMeasured(mItemConsumeX);
        }

        // 因为方法会不断调用，只有在真正变化了之后才调用
        if (mWidthChange || mMarginChange || mHeightChange) {
            itemView.setLayoutParams(lp);
        }
    }


    public void setOnItemSizeMeasuredListener(OnItemSizeMeasuredListener itemSizeMeasuredListener) {
        this.mOnItemSizeMeasuredListener = itemSizeMeasuredListener;
    }

    public void setOnItemClickListener(PhotoGalleryView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    interface OnItemSizeMeasuredListener{
        //测量item的尺寸大小
        void onItemSizeMeasured(int size);
    }


}
