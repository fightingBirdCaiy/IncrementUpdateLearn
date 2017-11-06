package com.caiy.learn.increment.update.bsdiffpatch;

/**
 * Created by admin on 2017/11/6.
 */

public class BsDiffPatchUtil {

    static {
        System.loadLibrary("bsdiff");
        System.loadLibrary("bspatch");
    }

    public static native int genDiff(String oldApkPath, String newApkPath, String patchPath);

    public static native int patch(String oldApkPath, String newApkPath, String patchPath);
}
