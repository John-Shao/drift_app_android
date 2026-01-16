package com.drift.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

//import com.drift.camcontroldemo.R;
//import com.foream.activity.BaseActivity;
//import com.foream.activity.WelcomeActivity;
//import com.drift.app.ForeamApp;
//import com.foream.define.Actions;
//import com.drift.foreamlib.cloud.ctrl.CloudController.OnThirdPartyCerLoginResListner;
//import com.drift.foreamlib.cloud.ctrl.CloudController.OnUserLoginListener;
//import com.drift.foreamlib.cloud.ctrl.CloudController.OnUserOffLineListener;
//import com.drift.foreamlib.cloud.model.ErrorCode;
//import com.drift.foreamlib.cloud.model.UserInfo;
//import com.drift.foreamlib.sqlite.NotifyDBManager;
//import com.drift.foreamlib.sqlite.NotifyData;
//import com.drift.foreamlib.util.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityUtil {
    private static Activity cur_activity = null;
    private static Activity pre_activity = null;
    private static final String TAG = "ActivityUtil";
//    private static QuickEditDialog filterMenu;

//    public static boolean isAppBack(final Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static void restartApp(Context context) {
//        context.sendBroadcast(new Intent(Actions.ACTION_FINISH_ALL_ACTIVITY));
//        Intent intent = new Intent(context, WelcomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//
//    }
//
//    public static void startWifiDirectActivity(Context context) {
//        //  AppManager.getAppManager().finishAllActivity();
//        ForeamApp.getInstance().forceMobileData(context);//进入直连关闭4g
//
//        Intent intent;
//        String ssid = NetworkUtil2.getCurrentWifiSSID(context);
////        if (ssid.contains("-")) {
////            //intent = new Intent(context, WifiDirectHomeAllNewActivity.class);
////            //intent = new Intent(context, CamFirmwareUpgradeActivity.class);
//////            intent = new Intent(context, WifiDirectHomeAllNewActivity.class);
////            // WifiDirectHomeAllNewActivity
////        }
//////        else if (ssid.contains("Compass")) {
//////            intent = new Intent(context, WifiDirectHomeOldFirmwareActivity.class);
//////            //intent = new Intent(this, WifiDirectHomeAllNewActivity.class);
//////        }
////        else if (ssid.contains("unknow")) {//ensure to make it enter new when can't fetch ssid.
////            intent = new Intent(context, WifiDirectHomeAllNewActivity.class);
////        } else if (ssid.contains("Stealth 2")) {
////
////            intent = new Intent(context, WifiDirectHomeActivity.class);
////
////        } else {
////            intent = new Intent(context, WifiDirectHomeOldFirmwareActivity.class);
////        }
////        context.startActivity(intent);
//    }
//
//    public static void regOfflineListener(Activity act) {
//        cur_activity = act;
//        ForeamApp.getInstance().getCloudController().setOnUserOffLister(onUserOffLineListener);
//    }
//
//    public static void unregOfflineListener() {
//        ForeamApp.getInstance().getCloudController().setOnUserOffLister(null);
//    }
//
//    public static void sendAnonymousNotificaiotn() {
//        NotifyData notifydata = new NotifyData();
//        notifydata.setContent(ForeamApp.getInstance().getString(R.string.bar_notification_need_to_login));
//        notifydata.setIs_deal(0);
//        notifydata.setTime_last(NotifyData.TIME_LAST_ALWAYS);
//        // notifydata.setExtra(id+"");
//        notifydata.setNotify_type(NotifyData.NOTIFY_TYPE_ANONYMOUS);
//        NotifyDBManager mNotifyDBManager = ForeamApp.getInstance().getNotifyDBManager();
//        mNotifyDBManager.updateNotifyData(notifydata);
//        ForeamApp.getInstance().sendBroadcast(new Intent(Actions.ACTION_NOTIFIICATION));
//    }
//
//    public static void loginActivity(Context ctx) {
////        Intent i = new Intent(ctx, LoginActivity.class);
////        ctx.startActivity(i);
//    }
//
//    public static void loginInBackground() {
//        // 未登录用户。不登录
//        if (ForeamApp.getInstance().getCloudController().getLoginInfo().getUsername().equals(UserInfo.ANONYMOUS)) {
//            //sendAnonymousNotificaiotn();
//            //showWarningAndGotoLogin();
//        } else {
//            // 后台自动登录.
//            String userName = PreferenceUtil.getString(PreferenceUtil.SharedUser);
//            String passWord = PreferenceUtil.getString(PreferenceUtil.SharedPassword);
//            String cert = PreferenceUtil.getString(PreferenceUtil.SharedCertificate);
//            int expire = PreferenceUtil.getInt(PreferenceUtil.SharedCertExpire, 0);
//
//            if (!TextUtils.isEmpty(passWord)) {// 普通用户
//                ForeamApp.getInstance().getCloudController().userLogin(userName, passWord, new OnUserLoginListener() {
//                    @Override
//                    public void onUserLogin(int code, String loginInfoStr) {
//                        if (code == ErrorCode.SUCCESS) {
//                            PreferenceUtil.putString(PreferenceUtil.LoginSesion, loginInfoStr);
//                            if (cur_activity != null)
//                                cur_activity.sendBroadcast(new Intent(Actions.ACTION_LOGIN));
//                        } else {
////							Toast.makeText(ForeamApp.getInstance(), "Background login fail!Please Check your network!", Toast.LENGTH_SHORT).show();
//                            PreferenceUtil.putBoolean(PreferenceUtil.AutoLogin, false);// 取消自动登录状态
//                        }
//                    }
//                });
//            } else {// 第三方用户
//                ForeamApp.getInstance().getCloudController().thirdPartyCerLogin(userName, cert, expire, new OnThirdPartyCerLoginResListner() {
//
//                    @Override
//                    public void onThirdPartyCerLoginRes(int code, String loginInfoStr) {
//                        if (code == ErrorCode.SUCCESS) {
//                            PreferenceUtil.putString(PreferenceUtil.LoginSesion, loginInfoStr);
//                            if (cur_activity != null)
//                                cur_activity.sendBroadcast(new Intent(Actions.ACTION_LOGIN));
//                            /*
//                            UserInfo loginInfo = null;
//                            try {
//                                loginInfo = new UserInfo(new JSONObject(loginInfoStr));
//                                PreferenceUtil.putString(PreferenceUtil.LoginSesion, loginInfoStr);
//                                PreferenceUtil.putBoolean(PreferenceUtil.AutoLogin, true);
//                                PreferenceUtil.putString(PreferenceUtil.SharedUser, loginInfo.getUsername());
//                                PreferenceUtil.putInt(PreferenceUtil.SharedCertExpire, loginInfo.getExpire());
//                                PreferenceUtil.putString(PreferenceUtil.SharedCertificate, loginInfo.getCertificate());
//                                PreferenceUtil.putString(PreferenceUtil.SharedPassword, null);
//                                String lastLoginId = PreferenceUtil.getString(PreferenceUtil.lastLoginId);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            */
//
//
//                        } else {
////							Toast.makeText(ForeamApp.getInstance(), "Background login fail!Please Check your network!", Toast.LENGTH_SHORT).show();
//                            PreferenceUtil.putBoolean(PreferenceUtil.AutoLogin, false);// 取消自动登录状态
//                        }
//                    }
//                });
//
//            }
//        }
//    }
//
//    private static OnUserOffLineListener onUserOffLineListener = new OnUserOffLineListener() {
//        @Override
//        public void onUserOffLine(int code) {
//            switch (code) {
//                // Session过期
//                case ErrorCode.ERR_USER_OFFLINE:
//                case ErrorCode.ERR_USR_TOKEN_INVALID:
//                    loginInBackground();
//
//                    break;
//                // 无Session，应该是crash造成的，需重新打开APP.
//                case ErrorCode.ERR_NO_SESIONID:
//                    if (ActivityUtil.isDebugVersion())
//                        Toast.makeText(ForeamApp.getInstance(), "ERR_NO_SESIONID!", Toast.LENGTH_SHORT).show();
//                    LoginUtil.loadServerArea();
//                    LoginUtil.loadLoginSession();
//                    if (!ForeamApp.getInstance().getCloudController().getLoginInfo().getUsername().equals(UserInfo.ANONYMOUS))
//                        restartApp(ForeamApp.getInstance());
//                    break;
//            }
//        }
//    };
//
//
//    public static String getVersionName() {
//        PackageManager manager = ForeamApp.getInstance().getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(ForeamApp.getInstance().getPackageName(), 0);
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return info.versionName;
//    }
//
//    /**
//     * 分享帖子
//     *
//     * @param mContext
//     * @param post
//     */
////    public static void sharePost(final Activity mContext, final Post post) {
////        if (filterMenu != null && filterMenu.isShowing()) return;//只允许一个DIALOG Show出来
////
////        filterMenu = new QuickEditDialog(mContext, post);
////        SocialShareUtil.addShareFilterMenu(filterMenu);
////        filterMenu.show(null);
////        filterMenu.setOnActionItemClickListener(new OnActionItemClickListener() {
////
////            @Override
////            public void onItemClick(Object source, int pos, int actionId) {
////                SocialShareUtil.dealShareFilterMenu(mContext, actionId, post);
////
////            }
////        });
////    }
//
//
//    public static String getVersionCode() {
//        PackageManager manager = ForeamApp.getInstance().getPackageManager();
//        PackageInfo info = null;
//        try {
//            info = manager.getPackageInfo(ForeamApp.getInstance().getPackageName(), 0);
//        } catch (NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return info.versionName;
//    }
//
//    /**
//     * V x.x.x 是正式版. V x.x.x Debug 是开发版. V x.x.x Alpha 是发布前的稳定版.
//     * Debug is debug version.
//     *
//     * @return
//     */
//    public static boolean isDebugVersion() {
//
//        if (ActivityUtil.getVersionName().toLowerCase(Locale.getDefault()).contains("debug")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public static boolean isAlphaVersion() {
//        if (ActivityUtil.getVersionName().toLowerCase(Locale.getDefault()).contains("alpha")) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public static boolean isDevelopingMode() {
//        File file = new File(Environment.getExternalStorageDirectory() + "/foream_debug.txt");
//        return file.exists();
//    }
//
//    public static void startUserPostsActivity(Activity context, String username, int userId) {
//        if (userId == ForeamApp.getInstance().getCloudController().getLoginInfo().getUserId()) {
//          /*  Intent intent = new Intent(context, UserPostsActivity.class);
//            intent.putExtra(Intents.EXTRA_USER_NAME, username);
//            intent.putExtra(Intents.EXTRA_USER_ID, userId);
//            context.startActivity(intent);
//            context.overridePendingTransition(R.anim.trans_in_right, R.anim.stay);*/
////            Intent intent_post = new Intent(context, MyPostsFilterActivity.class);
////            intent_post.putExtra(Intents.EXTRA_POST_FILTER, NetDiskController.KIND_CHANNEL);
////            intent_post.putExtra(Intents.EXTRA_POST_EXCEPT_CHANAL, 1);
////            context.startActivity(intent_post);
//        } else {
////            Intent i = new Intent(context, UserCenterActivity.class);
////            UserInfo userInfo = new UserInfo();
////            userInfo.setUserId(userId);
////            userInfo.setUsername(username);
////            Bundle bundle = new Bundle();
////            bundle.putSerializable(Intents.EXTRA_USER_INFO, userInfo);
////            i.putExtras(bundle);
////            context.startActivity(i);
//        }
//    }
//
//    /*
//     * 找到一个合适的用于连接网络的Wi-Fi
//     */
//    public static String getAppositeWifi(Context context) {
//        List<ScanResult> scanResult = NetworkUtil.getScanedWifi(context);
//        String lastWifi = PreferenceUtil.getString(PreferenceUtil.lastWifiInLogin);
//        ScanResult strongestWifi = null;
//
//        // 尝试找到并连接上次登录的WIFI
//        if (NetworkUtil.isConfiguredWifi(context, lastWifi)) {
//            return lastWifi;
//        }
//
//        // 尝试找到并附近信号最强的WIFI
//        for (ScanResult item : scanResult) {
//
//            if (!StringUtil2.isForeamCamSSid(item.SSID) && NetworkUtil.isConfiguredWifi(context, item.SSID)) {
//                if (strongestWifi == null) {
//                    strongestWifi = item;
//                } else {
//                    if (item.level > strongestWifi.level) {
//                        strongestWifi = item;
//                    }
//                }
//            }
//        }
//        if (strongestWifi != null) {
//            return strongestWifi.SSID;
//        }
//
//
//        return null;
//    }
//
//    public static void connectAppositeWifi(Context context, String cameraSsid) {
//        String appositeWifi = getAppositeWifi(context);
//        if (appositeWifi != null) {
//            NetworkUtil.connectConfiguredWifi(context, appositeWifi);
//        } else {
//            // 没找到合适的WIFI,直接断开连接.
//            NetworkUtil.disconnectWifi(context, cameraSsid);
//        }
//    }
//
//    public static boolean isExpireOutOfDate(String videoUrl) {
//        String[] strings = videoUrl.split("&");
//        for (int i = 0; i < strings.length; i++) {
//            String item = strings[i];
//            if (item.startsWith("expire=")) {
//                String expireStr = item.replace("expire=", "");
//                try {
//                    Long value = Long.parseLong(expireStr);
//                    Date date = new Date(value);
//                    Date now = new Date(System.currentTimeMillis());
//                    if (date.before(now)) {// 过期的地址
//                        return true;
//                    } else {
//                        return false;
//                    }
//                } catch (NumberFormatException exception) {
//                    exception.printStackTrace();
//                }
//            }
//        }
//        return false;
//    }
//
//    public static int checkNoResponseReason(Context context) {
//        if (!NetworkUtil.isConnected(context) || NetworkUtil.getCurrentNetworkType(context) == ConnectivityManager.TYPE_MOBILE) {
//            // Wi-Fi网络已断开
//            return R.string.error_wifi_disconnect;
//        } else {
//            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifiManager.getConnectionInfo();
//            int level = WifiManager.calculateSignalLevel(info.getRssi(), 5);
//            if (level <= 2) {
//                // Wi-Fi信号太弱,请靠近摄像机
//                return R.string.error_wifi_signal_leak;
//            } else {
//                // CAM无法通讯,请尝试重启摄像机
//                return R.string.error_cam_noresponse;
//            }
//        }
//
//    }
//
//    public static void openAppInMarket(Context context) {
//        if (context != null) {
//            String pckName = context.getPackageName();
//            try {
//                gotoMarket(context, pckName);
//            } catch (Exception ex) {
//                try {
//                    String otherMarketUri = "http://market.android.com/details?id="
//                            + pckName;
//                    Intent intent = new Intent(Intent.ACTION_VIEW,
//                            Uri.parse(otherMarketUri));
//                    context.startActivity(intent);
//                } catch (Exception e) {
//
//                }
//            }
//        }
//    }
//
////    public static void gotoMarket(Context context, String pck) {
////        if (!isHaveMarket(context)) {
////            AlertDialogHelper.showForeamHintDialog((BaseActivity) context, context.getResources().getString(R.string.not_install_market));
////            return;
////        }
////        Intent intent = new Intent();
////        intent.setAction(Intent.ACTION_VIEW);
////        intent.setData(Uri.parse("market://details?id=" + pck));
////        if (intent.resolveActivity(context.getPackageManager()) != null) {
////            context.startActivity(intent);
////        }
////    }
//
//    public static boolean isHaveMarket(Context context) {
//        Intent intent = new Intent();
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.APP_MARKET");
//        PackageManager pm = context.getPackageManager();
//        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
//        return infos.size() > 0;
//    }
//
    public static class JSONObjectHelper implements Serializable {
        private String strValue = null;
        private final static String TAG = "JSONObjectHelper";

        //public JSONObjectHelper(JSONObject obj){
        //saveStr(obj);
        //}
        public JSONArray getJSonArray(JSONObject obj, String value) {
            saveStr(obj);
            try {
                return obj.getJSONArray(value);
            } catch (JSONException e) {
                //Log.e(TAG,"Get "+value +" value fail!");
            }
            return null;
        }

        public JSONObject getJSonObject(JSONObject obj, String value) {
            saveStr(obj);
            try {
                return obj.getJSONObject(value);
            } catch (JSONException e) {
                //Log.e(TAG,"Get "+value +" value fail!");
            }
            return null;
        }

        protected String getString(JSONObject obj, String value) {
            saveStr(obj);
            try {
                String result = obj.getString(value);
                if (result != null && result.toLowerCase(Locale.getDefault()).equals("null")) {
                    return null;
                }
                return result;
            } catch (JSONException e) {
                //Log.e(TAG,"Get "+value +" value fail!");
            }
            return null;
        }

        @Override
        public String toString() {
            if (strValue != null) {
                return strValue;
            } else {
                return super.toString();
            }
        }

        protected long getLong(JSONObject obj, String value, long defaultvaule) {
            saveStr(obj);
            try {
                return obj.getLong(value);
            } catch (JSONException e) {
                //Log.e(TAG,"Get "+value +" value fail!");
            }
            return defaultvaule;
        }

        protected boolean getBoolean(JSONObject obj, String value, boolean defaultValue) {
            saveStr(obj);
            try {
                return obj.getBoolean(value);
            } catch (JSONException e) {
                //Log.e(TAG,"Get "+value +" value fail!");
            }
            return defaultValue;
        }

        protected int getInt(JSONObject obj, String value, int defaultvaule) {
            saveStr(obj);
            try {
                return obj.getInt(value);
            } catch (JSONException e) {
                //Log.e(TAG,"Get "+value +" value fail!");
            }
            return defaultvaule;
        }

        /**
         * 缓存 str,为了toString()的返回。
         *
         * @param obj
         */
        private void saveStr(JSONObject obj) {
            if (strValue == null) {
                if (obj != null) {
                    strValue = obj.toString();
                }
            }
        }
    }
}
