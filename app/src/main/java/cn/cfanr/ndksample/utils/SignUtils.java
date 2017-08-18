package cn.cfanr.ndksample.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by Pipi on 2017/8/16.
 */

public class SignUtils {
    /**
     * 校验文件的 md5 值是否和指定值相同
     * @param file1
     * @param md5
     * @return
     */
    public static boolean checkMd5(File file1,  String md5) {
        if(TextUtils.isEmpty(md5)) {
            throw new RuntimeException("md5 cannot be empty");
        }
        if(file1 != null && file1.exists()) {
            String file1Md5 = getMd5ByFile(file1);
            return file1Md5.equals(md5);
        }
        return false;
    }

    /**
     * 获取文件的MD5值
     * @param file
     * @return
     */
    private static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            value = bytes2Hex(digester.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private static String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }
        return new String(res);
    }
}
