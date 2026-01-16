package com.drift.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import com.foreamlib.imageloader.ImageLoader;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

//import com.foreamlib.imageloader.DrawableFileCache;
import com.foreamlib.imageloader.GoproDrawableFileCache;
import com.foreamlib.imageloader.ImageLoader;
import com.drift.util.SPUtil;


public class ForeamApp extends Application {

    private final static String TAG = "ForeamApp";
    private static ForeamApp instance = null;
    private ImageLoader imageloader = null;

    private String currentCamIP = null;//used for when try to accesss files on cam.

    public String getCurrentCamIP() {
        return currentCamIP;
    }

    public void setCurrentCamIP(String currentCamIP) {
        this.currentCamIP = currentCamIP;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * Init Global Var
         */
        SPUtil.setswCodec(this, true);
        if (com.foreamlib.log.Log.DEBUG_IMAGE_LOADING) {
            imageloader = new ImageLoader(1);
        } else {
            imageloader = new ImageLoader(3);
        }
        GoproDrawableFileCache.initFileCache(this);
        instance = this;
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        CrashReport.initCrashReport(getApplicationContext(), "f678368361", false);
    }

    public static ForeamApp getInstance() {
        return instance;
    }

    public ImageLoader getImageLoader() {
        return imageloader;
    }

    public void stopInternetTask() {
        if (imageloader != null)
            imageloader.cancelImageLoading();
        GoproDrawableFileCache.killAllTask();
    }

    public static boolean isInChinesEnvir() {
        if (Locale.CHINESE.getLanguage().equals(instance.getResources().getConfiguration().locale.getLanguage()))
            return true;
        return false;
    }

    public static String getVersionCode() {
        PackageManager manager = ForeamApp.getInstance().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(ForeamApp.getInstance().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }
}
