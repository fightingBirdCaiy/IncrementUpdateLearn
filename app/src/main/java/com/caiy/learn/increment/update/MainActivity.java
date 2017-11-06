package com.caiy.learn.increment.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.caiy.learn.increment.update.bsdiffpatch.BsDiffPatchUtil;
import com.caiy.learn.increment.update.util.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ProgressBar loadding;

    private String srcDir = Environment.getExternalStorageDirectory().toString() + "/360appstore_4811.apk";
    private String destDir1 = Environment.getExternalStorageDirectory().toString() + "/360appstore_4918.apk";
    private String destDir2 = Environment.getExternalStorageDirectory().toString() + "/360appstore_4918_my_new.apk";
    private String patchDir = Environment.getExternalStorageDirectory().toString() + "/360appstore_4918.patch";

    // 成功
    private static final int WHAT_SUCCESS = 1;
    // 失败
    private static final int WHAT_FAIL_PATCH = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "copy successed", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "copy failured", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "bsdiff successed", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "bsdiff failured", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), "patch successed", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(), "patch failures", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadding = (ProgressBar) findViewById(R.id.loadding);
    }

    public void copy(View view) {
        loadding.setVisibility(View.VISIBLE);
        new CopyTask().execute(srcDir, "360appstore_4811.apk", destDir1, "360appstore_4918.apk");
    }

    public void installOld(View view) {
        install(srcDir);
    }


    public void bsdiff(View view) {
        loadding.setVisibility(View.VISIBLE);
        new DiffTask().execute();
    }

    public void bspatch(View view) {
        loadding.setVisibility(View.VISIBLE);
        new PatchTask().execute();
    }

    public void installNew(View view) {
        install(destDir2);
    }

    private class CopyTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            for (int i = 0; i < params.length; i += 2) {
                try {
                    File file = new File(params[i]);
                    if (!file.exists())
                        FileUtils.createFile(file);

                    InputStream is;
                    OutputStream os = new FileOutputStream(params[i]);
                    is = getAssets().open(params[i + 1]);
                    byte[] buffer = new byte[1024];
                    int length = is.read(buffer);
                    while (length > 0) {
                        os.write(buffer, 0, length);
                        length = is.read(buffer);
                    }
                    os.flush();
                    is.close();
                    os.close();
                } catch (Exception e) {
                    handler.obtainMessage(1).sendToTarget();
                    return null;
                }
            }
            handler.obtainMessage(0).sendToTarget();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            loadding.setVisibility(View.GONE);
        }
    }

    /**
     * 生成差分包
     *
     */
    private class DiffTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            try {
                int result = BsDiffPatchUtil.genDiff(srcDir, destDir1, patchDir);
                if (result == 0) {
                    handler.obtainMessage(2).sendToTarget();
                    return WHAT_SUCCESS;
                } else {
                    handler.obtainMessage(3).sendToTarget();
                    return WHAT_FAIL_PATCH;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return WHAT_FAIL_PATCH;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            loadding.setVisibility(View.GONE);
        }
    }

    /**
     * 差分包合成APK
     *
     */
    private class PatchTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            try {

                int result = BsDiffPatchUtil.patch(srcDir, destDir2, patchDir);
                if (result == 0) {
                    handler.obtainMessage(4).sendToTarget();
                    return WHAT_SUCCESS;
                } else {
                    handler.obtainMessage(5).sendToTarget();
                    return WHAT_FAIL_PATCH;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return WHAT_FAIL_PATCH;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            loadding.setVisibility(View.GONE);
        }
    }

    private void install(String dir) {
        String command = "chmod 777 " + dir;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command); // 可执行权限
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + dir), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
