package com.li.imageControl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Bitmap inputBitmap;
    private ImageView iv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        File input = new File("/storage/emulated/0", "www.jpg");
        inputBitmap = BitmapFactory.decodeFile(input.getAbsolutePath());
        iv1 = findViewById(R.id.iv);
        ImageView iv2= findViewById(R.id.iv1);
        ImageView iv3 = findViewById(R.id.iv2);
        ImageView iv4 = findViewById(R.id.iv3);
      // iv1.setImageBitmap(inputBitmap);
    //    iv2.setImageResource(R.mipmap.www);

        Log.i("jett1","图片"+inputBitmap.getWidth()+"X"+inputBitmap.getHeight()+" 内存大小:"+inputBitmap.getByteCount());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.www);

        Log.i("jett","图片"+bitmap.getWidth()+"X"+bitmap.getHeight()+" 内存大小:"+bitmap.getByteCount());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native void nativeCompress(Bitmap bitmap, int q, String path);

    public void imagUtils(){
        nativeCompress(inputBitmap, 30, "/storage/emulated/0/www1.jpg");
        File input = new File("/storage/emulated/0", "www1.jpg");
       Bitmap  inputBitmap = BitmapFactory.decodeFile(input.getAbsolutePath());
        iv1.setImageBitmap(inputBitmap);
        Log.i("jett1","图片"+inputBitmap.getWidth()+"X"+inputBitmap.getHeight()+" 内存大小:"+inputBitmap.getByteCount());

    }

    public void onClick(View view) {
        imagUtils();

        Bitmap bitmap = ImageUtils.imageUtils(getApplicationContext(), R.mipmap.www, 373,458,false);
        Log.i("jett","图片"+bitmap.getWidth()+"X"+bitmap.getHeight()+" 内存大小:"+bitmap.getByteCount());
    //   iv1.setImageBitmap(bitmap);

    }
}
