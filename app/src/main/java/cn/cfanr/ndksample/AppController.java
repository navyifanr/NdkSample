package cn.cfanr.ndksample;

import android.app.Application;

/**
 * Created by Pipi on 2017/8/14.
 */

public class AppController extends Application {

    private static volatile AppController mInstance;

    public static synchronized AppController getInstance() {
        if(mInstance == null) {
            mInstance = new AppController();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}
