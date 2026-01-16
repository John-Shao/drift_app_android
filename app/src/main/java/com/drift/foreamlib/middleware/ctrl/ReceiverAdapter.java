package com.drift.foreamlib.middleware.ctrl;

//import com.foreamlib.boss.model.BurstSetting;
//import com.foreamlib.boss.model.CamFile;
//import com.foreamlib.boss.model.CameraSetting;
//import com.foreamlib.boss.model.CameraStatus;
//import com.foreamlib.boss.model.DeviceInfo;
//import com.foreamlib.boss.model.PhotoSetting;
//import com.foreamlib.boss.model.RecordStatus2;
//import com.foreamlib.boss.model.TimelapseSetting;
//import com.foreamlib.boss.model.VideoSetting;
//import com.foreamlib.boss.model.WifiInfoType;
//import com.foreamlib.middleware.model.LatestMedia;
//import com.foreamlib.middleware.model.StreamConfig;

import java.util.List;

public interface ReceiverAdapter {

	
	//****************** Camera Control ****************//
//	 public void onSetPreviewMode(boolean bSuccess, int mode);
//
//	public void onSetConShootMode(boolean bSuccess, int mode);
//	public void onSetDZoom(boolean bSuccess, int value);
//
//	public void onGetCameraInfo(boolean bSuccess, DeviceInfo cameraInfo);
//	public void onGetCameraStatusCloud(boolean bSuccess, CameraStatus camStatus, WifiInfoType wifiInfo);
//	public void onGetCameraSetting(boolean bSuccess, CameraSetting cameraSetting);
//	public void onGetVideoSetting(boolean bSuccess, VideoSetting videoSetting);
//	public void onGetPhotoSetting(boolean bSuccess, PhotoSetting photoSetting);
//	public void onGetTimeLapseSetting(boolean bSuccess, TimelapseSetting timelapseSetting);
////	public void onGetBurstSetting(boolean bSuccess, BurstSetting burstSetting);
//
//	public void onGetCameraStatus(boolean bSuccess, CameraStatus camStatus,
//			WifiInfoType wifiInfo);
//
//	public void onStartRecordVideo(int bSuccess);
//
//	public void onStopRecordVideo(boolean bSuccess);
//
//
//	public void onStartCapture(boolean bSuccess);
//
//	public void onStartConShoot(boolean bSuccess);
//
//	public void onStopConShoot(boolean bSuccess);
//
//
//	public abstract void onGetRecordStatus(boolean bSuccess, RecordStatus2 recStatus);
////	{
////		throwNotImplementException();
////	}
//
//	public void onEnableDLNA(boolean bSuccess, int enable);
//
//	public void onRebootCamera(int bSuccess);
//
//	public void onStartFwDownload(int bSuccess);
//
//	public void onStopFwDownload(int bSuccess);
//
//	//****************** End of Camera Control ****************//
//
//	//****************** Video Setting ****************//
//	public void onSetVideoRes(boolean bSuccess, int res);
//
//	public void onSetVideFramerate(int errCode,int frate);
//
//	public void onSetVideoFOV(int errCode,int fov);
//
//	public void onSetVideoAntiFlicker(int errCode,int flicker);
//	//****************** End of Video Setting ****************//
//
//	//****************** Photo Setting ****************//
//	public void onSetPhotoRes(boolean bSuccess, int res);
//
//	public void onSetPhotoConShooting(int errCode,int internal);
//
//	public void onSetPhotoAEMeter(int errCode,int value);
//
//	public void onSetPhotoWhiteBalance(int errCode,int value);
//
//	public void onSetPhotoContrast(int errCode,int value);
//
//	public void onSetPhotoSelftimer(int errCode,int value);
//	//****************** End of Photo Setting ****************//
//
//	//****************** Common Setting ****************//
//	public void onSetGenMicSen(int errCode,int value);
//
//	public void onSetGenDate(int errCode);
//
//	public void onSetGenCameraOff(int errCode,int value);
//
//	public void onSetGenRotaingLens(int errCode,int value);
//
//	public void onSetGenLEDIndicator(int errCode,int value);
//
//	public void onSetGenBeep(int errCode,int value);
//
//	public void onRestoreCameraDefaultSetting(int errCode);
//
//	public void onSetCameraSetting1(boolean errCode);
//
//	public void onSetApSSID(boolean errCode);
//	//****************** End of Common Setting ****************//
//
//	//****************** Stream setting ****************//
//	public void onGetStreamSetting(int errCode,StreamConfig settings);
//
//	public void onResetStream(boolean bSuccess);
//
//	public void onSetStreamRes(int errCode, int res);
//
//	public void onSetStreamBitRate(boolean bSuccess, int value);
//
//	public void onSetStreamMode(boolean bSuccess, int value);
//
//	public void onSetStreamGop(boolean bSuccess, int value);
//
//	public void onGetCameraCapacity(boolean bSuccess);
//
//	public void onSetStreamEncodeConfig(boolean bSuccess);
//
//	public void onSetCameraRegisterInfo(boolean bSuccess);
//
//	//****************** End of Stream setting ****************//
//
//
//
//	//****************** File Control ****************//
//	public void onDeleteFile(boolean bSuccess);
//
//	public void onListFile(int errCode,List<CamFile> list);
//
//	public void onGetLatestFileChangeTime(int errCode,String time);
//
//	public void onDelMediaFile(int errCode);
//
//	public void onDelMultiMediaFiles(int errCode);
//
//	public void onGetLatestMedia(boolean bSuccess, int mediaType, LatestMedia value);
//
//
//	//****************** End of File Control ****************//
//
//	//****************** Others ****************//
//	public void onResponseError(int errCode);
	
	public void onDealtResponse(boolean bSuccess);
	
	//****************** End of Others ****************//
	
//	public void onSet_Cam_Settings(int errCode);
//
//	public void onResetTsBuffer(int errCode);
	

	
}
