
package com.foreamlib.log;

import android.os.Environment;

//import com.foreamlib.cloud.model.CloudDefine;
import com.foreamlib.imageloader.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Log {
    private final static String LOG_DIR = "foream_log";
    public static boolean DEBUG_IMAGE_LOADING = false;
    public static boolean DEBUG_CLOUD_CMD = false;
    public static boolean DEBUG = DEBUG_IMAGE_LOADING | DEBUG_CLOUD_CMD;
    private final static String LOG_NAME = "foream_log_";
    private static FileOutputStream outputStream;
    public static int Serial = 0;

    public static void saveLog() {
        //保存为一个文件
//		if(outputStream!=null){
//			try {
//				outputStream.close();
//				Serial = 0;
//				outputStream = null;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
    }

    public static void initFile() {

        if (!DEBUG) return;
        if (outputStream == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
            String LogDir = Environment.getExternalStorageDirectory().getPath() + "/" + LOG_DIR;
            File dir = new File(LogDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filepath = LogDir + "/" + LOG_NAME + format.format(new Date(System.currentTimeMillis())) + ".txt";
            File file = new File(filepath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                outputStream = new FileOutputStream(filepath);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void d(String tag, String log) {
        if (!DEBUG_IMAGE_LOADING && tag.equals(ImageLoader.LOG_TAG)) return;
        if (!DEBUG) return;
        try {
            initFile();
            if (outputStream != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String value = tag + "\t" + format.format(new Date(System.currentTimeMillis())) + "\t" + log + "\r\n";
                outputStream.write(value.getBytes());
                outputStream.flush();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    public static void pingTestThread(final long serialnumber) {
//        new Thread() {
//            @Override
//            public void run() {
//                long time = pingTest("180.97.33.107");
//                d("Ping Baidu", serialnumber + "\tTime:" + time);
//            }
//        }.start();
//        new Thread() {
//            @Override
//            public void run() {
//                long time = pingTest(CloudDefine.getHost());
//                d("Ping Foream", serialnumber + "\tTime:" + time);
//            }
//        }.start();
//    }

    private static long pingTest(String URL) {
        Runtime run = Runtime.getRuntime();
        Process proc = null;
        try {
            String str = "ping -c 1 " + URL;//ping -c 1 -i 0.2 -W 1
            long curTime = System.currentTimeMillis();
            proc = run.exec(str);
            int result = proc.waitFor();
            if (result == 0) {
                return System.currentTimeMillis() - curTime;// Toast.makeText(ClientActivity.this, "ping连接成功",
                // Toast.LENGTH_SHORT).show();
            } else {
                return -1;// Toast.makeText(ClientActivity.this, "ping测试失败",
                // Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            if (proc != null)
                proc.destroy();
        }
        return -1;
    }
}
