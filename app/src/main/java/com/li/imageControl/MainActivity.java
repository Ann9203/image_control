package com.li.imageControl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.li.imageControl.lrucache.ImageLRUCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Bitmap inputBitmap;
    private ImageView iv1;
    private RecyclerView rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageLRUCache.getInstance().init(this, Environment.getExternalStorageDirectory()+"/li");
        rc = findViewById(R.id.rc);
      //  List<Bitmap> bitmapList =new ArrayList<>();
        //图片是网络图片
//        BitmapFactory.Options  options = new BitmapFactory.Options();
//        options.inMutable =  true;
 //       Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.www, options );

//        for (int i = 0; i < 100; i++){
//        //    bitmapList.add(ImageResizeUtils.imageUtils(getBaseContext(), R.mipmap.www, 160,160, true, bitmap));
//           options.inBitmap = bitmap;
//            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.www);
//
//        }
//        rc.setLayoutManager(new LinearLayoutManager(getBaseContext()));
//        ImageAdapter imageAdapter = new ImageAdapter(MainActivity.this.getBaseContext());
//        rc.setAdapter(imageAdapter);
        ListView lv = findViewById(R.id.lv);
        MyAdapter myAdapter = new MyAdapter(this);
        lv.setAdapter(myAdapter);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void nativeCompress(Bitmap bitmap, int q, String path);


























    // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
//        File input = new File("/storage/emulated/0", "www.jpg");
//        inputBitmap = BitmapFactory.decodeFile(input.getAbsolutePath());
//        iv1 = findViewById(R.id.iv);
//        ImageView iv2= findViewById(R.id.iv1);
//        ImageView iv3 = findViewById(R.id.iv2);
//        ImageView iv4 = findViewById(R.id.iv3);
//      // iv1.setImageBitmap(inputBitmap);
//    //    iv2.setImageResource(R.mipmap.www);
//
//        Log.i("jett1","图片"+inputBitmap.getWidth()+"X"+inputBitmap.getHeight()+" 内存大小:"+inputBitmap.getByteCount());
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.www);
//
//        Log.i("jett","图片"+bitmap.getWidth()+"X"+bitmap.getHeight()+" 内存大小:"+bitmap.getByteCount());

//    public void imagUtils(){
//        nativeCompress(inputBitmap, 30, "/storage/emulated/0/www1.jpg");
//        File input = new File("/storage/emulated/0", "www1.jpg");
//       Bitmap  inputBitmap = BitmapFactory.decodeFile(input.getAbsolutePath());
//        iv1.setImageBitmap(inputBitmap);
//        Log.i("jett1","图片"+inputBitmap.getWidth()+"X"+inputBitmap.getHeight()+" 内存大小:"+inputBitmap.getByteCount());
//
//    }
//
//    public void onClick(View view) {
//        imagUtils();
//
//        Bitmap bitmap = ImageResizeUtils.imageUtils(getApplicationContext(), R.mipmap.www, 373,458,false);
//        Log.i("jett","图片"+bitmap.getWidth()+"X"+bitmap.getHeight()+" 内存大小:"+bitmap.getByteCount());
//    //   iv1.setImageBitmap(bitmap);
//
//    }
}
