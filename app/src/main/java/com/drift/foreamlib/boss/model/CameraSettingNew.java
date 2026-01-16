package com.drift.foreamlib.boss.model;

import android.util.Log;

import com.drift.util.ActivityUtil;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;

public class CameraSettingNew extends ActivityUtil.JSONObjectHelper implements Serializable {

    /**
     *This is used for cam setting for new version cam firmware.
     * kc.chen
     * 2021-04-11
     */
    private final static String TAG = "CameraSettingNew";

    /*
    *       <camera_Setting>
				<dzoom>0</dzoom>
				<filter>0</filter>
				<exposure>1</exposure>
				<mic>3</mic>
				<led>1</led>
				<hd_record>0</hd_record>
			</camera_Setting>
    * */
    private int dzoom;

    private int filter;

    private int exposure;

    private int mic;

    private int led;

    private int hd_record;

    public CameraSettingNew() {

    }

    public CameraSettingNew(Node XMLDevInfo) {

        NodeList properties = XMLDevInfo.getChildNodes( );
        for (int j = 0; j < properties.getLength( ); j++) {
            Node property = properties.item(j);
            String nodeName = property.getNodeName( );
            if (property.getChildNodes( ).getLength( ) == 0) continue;
            if (nodeName.equals("dzoom")) {
                dzoom =  Integer.valueOf(property.getFirstChild( ).getNodeValue( ));
            } else if (nodeName.equals("filter")) {
                filter = Integer.valueOf(property.getFirstChild( ).getNodeValue( ));
            } else if (nodeName.equals("exposure")) {
                exposure = Integer.valueOf(property.getFirstChild( ).getNodeValue( ));
            } else if (nodeName.equals("mic")) {
                mic = Integer.valueOf(property.getFirstChild( ).getNodeValue( ));
            } else if (nodeName.equals("led")) {
                led = Integer.valueOf(property.getFirstChild().getNodeValue());
            } else if (nodeName.equals("hd_record")) {
                hd_record = Integer.valueOf(property.getFirstChild().getNodeValue());
            }
            else {
                Log.e(TAG, "Node(" + nodeName + ":" + property.getFirstChild( ).getNodeValue( ) + ") not recevie");
            }

        }

    }

    /**
     * Old init method
     **/
    public CameraSettingNew(JSONObject value) {

//        stream_res = getInt(value, "stream_res", 0);
//        stream_bitrate = getInt(value, "stream_bitrate", 0);
//        dzoom = getInt(value, "dzoom", 0);
//        filter = getInt(value, "filter", 0);
//
//        exposure = getInt(value, "exposure", 0);
//        mic = getInt(value, "mic", 0);
//
//        led = getInt(value, "led", 0);
    }

    /** **/

    public int getDzoom() {
        return dzoom;
    }

    public void setDzoom(int dzoom) {
        this.dzoom = dzoom;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public int getExposure() {
        return exposure;
    }

    public void setExposure(int exposure) {
        this.exposure = exposure;
    }

    public int getMic() {
        return mic;
    }

    public void setMic(int mic) {
        this.mic = mic;
    }

    public int getLed() {
        return led;
    }

    public void setLed(int led) {
        this.led = led;
    }

    public int getHd_record() {
        return hd_record;
    }

    public void setHd_record(int hd_record) {
        this.hd_record = hd_record;
    }
}
