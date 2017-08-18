package cn.cfanr.compress;

import android.graphics.Bitmap;
import android.os.Build;

/**
 * Created by Pipi on 2017/8/18.
 */

public class ImageCompress {

    static {
        System.loadLibrary("compress");
    }

    private ImageCompress(){

    }

    public static native boolean compressBitmap(Bitmap bitmap, String dstPath, int quality, boolean isOptimize);


    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount() / 1024;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount() / 1024;
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight() / 1024;                //earlier version
    }
}
