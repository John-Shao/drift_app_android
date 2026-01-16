package com.drift.foreamlib.boss.model;

import android.util.Log;

import com.drift.foreamlib.util.JSONObjectHelper;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;

public class CameraStatusNew extends JSONObjectHelper implements Serializable{

	/**
	 *
	 */
	private final static String TAG = "CameraStatusNew";

	/*
	* 		<camera_status>
				<capture_mode>0</capture_mode>
				<battery>100</battery>
				<sd_free>30052128</sd_free>
				<sd_total>30566400</sd_total>
				<rec_time>0</rec_time>
				<fw_ver>9010</fw_ver>
				<model_name>N2</model_name>
			</camera_status>
	* */
	private int battery;

	private int capture_mode;

	private String model_name;

	private long sd_free;

	private long sd_total;

	private int rec_time;

	private String fw_ver;

	public CameraStatusNew() {

	}
	public CameraStatusNew(Node XMLDevInfo){

		NodeList properties = XMLDevInfo.getChildNodes();
        for (int j = 0; j < properties.getLength(); j++) {
            Node property = properties.item(j);
            String nodeName = property.getNodeName();
           if(property.getChildNodes().getLength()==0)continue;
            if (nodeName.equals("capture_mode")) {
            	capture_mode = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("battery")) {
				battery = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("sd_free")) {
				sd_free = Long.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("sd_total")) {
				sd_total = Long.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("rec_time")) {
				rec_time = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("fw_ver")) {
				fw_ver = property.getFirstChild().getNodeValue();
			}
            else if (nodeName.equals("model_name")) {
            	model_name = property.getFirstChild().getNodeValue();
            }
			else{//time_stamp
            	Log.e(TAG,"Node("+nodeName+":"+property.getFirstChild().getNodeValue()+") not recevie");
            }

        }

	}
	/** Old init method **/
	public CameraStatusNew(JSONObject value) {

	}
	
	/** **/
	public int getBattery() {
		return battery;
	}

	public void setBattery(int battery) {
		this.battery = battery;
	}

	public int getCapture_mode() {
		return capture_mode;
	}

	public void setCapture_mode(int capture_mode) {
		this.capture_mode = capture_mode;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public long getSd_free() {
		return sd_free;
	}

	public void setSd_free(long sd_free) {
		this.sd_free = sd_free;
	}

	public long getSd_total() {
		return sd_total;
	}

	public void setSd_total(long sd_total) {
		this.sd_total = sd_total;
	}

	public int getRec_time() {
		return rec_time;
	}

	public void setRec_time(int rec_time) {
		this.rec_time = rec_time;
	}

	public String getFw_ver() {
		return fw_ver;
	}

	public void setFw_ver(String fw_ver) {
		this.fw_ver = fw_ver;
	}
}
