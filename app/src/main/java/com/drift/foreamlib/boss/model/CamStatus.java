package com.drift.foreamlib.boss.model;

import android.util.Log;

import com.drift.util.ActivityUtil;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;

public class CamStatus extends ActivityUtil.JSONObjectHelper implements Serializable{

	/**
	 *
	 */
//	private static final long serialVersionUID = 2178979756266811813L;

	private final static String TAG = "CamStatus";

	private String camIP;
	//相机已经离线
	private boolean isOffline;

	//相机状态已经init
	private boolean isInit;

	private CameraStatusNew mCameraStatus;
	private CameraSettingNew mCameraSetting;
	private VideoSettingNew mVideoSetting;
	private StreamSettingNew mStreamSetting;

	private String modelName;

	public CamStatus() {

	}
	public CamStatus(Node XMLDevInfo){

		NodeList properties = XMLDevInfo.getChildNodes();
        for (int j = 0; j < properties.getLength(); j++) {
            Node property = properties.item(j);
            String nodeName = property.getNodeName();
           if(property.getChildNodes().getLength()==0)continue;
			if(nodeName.equals("camera_status"))
			{
				mCameraStatus = new CameraStatusNew(property);
			}
			else if (nodeName.equals("video_setting")) {
				mVideoSetting = new VideoSettingNew(property);
			}
			else if (nodeName.equals("stream_setting")) {
				mStreamSetting = new StreamSettingNew(property);
			}
			else if (nodeName.equals("camera_setting")) {
				mCameraSetting = new CameraSettingNew(property);
			}
			else{//time_stamp
				Log.e(TAG,"Node("+nodeName+":"+property.getFirstChild().getNodeValue()+") not recevie");
			}

        }

	}
	/** Old init method **/
	public CamStatus(JSONObject value) {


	}
	
	/** **/

	public CameraSettingNew getmCameraSettingNew() {
		return mCameraSetting;
	}

	public void setmCameraSettingNew(CameraSettingNew mCameraSetting) {
		this.mCameraSetting = mCameraSetting;
	}

	public String getCamIP() {
		return camIP;
	}

	public void setCamIP(String camIP) {
		this.camIP = camIP;
	}

	public boolean isOffline() {
		return isOffline;
	}

	public void setOffline(boolean offline) {
		isOffline = offline;
	}

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean init) {
		isInit = init;
	}

	public CameraStatusNew getmCameraStatus() {
		return mCameraStatus;
	}

	public void setmCameraStatus(CameraStatusNew mCameraStatus) {
		this.mCameraStatus = mCameraStatus;
	}

	public CameraSettingNew getmCameraSetting() {
		return mCameraSetting;
	}

	public void setmCameraSetting(CameraSettingNew mCameraSetting) {
		this.mCameraSetting = mCameraSetting;
	}

	public VideoSettingNew getmVideoSetting() {
		return mVideoSetting;
	}

	public void setmVideoSetting(VideoSettingNew mVideoSetting) {
		this.mVideoSetting = mVideoSetting;
	}

	public StreamSettingNew getmStreamSetting() {
		return mStreamSetting;
	}

	public void setmStreamSetting(StreamSettingNew mStreamSetting) {
		this.mStreamSetting = mStreamSetting;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
}
