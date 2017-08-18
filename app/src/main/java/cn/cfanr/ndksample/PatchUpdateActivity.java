package cn.cfanr.ndksample;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import cn.cfanr.ndksample.utils.BsPatch;
import cn.cfanr.ndksample.utils.FileUtils;
import cn.cfanr.ndksample.utils.PatchUpdate;
import cn.cfanr.ndksample.utils.ToastUtils;

public class PatchUpdateActivity extends AppCompatActivity {
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch_update);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("当前版本为 v" + getVersionName());
//        tvVersion.setText("已增量更新为新版本，当前版本为 v" + getVersionName());
    }

    public void mergeApk(View view) {
        File file = new File(FileUtils.FILE_PATH + "update.patch");
        if(!file.exists()) {
            ToastUtils.showLong("本地没有patch文件，请先用adb命名push到/sdcard/NdkSample");
            return;
        }
        new ApkUpdateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void installApk(View view) {
        File newApkFile = new File(FileUtils.FILE_PATH + "new.apk");
//        //在安装前先校验生成的新 apk md5值是否和服务器的一致
//        if(!SignUtils.checkMd5(newApkFile, "")) {
//            ToastUtils.show("MD5值不一致，无法安装！");
//            return;
//        }
        if(!newApkFile.exists()) {
            ToastUtils.showLong("未生成新的Apk文件，请先生成");
            return;
        }
        PatchUpdate.install(this, FileUtils.FILE_PATH + "new.apk");
    }

    public String getVersionName() {
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }

    private class ApkUpdateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String oldApkPath = PatchUpdate.extractApkPath();
            File file = new File(FileUtils.FILE_PATH + "update.patch");
            if(!file.exists()) {
                return false;
            }
            BsPatch.bspatch(oldApkPath, FileUtils.FILE_PATH + "new.apk", FileUtils.FILE_PATH + "update.patch");
            File apkFile = new File(FileUtils.FILE_PATH + "new.apk");
            if(apkFile.exists()) {
//                //检验文件MD5值
//                return SignUtils.checkMd5(apkFile, "md5");
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                ToastUtils.show("patch 合并成功");
            } else {
                ToastUtils.show("patch 合并失败");
            }
        }
    }
}
