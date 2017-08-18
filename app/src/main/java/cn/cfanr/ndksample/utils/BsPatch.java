package cn.cfanr.ndksample.utils;

/**
 * Created by Pipi on 2017/8/15.
 */

public class BsPatch {

    public static native int bspatch(String oldApk, String newApk, String patch);
}
