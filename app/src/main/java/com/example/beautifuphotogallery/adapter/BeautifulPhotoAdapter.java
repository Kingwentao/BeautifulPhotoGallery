package com.example.beautifuphotogallery.adapter;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.beautifuphotogallery.R;

import java.util.List;
import java.util.Random;

/**
 * Created by 金文韬 on 2018/10/17.
 */

public class BeautifulPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BeautifulPhotoAdapter";
    private Context mContext;
    private List<Integer> mDatas;
    private OnItemPhotoChangedListener mOnItemPhotoChangedListener;

    public BeautifulPhotoAdapter(Context mContext,List<Integer> mDatas){
        this.mContext=mContext;
        this.mDatas=mDatas;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "--->onAttachedToRecyclerView");
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(R.layout.item_gallery,parent,false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "RecyclerAdapter onBindViewHolder" + "--> position = " + position);
        final ItemViewHolder photoHolder=(ItemViewHolder)holder;
        photoHolder.mPhotoView.setImageResource(mDatas.get(position));
        photoHolder.mChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int randomNum=new Random().nextInt(9);
                int[] res = {R.drawable.photo_nba1, R.drawable.photo_nba2, R.drawable.photo_nba3, R.drawable.photo_nba4,
                        R.drawable.photo_nba5, R.drawable.photo_nba6, R.drawable.photo_nba7, R.drawable.photo_nba8, R.drawable.photo_nba9};

                mDatas.set(photoHolder.getAdapterPosition(), res[randomNum]);
                notifyItemChanged(photoHolder.getAdapterPosition(),this.getClass().getName());
                if (mOnItemPhotoChangedListener != null) {
                    mOnItemPhotoChangedListener.onItemPhotoChanged();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mPhotoView;
        FloatingActionButton mChange;

        ItemViewHolder(View itemView) {
            super(itemView);
            mPhotoView = itemView.findViewById(R.id.iv_photo);
            mChange = itemView.findViewById(R.id.fab_change);
        }
    }

    public interface OnItemPhotoChangedListener {
        /**
         * 局部更新后需要替换背景图片
         */
        void onItemPhotoChanged();
    }

    public void setOnItemPhotoChangedListener(OnItemPhotoChangedListener mOnItemPhotoChangedListener) {
        this.mOnItemPhotoChangedListener = mOnItemPhotoChangedListener;
    }

    /**
     * 获取position位置对应的resId
     */
    public int getResId(int position) {
        Log.d(TAG, "getResId: position="+position);
        return mDatas == null ? 0 : mDatas.get(position);
    }
}
