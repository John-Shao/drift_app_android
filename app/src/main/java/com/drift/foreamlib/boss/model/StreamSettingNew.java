package com.drift.foreamlib.boss.model;

import android.util.Log;

import com.drift.util.ActivityUtil;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;

public class StreamSettingNew extends ActivityUtil.JSONObjectHelper implements Serializable {

    /**
     *This is used for cam setting for new version cam firmware.
     * kc.chen
     * 2021-04-11
     */
    private final static String TAG = "CameraSettingNew";

    /*	<stream_setting>
				<stream_res>3</stream_res>
				<stream_framerate>60</stream_framerate>
				<stream_bitrate>6000000</stream_bitrate>
		</stream_setting>
     */

    private int stream_res;

    private int stream_bitrate;

    private int stream_framerate;

    public StreamSettingNew() {

    }

    public StreamSettingNew(Node XMLDevInfo) {

        NodeList properties = XMLDevInfo.getChildNodes( );
        for (int j = 0; j < properties.getLength( ); j++) {
            Node property = properties.item(j);
            String nodeName = property.getNodeName( );
            if (property.getChildNodes( ).getLength( ) == 0) continue;
            if (nodeName.equals("stream_res")) {
                stream_res = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("stream_bitrate")) {
                stream_bitrate = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("stream_framerate")) {
                stream_framerate = Integer.valueOf(property.getFirstChild().getNodeValue());
            }
            else {
                Log.e(TAG, "Node(" + nodeName + ":" + property.getFirstChild( ).getNodeValue( ) + ") not recevie");
            }

        }

    }

    /**
     * Old init method
     **/
    public StreamSettingNew(JSONObject value) {

        stream_res = getInt(value, "stream_res", 0);
        stream_bitrate = getInt(value, "stream_bitrate", 0);
//        dzoom = getInt(value, "dzoom", 0);
//        filter = getInt(value, "filter", 0);
//
//        exposure = getInt(value, "exposure", 0);
//        mic = getInt(value, "mic", 0);
//
//        led = getInt(value, "led", 0);
    }

    /** **/

    public int getStream_res() {
        return stream_res;
    }

    public void setStream_res(int stream_res) {
        this.stream_res = stream_res;
    }

    public int getStream_bitrate() {
        return stream_bitrate;
    }

    public void setStream_bitrate(int stream_bitrate) {
        this.stream_bitrate = stream_bitrate;
    }


    public int getStream_framerate() {
        return stream_framerate;
    }

    public void setStream_framerate(int stream_framerate) {
        this.stream_framerate = stream_framerate;
    }
}
