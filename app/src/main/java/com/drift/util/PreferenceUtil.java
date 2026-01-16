package com.drift.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.drift.app.ForeamApp;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PreferenceUtil {
	public static final String LastModifyTime(int userName) {
		return "LastModifyTime__" + userName;
	}

	public static final String SavedWifiList(int userName) {
		return "SavedWifiList_" + userName;
	}

	public static final String NotificationType(int type) {
		return "NotificationType_" + type;
	}
	public static final String FistLoginToShowPersonalInfo = "first_login_to_show_personal_info";
	public static final String FavList = "FavList";
	public static final String LastPos = ".lastpos";
	public static final String SHARE_TAG = "user_info";
	public static final String SharedUser = "user";
	public static final String SharedPassword = "pw";
	public static final String SharedCertificate = "cert";
	public static final String SharedClientType = "type";
	public static final String SharedCertExpire = "";
	// public static final String SharedCheckBox = "remme";
	public static final String AutoLogin = "autologin";
	public static final String isThirdPartLogin = "thirdpartlogin";
	public static final String LoginSesion = "LoginSesion";
	public static final String NetworkMode = "network";
	public static final String DebugIP = "debugip";
	public static final String LIVE_CAMERA = "LIVE_CAMERA_NEW";
	public static final String LIVE_WIFI_SELECTED = "LIVE_WIFI_SELECTED";
    public static final String SP_DANMU_OPEN_STATUS="SP_DANMU_OPEN_STATUS";
	public static final String LAST_CAMERA_SELECTED = "LAST_CAMERA_SELECTED";
	public static final String NEWLY_REGISTERED = "NEWLY_REGISTERED";

	public static final String STRING_FOR_OLD_VERSION_X1 = "STRING_FOR_OLD_VERSION_X1";
	public static final String FLAG_FOR_OLD_VERSION_X1 = "FLAG_FOR_OLD_VERSION_X1";



    
//	public static final String LastConnectChannelId(String camera_id){
//		return "LastConnectChannelId_" + camera_id;
//	}
//	public static final String LastConnectChannelTitle(String camera_id){
//		return "LastConnectChannelTitle_" + camera_id;
//	}
	public static final String haveAddFriendsByContact(String username){
		return "haveAddFriendsByContact_" + username;
	}
	public static final String FirstUse(int index) {
		return "firstuse__" + index;
	}
	public static final String FirstUse(String index) {
		return "firstuse__" + index;
	}
	public static final String HasRemindUpgrade(String serialnumber) {
		return "HasRemindUpgrade_" + serialnumber;
	}
	public static final String HasDisableDlna(String serialnumber) {
		return "DisableDlna_" + serialnumber;
	}
	public static final String CacheIndex(String userId,int head_type){
		return "CacheIndex"+userId+head_type;
	}
	public static final String lastLoginId = "lastLoginId";
	public static final String connectedCamModule = "connectedCamModule";
	public static final String addCameraDirectInfo = "addCameraDirectInfo";
	public static final String lastUpgradingCamId = "lastUpgradingCamId";
	public static final String lastCheckVersion = "lastCheckVersion";
	public static final String has_accept_pkinvite_id = "has_accept_pkinvite_id";
	public static final int MODE_HD = 0;
	public static final int MODE_NORMAL = 1;
	public static final String VideoDownMode = "video_mode_";
	public static final String PhotoDownMode = "photo_mode_";

	public static final String RemindNextTime = "remind_next_time";
	public static final String RemindRegNextTime = "RemindRegNextTime";
	public static final String First_Live = "first_live";
	public static final String First_Mycam = "first_mycam";
	public static final String First_Myfile = "first_myfile";
	public static final String First_Shoot = "first_shoot";
	public static final String First_Picture = "first_picture";
	public static final String First_Video = "first_video";
	public static final String First_TimeMove = "first_timemove";
	public static final String FirstGotoRegCam = "firstGotoRegCam";

	public static final String LastOpenFragment = "LastOpenFragment";
	public static final String lastWifiInLogin = "lastWifiInLogin";
	public static final String lastRegisterCamId = "lastRegisterCamId";
	public static final String lastRegisterUserId = "lastRegisterUserId";
	public static final String lastRegisterCamName = "lastRegisterCamName";
	public static final String lastRegisterTime = "lastRegisterTime";
	public static final String isRegisteringCam = "isRegisteringCam";
	public static final String lastMsgId = "lastMsgId";
	
	public static final String IS_CLICK_CANCEL = "ISCLICKCANCEL";

	public static final String NewCamReg = "RegNewCam";
	public static final String NewCamName = "NewCamName";
	public static final String NewCamId = "NewCamId";
	public static final String NewCamKey = "NewCamKey";

	public static final String ActiveLater = "activelater";

	public static final String ModuleInfos = "ModuleInfos_";

	public static final String appStartInfo = "app_start_info";
	
	

	public static final String LocalDelaySec = "localDelaySec";
	
	public static final String DebugImageLoading = "DebugImageLoading";
	
	public static final String DebugCloudCmd = "DebugCloudCmd";
	
	public static final String DebugUploadFile = "DebugUploadFile";
	
	public static final String DevelopingMode = "DevelopingMode";
	
	public static final String PopupDirectCamInfo = "PopupDirectCamInfo";

	public static final String FirstLoginInSelectSocialOrWifiDirect = "FirstLoginInSelectSocialOrWifiDirect"; 
	
	public final static int STREAM_TYPE_SMOOTH = 0;
	public final static int STREAM_TYPE_REALTIME = 1;
	public static final String StreamPlayType = "StreamPlayType";

	public static final String VideoRes = "VideoRes";
	
	public static final String FirstPublishLargeFileIn3G = "PublishLargeFileIn3G";
	public static final String IsHasCamOnCloud = "IsHasCamOnClound";
	
	public static final String CloudCamFileUploadList = "CloudCamFileUploadList";
	public static final String InviteFriendLuckyDraw = "InviteFriendLuckyDraw";
	
	public static final String TYPE_SET_CAM_DEFAULT_PASSWORD="TYPE_SET_CAM_DEFAULT_PASSWORD";
	public static final String TYPE_COMPASS_DEFAULT_PASSWORD = "TYPE_COMPASS_DEFAULT_PASSWORD";
	public static final String DEFAULT_PASSWORD_COMPASS = "1234567890";
	public static final String TYPE_X1_DEFAULT_PASSWORD = "TYPE_X1_DEFAULT_PASSWORD";
	public static final String DEFAULT_PASSWORD_X1 = "foreamx1";
	
	public static final String FIRST_TO_SHOW_WIFI_DIRECT_STREAM_COMPRESS_HINT = "FIRST_TO_SHOW_WIFI_DIRECT_STREAM_COMPRESS_HINT";
	public static final String FIRST_TO_SHOW_WIFI_DIRECT_IMAGE_COMPRESS_HINT = "FIRST_TO_SHOW_WIFI_DIRECT_IMAGE_COMPRESS_HINT";

	public static int cam_latest_status_live=0;//直播界面
	public static int cam_latest_status_sync=1;//文件同步
	public static String cam_latest_status="cam_latest_status_";

	public static final String LIVE_HAS_CREATED = "LIVE_HAS_CREATED";//判断是否有创建过4K+直播
	public static final String PHONE_ID = "PHONE_ID";//保存手机的id

	private static Context getContext() {
		return ForeamApp.getInstance();
	}

	public static String getString(String key) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);

		return shareInfo.getString(key, null);
	}
	public static String getString(String key,String defaultValue) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);

		return shareInfo.getString(key, defaultValue);
	}
	public static void putString(String key, String value) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		shareInfo.edit().putString(key, value).commit();

	}

	public static int getInt(String key, int defValue) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		return shareInfo.getInt(key, defValue);
	}

	public static void putInt(String key, int value) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		shareInfo.edit().putInt(key, value).commit();
	}

	public static long getLong(String key, long defValue) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		return shareInfo.getLong(key, defValue);
	}

	public static void putLong(String key, Long value) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		shareInfo.edit().putLong(key, value).commit();
	}

	public static boolean getBoolean(String key, boolean defValue) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		return shareInfo.getBoolean(key, defValue);
	}
	public static void putBoolean(String key, boolean value) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		shareInfo.edit().putBoolean(key, value).commit();
	}
	
	public static Set<String> getStringSet(String key) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		return shareInfo.getStringSet(key, new HashSet<String>());
	}
	public static void putStringSet(String key, Set<String> value) {
		SharedPreferences shareInfo = getContext().getSharedPreferences(PreferenceUtil.SHARE_TAG, 0);
		shareInfo.edit().putStringSet(key, value).commit();
		if(value!=null&&value.size()==0)
			shareInfo.edit().putStringSet(key, null).commit();
	}
	
	public static void setStringArrayPref( String key, ArrayList<String> values) {
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ForeamApp.getInstance());
	    SharedPreferences.Editor editor = prefs.edit();
	    JSONArray a = new JSONArray();
	    for (int i = 0; i < values.size(); i++) {
	        a.put(values.get(i));
	    }
	    if (!values.isEmpty()) {
	        editor.putString(key, a.toString());
	    } else {
	        editor.putString(key, null);
	    }
	    editor.commit();
	}

	public static ArrayList<String> getStringArrayPref( String key) {
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ForeamApp.getInstance());
	    String json = prefs.getString(key, null);
	    ArrayList<String> urls = new ArrayList<String>();
	    if (json != null) {
	        try {
	            JSONArray a = new JSONArray(json);
	            for (int i = 0; i < a.length(); i++) {
	                String url = a.optString(i);
	                urls.add(url);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	    }
	    return urls;
	}

	public static final String HasResetDefaultRes(String serialnumber) {
		return "HasResetDefaultRes_" + serialnumber;
	}

}
