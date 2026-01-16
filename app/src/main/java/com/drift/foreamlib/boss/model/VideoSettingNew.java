package com.drift.foreamlib.boss.model;

import android.util.Log;

import com.drift.foreamlib.util.JSONObjectHelper;

import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;

public class VideoSettingNew extends JSONObjectHelper implements Serializable {

    /**
     *This is used for cam setting for new version cam firmware.
     * kc.chen
     * 2021-04-11
     */
    private final static String TAG = "CameraSettingNew";

    /*
    *       <video_setting>
				<res>3</res>
				<framerate>5</framerate>
				<bitrate>6000000</bitrate>
				<quality>2</quality>
			</video_setting>
    * */

    private int video_res;

    private int video_bitrate;

    private int video_framerate;

    private int video_quality;

    public VideoSettingNew() {

    }

    public VideoSettingNew(Node XMLDevInfo) {

        NodeList properties = XMLDevInfo.getChildNodes( );
        for (int j = 0; j < properties.getLength( ); j++) {
            Node property = properties.item(j);
            String nodeName = property.getNodeName( );
            if (property.getChildNodes( ).getLength( ) == 0) continue;

            if (nodeName.equals("res")) {
                video_res = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("framerate")) {
                video_framerate = Integer.valueOf(property.getFirstChild().getNodeValue());
            }else if (nodeName.equals("bitrate")) {
                video_bitrate = Integer.valueOf(property.getFirstChild().getNodeValue());
            }
            else if (nodeName.equals("quality")) {
                video_quality = Integer.valueOf(property.getFirstChild().getNodeValue());
            }
            else {
                Log.e(TAG, "Node(" + nodeName + ":" + property.getFirstChild( ).getNodeValue( ) + ") not recevie");
            }

        }

    }

    /**
     * Old init method
     **/
    public VideoSettingNew(JSONObject value) {

    }

    /** **/

    public int getVideo_res() {
        return video_res;
    }

    public void setVideo_res(int video_res) {
        this.video_res = video_res;
    }

    public int getVideo_bitrate() {
        return video_bitrate;
    }

    public void setVideo_bitrate(int video_bitrate) {
        this.video_bitrate = video_bitrate;
    }

    public int getVideo_framerate() {
        return video_framerate;
    }

    public void setVideo_framerate(int video_framerate) {
        this.video_framerate = video_framerate;
    }

    public int getVideo_quality() {
        return video_quality;
    }

    public void setVideo_quality(int video_quality) {
        this.video_quality = video_quality;
    }
}
