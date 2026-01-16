package com.foreamlib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class FileUtil {
    public static String dirName = "sportDV";
    private static long MB = 1024 * 1024;
    public final static String mediaDir = "/mnt/sdcard/DCIM/Camera/";
    private static String cacheRootPath = null;


    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


    public static boolean createSDCardDir(String fileName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File sdcardDir = Environment.getExternalStorageDirectory();

            String path = sdcardDir.getPath() + "/" + fileName;

            File path1 = new File(path);
            if (!path1.exists()) {

                path1.mkdirs();
                return true;
            }
        }
        return false;

    }

    /**
     * 复制单个文件
     *
     * @param srcFileName 待复制的文件名
     *                    目标文件名
     * @param overlay     如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);

        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                new File(destFileName).delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        int byteread = 0; // 读取的字节数
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean createSystemDir(String fileName) {
        String path = "/" + fileName;
        File path1 = new File(path);
        if (!path1.exists()) {

            path1.mkdirs();
            return true;
        }
        return false;

    }

    public static boolean isExist() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File sdcardDir = Environment.getExternalStorageDirectory();

            String path = sdcardDir.getPath() + "/" + "cdmediaTemp";

            File path1 = new File(path);
            if (path1.exists()) {
                return true;
            }
        }

        return false;
    }


    public static boolean isSDExist() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            return true;
        }

        return false;
    }

    public static boolean isExist(String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File sdcardDir = Environment.getExternalStorageDirectory();

            System.out.println("path--->" + path);
            File f = new File(path);
            if (f.exists()) {
                System.out.println("good");
                return true;
            }
        }

        return false;
    }

    public static String getDownloadPath() {
        String parentPath = "";
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 3 method get externalStorage path.
            parentPath = StorageOptions.getBiggestAvailableStorge();

            File file = new File(parentPath + "/Parashoot/");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getPath();
        } else {
            return null;
        }

    }

    public static String getMusicPath() {
        String parentPath = "";
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 3 method get externalStorage path.
            parentPath = StorageOptions.getBiggestAvailableStorge();

            File file = new File(parentPath + "/Parashoot/music/");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getPath();
        } else {
            return null;
        }

    }

    static String getExternalStorage() {
        String exts = Environment.getExternalStorageDirectory().getPath();
        try {
            FileReader fr = new FileReader(new File("/proc/mounts"));
            BufferedReader br = new BufferedReader(fr);
            String sdCard = null;
            String line;
            while ((line = br.readLine()) != null) {
                Log.d("", line);
                if (line.contains("secure") || line.contains("asec"))
                    continue;
                if (line.contains("fat")) {
                    String[] pars = line.split("\\s");
                    if (pars.length < 2)
                        continue;
                    if (pars[1].equals(exts))
                        continue;
                    // String[]pars2 = pars[1].split(":");
                    sdCard = pars[1];
                    break;
                }
            }
            fr.close();
            br.close();
            return sdCard;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String getRealSDCardPath() {
        String path = null;
        File file = new File("/system/etc/vold.fstab");
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fr != null) {
                br = new BufferedReader(fr);
                String s = br.readLine();
                while (s != null) {
                    if (s.startsWith("dev_mount")) {
                        String[] tokens = s.split("\\s");
                        path = tokens[2]; // mount_point
                        if (!Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
                            break;
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public static long getFileSize(String path) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File sdcardDir = Environment.getExternalStorageDirectory();

            File f = new File(path);
            return f.length();
        }

        return 0;
    }

    public static String getSDdir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File sdcardDir = Environment.getExternalStorageDirectory();
            String path = sdcardDir.getPath();

            return path;
        }
        return null;
    }

    public static String GetFileName(String URL, String type) {
        try {

            int end = URL.indexOf(type);
            if (end != -1) {
                String temp = URL.substring(0, end + 3);
                int start = temp.lastIndexOf("/");

                return (temp.substring(start + 1));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;

    }

    public static String GetFileName(String URL) {
        try {
            int start = URL.lastIndexOf("/");
            if (start != -1)
                return (URL.substring(start + 1));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return URL;

    }

    public static long freeSpaceOnSDcard() {

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    public static String getSDCardDir(String filePath) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File sdcardDir = Environment.getExternalStorageDirectory();
                String path = sdcardDir.getPath() + File.separator + filePath;
                // System.out.println("Cach Path-->" + path);
                if (!isExist(path)) {
                    File file = new File(path);
                    file.mkdirs();
                    // System.out.println("�����ļ���");
                }
                return path + "/";
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteFile(String path) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File f = new File(path);
                if (f.exists())
                    f.delete();
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return false;
    }

    public static boolean savePictureToSD(Bitmap mBitmap) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // File sdcardDir = Environment.getExternalStorageDirectory();
                File mFile = new File(mediaDir, String.valueOf(System.currentTimeMillis() + ".png"));
                mFile.createNewFile();
                OutputStream os = new FileOutputStream(mFile);
                os.write(BitmapUtil.bitmapToBytes(mBitmap, Bitmap.CompressFormat.PNG));
                os.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public static boolean savePictureBytesToSD(byte[] bytes) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                // File sdcardDir = Environment.getExternalStorageDirectory();
                File mFile = new File(mediaDir, String.valueOf(System.currentTimeMillis() + ".png"));
                mFile.createNewFile();
                OutputStream os = new FileOutputStream(mFile);
                os.write(bytes);
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readFile(String file_path) {
        String read_data = null;
        try {
            InputStream is = new FileInputStream(file_path);
            byte[] buffer = new byte[1];
            try {
                while (is.read(buffer) != -1)
                    read_data = read_data + (new String(buffer, "UTF-8"));
                return read_data;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyFile(String fileToPath, InputStream in) throws Exception {
        OutputStream out = null;
        try {
            out = new FileOutputStream(fileToPath);
            byte[] buffer = new byte[1024];
            while (true) {
                int ins = in.read(buffer);
                if (ins == -1) {
                    break;
                }

                out.write(buffer, 0, ins);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public static String getCacheDir(Context mContext) {
        if (cacheRootPath == null) {

            if (mContext.getExternalCacheDir() == null) {
                cacheRootPath = mContext.getCacheDir().getPath();
            } else {
                cacheRootPath = mContext.getExternalCacheDir().getPath();
            }
        }
        return cacheRootPath;
    }

    public static String getVideoProcessDir(Context mContext) {
        String path = getCacheDir(mContext) + "/" + MyDate.getCurTime();
        if (path != null) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getPath();
        } else {
            return null;
        }
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}