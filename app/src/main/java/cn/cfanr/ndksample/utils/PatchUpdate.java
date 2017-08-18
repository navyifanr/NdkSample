package cn.cfanr.ndksample.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.util.Log;

import java.io.File;

import cn.cfanr.ndksample.AppController;

/**
 * Created by Pipi on 2017/8/15.
 */

public class PatchUpdate {

    /**
     * 提取 apk 路径
     * @return
     */
    public static String extractApkPath() {
        ApplicationInfo applicationInfo = AppController.getInstance().getApplicationInfo();
        String apkPath = applicationInfo.sourceDir;
        Log.d("patchUpdate", apkPath);
        return apkPath;
    }

    /**
     * 安装 apk
     * @param context
     * @param apkPath
     */
    public static void install(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
