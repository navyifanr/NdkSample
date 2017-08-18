package cn.cfanr.ndksample.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Pipi on 2017/8/13.
 */

public class FileUtils {
    private static final String FILE_PATH_PREFIX = Environment.getExternalStorageDirectory() + File.separator;
    private static final String FOLDER_NAME = "NdkSample" + File.separator;
    public static final String FILE_PATH = FILE_PATH_PREFIX + FOLDER_NAME;

    /**
     * 文件加密
     *
     * @return
     */
    public static boolean fileEncrypt() {
        String normalFilePath = FILE_PATH + "cats.jpg";
        String encryptFilePath = FILE_PATH + "cats_encrypt.jpg";
        try {
            return fileEncrypt(normalFilePath, encryptFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件解密
     *
     * @return
     */
    public static boolean fileDecode() {
        String encryptFilePath = FILE_PATH + "cats_encrypt.jpg";
        String decodeFilePath = FILE_PATH + "cats_decode.jpg";
        try {
            return fileDecode(encryptFilePath, decodeFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件分割
     *
     * @return
     */
    public static boolean fileSplit() {
        String splitFilePath = FILE_PATH + "image.jpg";
        String suffix = ".b";
        try {
            return fileSplit(splitFilePath, suffix, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件合并
     *
     * @return
     */
    public static boolean fileMerge() {
        String splitFilePath = FILE_PATH + "image.jpg";
        String splitSuffix = ".b";
        String mergeSuffix = ".jpeg";
        try {
            return fileMerge(splitFilePath, splitSuffix, mergeSuffix, 4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件加密
     *
     * @param normalFilePath  要加密的文件路径
     * @param encryptFilePath 加密的文件路径
     */
    private static native boolean fileEncrypt(String normalFilePath, String encryptFilePath);

    /**
     * 文件解密
     *
     * @param encryptFilePath 加密文件路径
     * @param decodeFilePath  解密文件路径
     */
    private static native boolean fileDecode(String encryptFilePath, String decodeFilePath);

    /**
     * 文件分割
     *
     * @param splitFilePath 要分割文件的路径
     * @param suffix        分割文件的扩展名
     * @param fileNum       分割文件的数量
     * @return
     */
    private static native boolean fileSplit(String splitFilePath, String suffix, int fileNum);

    /**
     * 文件合并
     *
     * @param splitFilePath 分割文件的路径
     * @param splitSuffix   分割文件的扩展名
     * @param mergeSuffix   合并文件的扩展名
     * @param fileNum       分割文件的数量
     * @return
     */
    private static native boolean fileMerge(String splitFilePath, String splitSuffix, String mergeSuffix, int fileNum);


    /**
     * 拷贝assets单个文件到 SD 卡
     *
     * @param context
     * @param fileNames 具体文件路径
     */
    public static boolean copyAssetsFileToSDCard(Context context, String... fileNames) {
        try {
            InputStream is = null;
            FileOutputStream fos = null;
            for(String fileName : fileNames) {
                File file = new File(FILE_PATH);  //只写文件路径（不要文件名）
                if (!file.exists()) {
                    file.mkdirs();  //注意和 mkdir()方法的区别，使用mkdir()，如果想在父文件夹新建文件夹，则父文件夹必须存在
                }
                Log.e("path", file.getPath());
                is = context.getAssets().open(fileName);
                fos = new FileOutputStream(file + "/" +fileName);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) { //循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context
     * @param oldPath 原文件路径  如：/aa
     * @param newPath 复制后路径  如：xx:/bb/cc
     */
    public static boolean copyAssetsFilesToSDCard(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {  //如果是目录
                File file = new File(newPath);
                file.mkdirs();  //如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyAssetsFilesToSDCard(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 拷贝整个 asset 目录的文件（包含隐藏的文件/文件夹）
     * @param context
     * @return
     */
    @Deprecated
    public static boolean copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
            return false;
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(FILE_PATH);
                if(!outFile.exists()) {
                    outFile.mkdirs();
                }
                out = new FileOutputStream(outFile + "/" +filename);  //文件夹会有异常
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
                return false;
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return true;
    }
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

}
