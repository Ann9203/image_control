package com.li.imageControl;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.li.imageControl.lrucache.ImageLRUCache;

import java.util.List;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/11 10:21
 * 描述:
 */


public class ImageAdapter  extends RecyclerView.Adapter<ImageViewHolder>{

    private Context mContext ;
    public ImageAdapter(Context mContext){
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View hodlerView =  LayoutInflater.from(mContext).inflate(R.layout .item_view, viewGroup, false);
        ImageViewHolder viewHolder = new ImageViewHolder(hodlerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {


        imageViewHolder.ivc.setImageBitmap(ImageLRUCache.getInstance().getBitmap(String.valueOf(i), 60,60,1));
    }

    @Override
    public int getItemCount() {
        return 9999;
    }
}
