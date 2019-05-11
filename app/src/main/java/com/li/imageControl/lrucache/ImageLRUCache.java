package com.li.imageControl.lrucache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import com.li.imageControl.BuildConfig;
import com.li.imageControl.ImageResizeUtils;
import com.li.imageControl.R;
import com.li.imageControl.lrucache.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 作者:lixue
 * 邮箱:lixue326@163.com
 * 时间:2019/5/11 10:47
 * 描述: 图片三级缓存管理
 * 缓存查找---> 复用池 ---> 磁盘查找 --->网络加载图片
 */


public class ImageLRUCache {
    private LruCache<String , Bitmap> memeoryCache;
    private DiskLruCache diskLruCache;
    private Set<WeakReference<Bitmap>>  reuseablePool ;
    private Context context;
    private ReferenceQueue  referenceQueue;
    Thread clearReferencQueue;
    boolean shutDown;
    BitmapFactory.Options options = new BitmapFactory.Options();

    /**
     * 获取referenceQueue队列中数据  然后进行手动 或者  动态的垃圾回收
     * @return
     */
    private ReferenceQueue<Bitmap> getReferenceQueue(){
       if (null == referenceQueue){
           referenceQueue = new ReferenceQueue<Bitmap>();
           clearReferencQueue = new Thread(new Runnable() {
               @Override
               public void run() {
                    while (!shutDown){
                        try {
                            Reference<Bitmap> reference = referenceQueue.remove();
                            Bitmap bitmap = reference.get();
                            if (null != bitmap && !bitmap.isRecycled()){
                                bitmap.recycle();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
               }
           });
           clearReferencQueue.start();
       }
        return referenceQueue;
    }





    private static ImageLRUCache instance;
    public static ImageLRUCache getInstance(){
        if (null == instance){
            synchronized (ImageLRUCache.class){
                if (null == instance){
                    instance = new ImageLRUCache();
                }
            }
        }
        return instance;
    }


    //缓存处理
    public void init(Context context, String dir){
        this.context = context.getApplicationContext();
        reuseablePool= Collections.synchronizedSet(new HashSet< WeakReference<Bitmap> >());

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memorySize = am.getMemoryClass();
        Log.e("memeorySize: ", String.valueOf(memorySize/8*1024*1024) );
        memeoryCache = new LruCache<String, Bitmap>(memorySize/16*1024*1024){
            /**
             * 设置大小
             * @param key
             * @param value
             * @return
             */
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //APi19之前必须是inSimpleSize =1 才能复用也就是同等大小才能复用
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                    value.getAllocationByteCount();
                }
                return value.getByteCount();
            }

            /**
             * url满员, 需要移除对象
             * 判断是否需要复用的对象, 如果是, 就放入复用池
             * 如果不是  就recycle
             * 3.0在native
             * 3.0-8.0之前是java
             * 8.0之后就是native层
             * @param evicted
             * @param key
             * @param oldValue
             * @param newValue
             */
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (oldValue.isMutable()){
                    reuseablePool.add(new WeakReference<Bitmap>(oldValue, referenceQueue));
                } else {
                    oldValue.recycle();
                }
            }
        };

        try {
            diskLruCache = DiskLruCache.open(new File(dir), BuildConfig.VERSION_CODE, 1, 10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //引用队列  不断轮询  调用recycle方法
        getReferenceQueue();
    }

    /**
     * 图片放入缓存中
     * @param key
     * @param bitmap
     */
    public void  putBitmapToMemeory(String key, Bitmap bitmap){
        memeoryCache.put(key, bitmap);
    }

    /**
     * 获取缓存中的bitmap
     * @param key
     * @return
     */
    public Bitmap getBitmapToMemeory(String key){
        return  memeoryCache.get(key);
    }

    /**
     * 清楚缓存
     */
    public void clearMemeoryCache(){
        memeoryCache.evictAll();
    }

    /**
     * 复用 复用池中的bitmap的大小
     * @param w
     * @param h
     * @param inSampleSize
     * @return
     */
    public Bitmap getReuseable(int w, int h, int inSampleSize){
        //小于3.0之后 不需要使用缓存池
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            return null;
        }
        Bitmap reuseable = null;
        Iterator<WeakReference<Bitmap>> iterator = reuseablePool.iterator();
        Log.e("resuableSize", reuseablePool.size()+"");
        while (iterator.hasNext()){
            Bitmap bitmap = iterator.next().get();
            if (null != bitmap){
                //可以进行复用
                if (checkInBitmap(bitmap, w, h, inSampleSize)){
                    Log.e("resuable", "可用");
                    reuseable = bitmap;
                    iterator.remove();
                    break;
                }  else {
                    iterator.remove();
                }
            }

        }
        return  reuseable;
    }

    /**
     * 检查是否可以使用
     * 5.0之前需要完全能够复用, 就是长款以及insmapleSize == 1复用池的内存块才可以被使用
     * @param bitmap
     * @param w
     * @param h
     * @param inSampleSize
     * @return
     */
    private boolean checkInBitmap(Bitmap bitmap , int w, int h, int inSampleSize){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return bitmap.getWidth() == w && bitmap.getHeight() == h && inSampleSize == 1;
        }
        if (inSampleSize >= 1){
            w = bitmap.getWidth()/inSampleSize;
            h = bitmap.getHeight()/inSampleSize;
        }
        int byteCount = w*h*getPixelsCount( bitmap.getConfig());
        return  byteCount < bitmap.getAllocationByteCount();
    }

    /**
     * 获取图片像素 位数
     * @return
     */
    private int getPixelsCount( Bitmap.Config config){
        if (config == Bitmap.Config.ARGB_8888){
            return 4;
        }
        return 2;
    }

    /**
     * 将图片放入硬盘中
     * @param key
     * @param bitmap
     */
    public void putBitmapToDisk(String key, Bitmap bitmap) {
        DiskLruCache.Snapshot snapshot = null;
        DiskLruCache .Editor editor;
        OutputStream outputStream = null;

        try {
            snapshot = diskLruCache.get(key);
            if (null == snapshot) {

                editor = diskLruCache.edit(key);
                if (null != editor){
                    outputStream = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    editor.commit();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot){
                snapshot.close();
            }
            if (null != outputStream){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取磁盘中的图片
     * @param key
     * @return
     */
    public Bitmap getBitmapFromDisk(String key, Bitmap reusableBitmap){
     DiskLruCache.Snapshot snapshot = null;
     Bitmap bitmap = null;
        try {
            snapshot = diskLruCache.get(key);
            if (null == snapshot){
                return null;
            }
            InputStream inputStream = snapshot.getInputStream(0);
            options.inMutable = true;
            options.inBitmap= reusableBitmap;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            if (null != bitmap){
                memeoryCache.put(key, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot){
                snapshot .close();
            }
        }

        return bitmap;
    }

    public Bitmap getBitmap(String key, int w, int h, int inSimpleSize){
        Bitmap bitmap = memeoryCache.get(key);
        if (null == bitmap) {
            Bitmap resueableBitmap = getReuseable(w, h, inSimpleSize);
            bitmap = getBitmapFromDisk(key, resueableBitmap);
            if (bitmap == null) {
                //从网络上加载数据
                bitmap = ImageResizeUtils.imageUtils(context, R.mipmap.wyz_p, 80, 80, false, resueableBitmap);
                putBitmapToMemeory(key, bitmap);
                putBitmapToDisk(key, bitmap);

            }
        }
        return bitmap;
    }


}
