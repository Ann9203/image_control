package com.li.imageControl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/10 0:30
 * 描述: 缩放压缩
*/

public class ImageResizeUtils {

    /**
     * 缩放压缩
     * @param context
     * @param resID
     * @param maxW
     * @param maxH
     * @return
     */
    public static Bitmap imageUtils(Context context,int  resID, int maxW, int maxH, boolean isAlph, Bitmap reusableBitmap){
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //获取图片信息
        BitmapFactory.decodeResource(resources, resID, options);
        //获取图片宽高
        int w = options.outWidth;
        int h = options.outHeight;

        options.inSampleSize = getSampleSize(w, h, maxW, maxH); //设置一个压缩比例
        if (!isAlph){
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        options.inJustDecodeBounds = false; //关闭
        options.inMutable = true;
        options.inBitmap = reusableBitmap;
        return BitmapFactory.decodeResource(resources, resID, options); //得到压缩的图片

    }

    /**
     * 获取一个压缩比例
     * @param w
     * @param h
     * @param maxW
     * @param maxH
     * @return
     */
    private  static int getSampleSize(int w, int h, int maxW, int maxH){
        int sampleSize =1;
        if (w > maxW && h > maxH){
            sampleSize = 2;
        }
        while (w/sampleSize > maxW && h/sampleSize > maxH){
            sampleSize *=2;
        }
        return sampleSize/2;
    }
}
