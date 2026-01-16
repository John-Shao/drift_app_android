package com.drift.foreamlib.local.ctrl;

import com.drift.foreamlib.boss.model.CamStatus;
import com.drift.foreamlib.boss.model.CameraSettingNew;

public class LocalListener {
//	public interface OnRebootCameraListener {
//		public void onReboosCamera(boolean success);
//	}
//
//	public interface OnSetCameraRegisterInfoListener {
//		public void onSetCameraRegisterInfo(boolean success);
//	}
//
//	public interface OnSetCameraUpgradeListener {
//		public void onSetCameraUpgradeListener(boolean success);
//	}
//
//	public interface OnClearLastRegisterErrnoFlagLitener {
//		public void onClearLastRegisterErrnoFlag(boolean success);
//	}
//
//	public interface OnGetCameraRegisterInfoListener {
//		public void onGetCameraRegisterInfo(boolean success, CloudRegInfo info);
//	}
//
//	public interface OnGetCameraSelfRegisterInfoListener {
//		public void onGetCameraSelfRegisterInfo(boolean success, SelfRegInfo info);
//	}
//
//	public interface OnGetCameraInfoListener {
//		public void onGetCameraInfo(boolean success, DeviceInfo info);
//	}
//
//	public interface OnSwitchWifiModeListener {
//		public void onSwitchWifiMode(boolean success);
//	}
//
//	public interface OnCheckM3u8Listener {
//		public void onCheckM3u8(int state);
//	}

	public interface OnCommonResListener {
		public void onCommonRes(boolean success);
	}

//	public interface OnGetWifiRouterListListener {
//		public void onGetWifiRouterList(boolean errCode, List<SavedWifi> list, long time);
//	}
//
//	public interface OnGetUpdateStatusListener {
//		public void onGetUpdateStatus(boolean success, int rec_status, Date last_update);
//	}
//
	public interface OnGetCamSettingListener {
		public void onGetCamSetting(boolean success, CameraSettingNew status);
	}

	public interface OnGetCamFoldersListener {
		public void onGetCamFolders(boolean success, String foldersData, int amount);
	}

	public interface OnGetCamFilesListener {
		public void onGetCamFiles(boolean success, String filesData, int amount);
	}

	public interface OnGetCamStatusListener {
		public void onGetCamStatus(boolean success, CamStatus status, String serverIp);
	}

	public interface OnNoResponseListener {
		public void onNoResponse();
	}
//
//	public interface UploadStatusListener {
//		public void onUploadStatusChange(int progress);
//	}

//	protected OnCommonResListener mOnFormatSDCard = null;
//	protected OnGetRecordStatusListener mOnGetRecordStatusListener = null;
//	protected OnGetUpdateStatusListener mOnGetUpdateStatusListener = null;
	protected OnNoResponseListener mOnNoResponseListener = null;
//	protected OnRebootCameraListener mOnrebootCameraListener = null;
//	protected OnSetCameraRegisterInfoListener mOnSetCameraRegisterInfoListener = null;
//	protected OnGetCameraInfoListener mOnGetCameraInfoListener = null;
//	protected OnSwitchWifiModeListener mOnSwitchWifiModeListener = null;
//	protected OnGetCameraRegisterInfoListener mOnGetCameraRegisterInfoListener = null;
//	protected OnGetCameraSelfRegisterInfoListener mOnGetCameraSelfRegisterInfoListener = null;
//	protected OnCheckM3u8Listener mOnCheckM3u8Listener = null;
//	protected OnClearLastRegisterErrnoFlagLitener mOnClearLastRegisterErrnoFlagLitener = null;
//	protected OnSetCameraUpgradeListener mOnSetCameraUpgradeListener = null;
//	protected OnCommonResListener mOnSetCameraSelfRegisterInfoListener = null;
//
//	protected OnGetWifiRouterListListener mOnGetWifiRouterListListener = null;

//	protected OnCommonResListener mOnStartStreamListener = null;
//	protected OnCommonResListener mOnStopStreamListener = null;
	protected OnCommonResListener mOnCommonResListener = null;
	protected OnGetCamSettingListener mOnCamSettingListener = null;
	protected OnGetCamStatusListener mOnCamStatusListener = null;
	protected OnGetCamFoldersListener mOnCamFoldersListener = null;
	protected OnGetCamFilesListener mOnCamFilesListener = null;
}
