package cn.cfanr.ndksample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.cfanr.compress.ImageCompress;
import cn.cfanr.ndksample.utils.FileUtils;
import cn.cfanr.ndksample.utils.ToastUtils;

public class LibJpegActivity extends AppCompatActivity {
    private TextView tvPreCompress, tvAfterCompress;
    private ImageView ivPreCompress, ivAfterCompress;

    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_jpeg);

        tvPreCompress = (TextView) findViewById(R.id.tv_pre_compress);
        tvAfterCompress = (TextView) findViewById(R.id.tv_after_compress);
        ivPreCompress = (ImageView) findViewById(R.id.iv_pre_compress);
        ivAfterCompress = (ImageView) findViewById(R.id.iv_after_compress);

        InputStream in = null;
        try {
            in = getResources().getAssets().open("image.jpg");
            bitmap = BitmapFactory.decodeStream(in);
            ivPreCompress.setImageBitmap(bitmap);
            tvPreCompress.setText("压缩前，size=" + ImageCompress.getBitmapSize(bitmap) + "kb");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void compressImage(View view) {
        if(bitmap == null) {
            ToastUtils.show("文件不存在");
            return;
        }
        final String filename = "compress.jpg";
        final File jpegFile = new File(FileUtils.FILE_PATH);
        if (!jpegFile.exists()) {
            jpegFile.mkdirs();
        }
        final int quality = 50;
        new AsyncTask<Bitmap, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Bitmap... bitmap) {
                return ImageCompress.compressBitmap(bitmap[0], jpegFile.getPath() + "/" + filename, quality, true);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean) {
                    Bitmap bp = BitmapFactory.decodeFile(FileUtils.FILE_PATH + filename);
                    ivAfterCompress.setImageBitmap(bp);
                    tvAfterCompress.setText("压缩后，size=" + new File(FileUtils.FILE_PATH + filename).length() / 1024 + "kb，压缩率：" + quality);
                    tvAfterCompress.setTextColor(Color.BLACK);
                    tvAfterCompress.setEnabled(false);
                } else {
                    ToastUtils.show("压缩失败");
                }
            }
        }.execute(bitmap);
    }
}
