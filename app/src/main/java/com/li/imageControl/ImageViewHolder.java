package com.li.imageControl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/11 10:22
 * 描述: iamgeView Hodler
 */


public class ImageViewHolder extends RecyclerView.ViewHolder {
    public  ImageView ivc;
    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
        ivc = itemView.findViewById(R.id.ivc);
    }
}
