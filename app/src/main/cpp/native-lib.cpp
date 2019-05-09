#include <jni.h>
#include <string>
#include <malloc.h>
#include <android/bitmap.h>
#include <jpeglib.h>

void write_JPEG_file(uint8_t *temp, int w, int h, const char *path);

extern "C" JNIEXPORT jstring JNICALL
Java_com_li_imageControl_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

void write_JPEG_file(uint8_t *data, int w, int h,int q, const char *path) {
    //1.创建JPGe压缩对象
    jpeg_compress_struct jcs;
    //错误回调
    jpeg_error_mgr error;
    jcs.err = jpeg_std_error(&error);
    //创建压缩对象
    jpeg_create_compress(&jcs);
    //2.指定存储文件
        FILE *f = fopen(path, "wb");
        jpeg_stdio_dest(&jcs,f);
    //3.设置压缩参数
    jcs.image_width = w;
    jcs.image_height = h;
    //bgr
    jcs.input_components = 3;
    jcs.in_color_space = JCS_RGB;
    jpeg_set_defaults(&jcs);
    //开启哈夫曼
    jcs.optimize_coding = true;
    jpeg_set_quality(&jcs, q,1);
    //4.开始压缩
    jpeg_start_compress(&jcs, 1);
    //5.循环写入每一行
    int row_stride = w*3;
    JSAMPROW  row[1];
    while (jcs.next_scanline < jcs.image_height){
        //获取一行数据
        uint8_t *pixels = data+jcs.next_scanline*row_stride;
        row[0]=pixels;
        jpeg_write_scanlines(&jcs, row, 1);
    }


    //6.压缩完成
    jpeg_finish_compress(&jcs);
    //7.释放对象
    fclose(f);
    jpeg_destroy_compress(&jcs);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_li_imageControl_MainActivity_nativeCompress(JNIEnv *env, jobject instance, jobject bitmap,
                                                     jint q, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);

    // TODO
    //从bitmap中获取arg像素
    AndroidBitmapInfo info;
    //获取里边的信息
    AndroidBitmap_getInfo(env, bitmap, &info);
    //获取像素信息
    uint8_t  *pixels; //指针指向的是地址, 相当于是一个数组使用
    AndroidBitmap_lockPixels(env, bitmap, (void**)&pixels);
    //去掉alph
    int w  = info.width;
    int h = info.height;
    int color;
    //开辟一块内存用来存储rgb信息
    uint8_t  *data = (uint8_t*)malloc(w * h * 3);
    uint8_t  *temp = data;
    uint8_t  r, g, b;
    //循环取出每一个像素
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++){
            color = *(int*)pixels;
            r = (color >> 16)&0xFF;
            g=(color>>8)&0xFF;
            b=color&0xFF;
            *data =b;
            *(data+1)=g;
            *(data+2)=r;
            data+=3;
            pixels+=4;
        }
    }
    write_JPEG_file(temp, w, h,q, path);


    env->ReleaseStringUTFChars(path_, path);
}
