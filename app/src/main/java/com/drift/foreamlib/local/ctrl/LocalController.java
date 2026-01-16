package com.drift.foreamlib.local.ctrl;

import android.os.AsyncTask;
import android.util.Log;

import com.drift.foreamlib.asynctask.BaseSingleThreadAsyncTask;
import com.drift.foreamlib.boss.model.BossDefine;
import com.drift.foreamlib.boss.model.CamStatus;
import com.drift.foreamlib.boss.model.CameraSettingNew;
import com.drift.foreamlib.boss.model.ResCommon;
import com.drift.foreamlib.middleware.ctrl.ReceiverAdapter;
//import com.thoughtworks.xstream.XStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LocalController extends com.drift.foreamlib.local.ctrl.LocalListener {
    private final static String TAG = "LocalController";
    private final static int CMD_START_STREAM_WITH_URL = 1;
    private final static int CMD_START_STREAM = 2;
    private final static int CMD_STOP_STREAM = 3;
    private final static int CMD_SET_ZOOM = 4;
    private final static int CMD_SET_EXPOSURE = 5;
    private final static int CMD_SET_STREAM_BITRATE = 6;
    private final static int CMD_SET_FILTERS = 7;
    private final static int CMD_SET_STREAM_RESOLUTION = 8;
    private final static int CMD_REBOOT = 9;
    private final static int CMD_SHUTDOWN = 10;
    private final static int CMD_SETTIME = 11;
    private final static int CMD_GETCAMSETTING = 12;
    private final static int CMD_GETCAMSTATUS = 13;
    private final static int CMD_START_RECORD = 14;
    private final static int CMD_STOP_RECORD = 15;
    private final static int CMD_SET_LED = 16;
    private final static int CMD_SET_VIDEO_BITRATE = 17;
    private final static int CMD_SET_VIDEO_FRAMERATE = 18;
    private final static int CMD_SET_VIDEO_RESOLUTION = 19;
    private final static int CMD_SET_MIC_SENSITIVITY = 20;
    private final static int CMD_SET_STREAM_FRAMERATE = 21;
    private final static int CMD_GETCAMFOLDERS = 22;
    private final static int CMD_GETCAMFILES = 23;
    private final static int CMD_DELETEFILES = 24;


    private final LinkedList<AsyncTask<?, ?, ?>> mTasks;
    private ReceiverAdapter mReceiver;
    private static String serverIp;



    public LocalController() {
        mTasks = new LinkedList<AsyncTask<?, ?, ?>>();
    }

    // public BossController(BossReceiverAdapter receiverAdapter) {
    // mReceiver = receiverAdapter;
    // mTasks = new LinkedList<LoadXMLTask>();
    // }

    private int getCommonResult(Element rootElement) {
        try {

            ResCommon res = new ResCommon(rootElement);
            return res.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BossDefine.RES_FAIL;
    }

    private class LoadXMLTask extends BaseSingleThreadAsyncTask<String, Integer, String> {
        private final int mCmdType;
        private final int mValue;
        private final String mReturnIp;//返回发送的服务器ip地址,用以区分返回给哪个设备.

        public LoadXMLTask(int cmdType, LocalController localCtrller) {
            this(cmdType, localCtrller, 0, null);
        }

        public LoadXMLTask(int cmdType, LocalController localCtrller, String returnIp) {
            this(cmdType, localCtrller, 0, returnIp);
        }

        public LoadXMLTask(int cmdType, LocalController localCtrller, int value, String returnIp) {
            mCmdType = cmdType;
            mValue = value;
            mReturnIp = returnIp;
//            Log.d(TAG, "returnIp:" + returnIp);
        }

        protected String doInBackground(String... params) {
            runningTask++;
            Log.d(TAG, "runningTask:" + runningTask);
            try {
                String urlStr;
                if (serverIp != null && !serverIp.equals("")) {
                    urlStr = params[0].replace("192.168.42.1", serverIp);
                } else {
                    urlStr = params[0];
                }
                URL url = new URL(urlStr);
                Log.d(TAG, params[0]);
                URLConnection conn = (URLConnection) url.openConnection();
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String s = "";
                StringBuffer sb = new StringBuffer("");
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
                br.close();
                runningTask--;
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                runningTask--;
                return null;
            }
        }

        protected void onPostExecute(String result) {
            mTasks.remove(this);
            onReceiveData(mCmdType, result, mValue, mReturnIp);
            super.onPostExecute(result);
        }

        protected void onPreExecute() {

        }

    }

//	public void rebootCamera() {
//		String path;
//		path = BossDefine.REBOOT_CAMERA;
//		LoadXMLTask task = new LoadXMLTask(CMD_REBOOT_CAMERA, this);
//		mTasks.add(task);
//		task.executeOnThreadPool(path);
//
//	}

    public void startPushStreamWithURL(String serverIp, String rtmpUrl, String streamRes, String streamBitrate, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        rtmpUrl = rtmpUrl.replace("rtmp://", "");//去掉rtmp前缀
        rtmpUrl = rtmpUrl.replace("&", "***");
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_START_STREAM_WITH_URL, serverIp, rtmpUrl, streamRes, streamBitrate);
        LoadXMLTask task = new LoadXMLTask(CMD_START_STREAM_WITH_URL, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);

    }

    public void startPushStream(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_START_STREAM, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_START_STREAM, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void stopPushStream(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_STOP_STREAM, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_STOP_STREAM, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setZoom(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_ZOOM, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_ZOOM, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }


    public void setExposure(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_EXPOSURE, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_EXPOSURE, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }


    public void setBitrate(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_BITRATE, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_STREAM_BITRATE, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setStreamResolution(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_RESOLUTION, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_STREAM_RESOLUTION, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setStreamFramerate(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_STREAM_FRAMERATE, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_STREAM_FRAMERATE, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setStreamBitrate(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_STREAM_BITRATE, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_STREAM_BITRATE, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setVideoFramerate(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_VIDEO_FRAMERATE, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_VIDEO_FRAMERATE, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setVideoBitrate(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_VIDEO_BITRATE, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_VIDEO_BITRATE, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setVideoResolution(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_VIDEO_RESOLUTION, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_VIDEO_RESOLUTION, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setMicSensitivity(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_MIC_SENSITIVITY, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_MIC_SENSITIVITY, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setFilter(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTING_FILTERS, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_FILTERS, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void reboot(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_REBOOT, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_REBOOT, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void shutdown(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SHUTDOWN, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_SHUTDOWN, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setTime(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SETTIME, serverIp, getTime().replace(" ", "_"));
        LoadXMLTask task = new LoadXMLTask(CMD_SHUTDOWN, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void getCamsetting(String serverIp, OnGetCamSettingListener ls) {//
        String path;
        mOnCamSettingListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_GETCAMSETTING, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_GETCAMSETTING, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void getCamFolders(String serverIp, OnGetCamFoldersListener ls) {//
        String path;
        mOnCamFoldersListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_LIST_FOLDERS, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_GETCAMFOLDERS , this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void getCamFiles(String serverIp, String folderName, OnGetCamFilesListener ls) {//
        String path;
        mOnCamFilesListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_LIST_FILES, serverIp, folderName);
        LoadXMLTask task = new LoadXMLTask(CMD_GETCAMFILES, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void getCamStaus(String serverIp, OnGetCamStatusListener ls) {//
        String path;
        mOnCamStatusListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_GETCAMSTATUS, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_GETCAMSTATUS, this, 0, serverIp);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void startRecord(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_START_RECORD, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_START_RECORD, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void startRecordWithPara(String serverIp, String key1, String key2, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_RECORD_WITH_PARAM, serverIp, key1, key2);
        LoadXMLTask task = new LoadXMLTask(CMD_START_RECORD, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void stopRecord(String serverIp, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_STOP_RECORD, serverIp);
        LoadXMLTask task = new LoadXMLTask(CMD_STOP_RECORD, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void setLed(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_SET_LED, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_SET_LED, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public void delFile(String serverIp, String value, OnCommonResListener ls) {//
        String path;
        mOnCommonResListener = ls;
        path = String.format(Locale.getDefault(), BossDefine.LOCAL_DEL_FILES, serverIp,value);
        LoadXMLTask task = new LoadXMLTask(CMD_DELETEFILES, this);
        mTasks.add(task);
        task.executeOnThreadPool(path);
    }

    public LocalController(ReceiverAdapter receiverAdapter) {
        mReceiver = receiverAdapter;
        mTasks = new LinkedList<AsyncTask<?, ?, ?>>();
    }

    public void onReceiveData(int cmdType, String response, int value) {

        if (response != null) {
            Log.i(TAG, response);
        } else {
            Log.i(TAG, "response is null");
        }
//        XStream xstream = new XStream();

        boolean bSuccess = false;
        int errcode = -1;
        String retVal = "";
        CameraSettingNew cameraSetting = null;
        CamStatus cameraStatus = null;
        String folderData = null;
        String fileData = null;
        int amount = 0;

        if (response != null)
            Log.i(TAG, response);
        if (response == null) {
            if (mOnNoResponseListener != null)
                mOnNoResponseListener.onNoResponse();
            response = "";
        }
        Element rootElement = null;
        try {
            InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // ȡ��DocumentBuilderFactoryʵ��
            DocumentBuilder builder = factory.newDocumentBuilder(); // ��factory��ȡDocumentBuilderʵ��
            Document doc = builder.parse(is); // ���������� �õ�Documentʵ��
            rootElement = doc.getDocumentElement();
            NodeList nodeList = doc.getElementsByTagName("Response");
            Element element = (Element) nodeList.item(0);
            NodeList childNodes = element.getChildNodes();
            Element element1 = (Element) element.getFirstChild();
//            String nodeName = element1.getNodeName();
//            String nodeValue = "";//element1.getFirstChild().getNodeValue();
            if("Status".equals(element1.getNodeName())) {
                retVal =element1.getFirstChild().getNodeValue();
                if(cmdType == CMD_GETCAMSETTING)
                {//获取相机设置
//                    Element element2 = (Element) childNodes.item(1);
//                    cameraSetting = new CameraSettingNew();
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
                    Node XMLData = rootElement.getChildNodes().item(1);
                    cameraSetting = new CameraSettingNew(XMLData);
                }
                else if(cmdType == CMD_GETCAMSTATUS)
                {
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
                    Node XMLData = rootElement.getChildNodes().item(1);
                    cameraStatus = new CamStatus(XMLData);
                }
            }
            else
            {
                if(cmdType == CMD_GETCAMFOLDERS)
                {
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "CMD_GETCAMFOLDERS nodeList Len:" + nodeList2.getLength());
                    if(nodeList2.getLength()>1) {
//                    NodeList nodeList2 = rootElement.getChildNodes();
//                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
//                    Node XMLData = rootElement.getChildNodes().item(1);
                        String amountStr = element1.getFirstChild().getNodeValue();
                        amount = Integer.valueOf(amountStr).intValue();
                        Log.d(TAG, "CMD_GETCAMFOLDERS nodeList Len:" + amount);
                        bSuccess = true;
                    }
                    else
                    {
                        bSuccess = true;
                        amount = 0;
                    }
                }
                else if(cmdType == CMD_GETCAMFILES)
                {
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "CMD_GETCAMFILES nodeList Len:" + nodeList2.getLength());
//                    NodeList nodeList2 = rootElement.getChildNodes();
//                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
//                    Node XMLData = rootElement.getChildNodes().item(1);
                    if(nodeList2.getLength()>2) {
                        String amountStr = element1.getLastChild().getNodeValue();
                        amount = Integer.valueOf(amountStr).intValue();
                        Log.d(TAG, "CMD_GETCAMFILES nodeList Len:" + amount);
                        bSuccess = true;
                    }
                    else
                    {
                        bSuccess = true;
                        amount = 0;
                    }
                }

            }

            Log.d(TAG, "retVal is "+retVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (cmdType) {
            case CMD_START_STREAM_WITH_URL:
			case CMD_START_STREAM:
            case CMD_SET_ZOOM:
            case CMD_SET_EXPOSURE:
            case CMD_SET_STREAM_BITRATE:
            case CMD_SET_FILTERS:
            case CMD_SET_STREAM_RESOLUTION:
            case CMD_SET_STREAM_FRAMERATE:
            case CMD_REBOOT:
            case CMD_SHUTDOWN:
            case CMD_SETTIME:
            case CMD_STOP_STREAM:
            case CMD_START_RECORD:
            case CMD_STOP_RECORD:
            case CMD_SET_LED:
            case CMD_DELETEFILES:
            case CMD_SET_VIDEO_BITRATE:
            case CMD_SET_VIDEO_FRAMERATE:
            case CMD_SET_VIDEO_RESOLUTION:
            case CMD_SET_MIC_SENSITIVITY:

//                bSuccess = (getCommonResult(rootElement) == BossDefine.RES_SUCCESS);
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;
				if (mOnCommonResListener != null)
                    mOnCommonResListener.onCommonRes(bSuccess);
				break;
            case CMD_GETCAMSETTING:
                {
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;

                if (mOnCamSettingListener != null)
                    mOnCamSettingListener.onGetCamSetting(bSuccess,cameraSetting);
            }
            break;
            case CMD_GETCAMFOLDERS:
            {
//                if(retVal.equals("1"))
//                    bSuccess = true;
//                else
//                    bSuccess = false;

                if (mOnCamFoldersListener != null)
                    mOnCamFoldersListener.onGetCamFolders(bSuccess,folderData, amount);
            }
            break;
            case CMD_GETCAMFILES:
            {
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;

                if (mOnCamFilesListener != null)
                    mOnCamFilesListener.onGetCamFiles(bSuccess,fileData, amount);
            }
            break;
            case CMD_GETCAMSTATUS:
            {
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;

                if (mOnCamStatusListener != null)
                    mOnCamStatusListener.onGetCamStatus(bSuccess,cameraStatus,"");
            }
            break;
            default:
                break;
        }
        if (mReceiver != null)
            mReceiver.onDealtResponse(bSuccess);

    }

    public void onReceiveData(int cmdType, String response, int value, String returnIp) {

        if (response != null) {
            Log.i(TAG, response);
        } else {
            Log.i(TAG, "response is null");
        }
//        XStream xstream = new XStream();

        boolean bSuccess = false;
        int errcode = -1;
        String retVal = "";
        CameraSettingNew cameraSetting = null;
        CamStatus cameraStatus = null;
        String folderData = null;
        String fileData = null;
        int amount = 0;

        if (response != null)
            Log.i(TAG, response);
        if (response == null) {
            if (mOnNoResponseListener != null)
                mOnNoResponseListener.onNoResponse();
            response = "";
        }
        Element rootElement = null;
        try {
            InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // ȡ��DocumentBuilderFactoryʵ��
            DocumentBuilder builder = factory.newDocumentBuilder(); // ��factory��ȡDocumentBuilderʵ��
            Document doc = builder.parse(is); // ���������� �õ�Documentʵ��
            rootElement = doc.getDocumentElement();
            NodeList nodeList = doc.getElementsByTagName("Response");
            Element element = (Element) nodeList.item(0);
            NodeList childNodes = element.getChildNodes();
            Element element1 = (Element) element.getFirstChild();
            Element element2 = (Element) element.getLastChild();
//            String nodeName = element1.getNodeName();
//            String nodeValue = "";//element1.getFirstChild().getNodeValue();
            if("Status".equals(element1.getNodeName())) {
                retVal =element1.getFirstChild().getNodeValue();
                if(cmdType == CMD_GETCAMSETTING)
                {//获取相机设置
//                    Element element2 = (Element) childNodes.item(1);
//                    cameraSetting = new CameraSettingNew();
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
                    Node XMLData = rootElement.getChildNodes().item(1);
                    cameraSetting = new CameraSettingNew(XMLData);
                }
                else if(cmdType == CMD_GETCAMSTATUS)
                {
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
                    Node XMLData = rootElement.getChildNodes().item(1);
                    cameraStatus = new CamStatus(XMLData);
                }
            }
            else
            {
                if(cmdType == CMD_GETCAMFOLDERS)
                {
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "CMD_GETCAMFOLDERS nodeList Len:" + nodeList2.getLength());
                    if(nodeList2.getLength()>1) {
//                    NodeList nodeList2 = rootElement.getChildNodes();
//                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
//                    Node XMLData = rootElement.getChildNodes().item(1);
                        folderData = element1.getFirstChild().getNodeValue();
                        String amountStr = element2.getFirstChild().getNodeValue();
                        amount = Integer.valueOf(amountStr).intValue();
                        Log.d(TAG, "CMD_GETCAMFOLDERS nodeList Len:" + amount);
                        bSuccess = true;
                    }
                    else
                    {
                        bSuccess = true;
                        amount = 0;
                        fileData = "";
                    }
                }
                else if(cmdType == CMD_GETCAMFILES)
                {
                    NodeList nodeList2 = rootElement.getChildNodes();
                    Log.d(TAG, "CMD_GETCAMFILES nodeList Len:" + nodeList2.getLength());
//                    NodeList nodeList2 = rootElement.getChildNodes();
//                    Log.d(TAG, "nodeList Len:" + nodeList2.getLength());
//                    Node XMLData = rootElement.getChildNodes().item(1);
                    if(nodeList2.getLength()>1) {
                        fileData = element1.getFirstChild().getNodeValue();
                        String amountStr = element2.getFirstChild().getNodeValue();
                        amount = Integer.valueOf(amountStr).intValue();
                        Log.d(TAG, "CMD_GETCAMFILES nodeList Len:" + amount);
                        bSuccess = true;
                    }
                    else
                    {
                        bSuccess = true;
                        amount = 0;
                        fileData = "";
                    }
                }
            }

            Log.d(TAG, "retVal is "+retVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (cmdType) {
            case CMD_START_STREAM_WITH_URL:
            case CMD_START_STREAM:
            case CMD_SET_ZOOM:
            case CMD_SET_EXPOSURE:
            case CMD_SET_STREAM_BITRATE:
            case CMD_SET_FILTERS:
            case CMD_SET_STREAM_RESOLUTION:
            case CMD_SET_STREAM_FRAMERATE:
            case CMD_REBOOT:
            case CMD_SHUTDOWN:
            case CMD_SETTIME:
            case CMD_STOP_STREAM:
            case CMD_START_RECORD:
            case CMD_STOP_RECORD:
            case CMD_SET_LED:
            case CMD_DELETEFILES:
            case CMD_SET_VIDEO_BITRATE:
            case CMD_SET_VIDEO_FRAMERATE:
            case CMD_SET_VIDEO_RESOLUTION:
            case CMD_SET_MIC_SENSITIVITY:

//                bSuccess = (getCommonResult(rootElement) == BossDefine.RES_SUCCESS);
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;
                if (mOnCommonResListener != null)
                    mOnCommonResListener.onCommonRes(bSuccess);
                break;
            case CMD_GETCAMSETTING:
            {
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;

                if (mOnCamSettingListener != null)
                    mOnCamSettingListener.onGetCamSetting(bSuccess,cameraSetting);
            }
            break;
            case CMD_GETCAMFOLDERS:
            {
//                if(retVal.equals("1"))
//                    bSuccess = true;
//                else
//                    bSuccess = false;

                if (mOnCamFoldersListener != null)
                    mOnCamFoldersListener.onGetCamFolders(bSuccess,folderData,amount);
            }
            break;
            case CMD_GETCAMFILES:
            {
//                if(retVal.equals("1"))
//                    bSuccess = true;
//                else
//                    bSuccess = false;

                if (mOnCamFilesListener != null)
                    mOnCamFilesListener.onGetCamFiles(bSuccess,fileData, amount);
            }
            break;
            case CMD_GETCAMSTATUS:
            {
                if(retVal.equals("1"))
                    bSuccess = true;
                else
                    bSuccess = false;

                if (mOnCamStatusListener != null)
                    mOnCamStatusListener.onGetCamStatus(bSuccess,cameraStatus,returnIp);
            }
            break;
            default:
                break;
        }
        if (mReceiver != null)
            mReceiver.onDealtResponse(bSuccess);

    }

//	private void syncRecCameraStatus(RecordStatus2 recStatus) {
//		cameraStatus.setApp_type(recStatus.getMode());
//		cameraStatus.setBattery(recStatus.getBattery());
//		cameraStatus.setAvailable(recStatus.getMemory());
//		cameraStatus.setDzoom_step(recStatus.getDzoom_step());
//		cameraStatus.setNTC(recStatus.getNtc());
//		// cameraStatus.setPhoto_size(recStatus.getPhoto_size());
//		cameraStatus.setRec_status(recStatus.getRec_status());
//		cameraStatus.setVideo_res(recStatus.getVideo_res());
//	}

    public void cancelAllCommand() {
        // TODO Auto-generated method stub
        while (!mTasks.isEmpty()) {
            mTasks.poll().cancel(true);
        }
    }


    private static int runningTask = 0;

    public String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new Date( ));

        return date;
    }

}
