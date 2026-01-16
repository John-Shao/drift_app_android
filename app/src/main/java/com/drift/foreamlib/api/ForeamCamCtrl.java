package com.drift.foreamlib.api;

import android.os.AsyncTask;
import android.util.Log;

import androidx.collection.ArrayMap;

import com.drift.foreamlib.boss.model.CamStatus;
import com.drift.foreamlib.local.ctrl.LocalController;
import com.drift.foreamlib.local.ctrl.LocalListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kc.chen on 13/04/2018.
 */

public class ForeamCamCtrl {

    private final static String TAG = "ForeamCamCtrl";
    public boolean threadLoop = true;
    public boolean isRunning;
    DatagramSocket server;
    private ReceivePacketsTask mTask;
    private Timer loopTimer;
    private ArrayList<String> camsOnline = new ArrayList<String>();

    private OnReceiveUDPMsgListener mOnReceiveUDPMsgListener;

    public interface OnReceiveUDPMsgListener {
        public void camIsOnline(String serialNum, String msgValue, String camIP, String ownerId);

        public void camIsOffline(String camIP);

        public void numberOfCamsOnline(ArrayMap<String, CamInfo> arrayList);

    }

    public void setOnReceiveUDPMsgListener(OnReceiveUDPMsgListener ls) {
        mOnReceiveUDPMsgListener = ls;
    }

    private ForeamCamCtrl() {
        initTimer();

    }

    public static synchronized ForeamCamCtrl getInstance() {
        return ForeamCamCtrlHolder.instance;
    }

    private static class ForeamCamCtrlHolder {
        private static final ForeamCamCtrl instance = new ForeamCamCtrl();
    }

    public void startReceive() {
        if (mTask != null) {
            if (mTask.isRunning) {
                stopReceive();
                //throw new NullPointerException("Last UDP server is still running.Please call stopReceive()!");
            }
        }
        mTask = new ReceivePacketsTask();
        mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.e(TAG, "startReceive");
    }

    public void stopReceive() {
        if (mTask != null) {
            mTask.unBindPort();
            mTask.threadLoop = false;
            mTask.cancel(true);
            mTask = null;
            Log.e(TAG, "stopReceive");
        }
    }

    public void startScan() {
        stopScan();

        if (mTask != null) {
            mTask.setScaning(true);
        }
        new SendPacketsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stopScan() {
        if (mTask != null) {
            mTask.setScaning(false);
        }
    }

    public String generateQRCode(String ssid, String pwd, String phoneId, String stream_Type) {
        String qrCodeString = null;
        //X1+的二维码格式为: "17|router_ssid|router_password|stream_Type"
        qrCodeString = "17|" + ssid + "|" + pwd + "|" + stream_Type;
        return qrCodeString;
    }

    private class SendPacketsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String messageStr = "CAM";
            int server_port = 5555;
            try {
//                DatagramSocket s = new DatagramSocket();
                InetAddress local = InetAddress.getByName("255.255.255.255");//my broadcast ip
                int msg_length = messageStr.length();
                byte[] message = messageStr.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
                server.send(p);
                Log.d(TAG, "message send");
            } catch (Exception e) {
                Log.d(TAG, "error  " + e.toString());
            }
            return null;
        }
    }

    private class ReceivePacketsTask extends AsyncTask<Void, Integer, Void> {

        public boolean threadLoop = true;
        public boolean isRunning;
        //        DatagramSocket server;
        ArrayMap<String, CamInfo> arrayList = new ArrayMap<>();
        String serialNum = null;
        String address = null;
        String cameraName = null;
        String msgValue = null;
        String ownerId = null;
        Timer scanOnlineCamtimer;
        boolean isScaning = false;//是否正在查询的标志

        public void setScaning(boolean flag) {
            isScaning = flag;
            if (flag) {//正在搜索，需设置一个3s的定时器做更新
                //清空所有数据
                arrayList.clear();
                scanOnlineCamtimer = new Timer(true);

                TimerTask task = new TimerTask() {
                    public void run() {
                        if (isScaning) {
                            Log.d(TAG, "主动查询结束，返回相机在线数目");
                            publishProgress(1);
                            isScaning = false;
                        }
                        scanOnlineCamtimer = null;
                    }
                };
                scanOnlineCamtimer.schedule(task, 3000);
            } else {
                if (scanOnlineCamtimer != null) {
                    scanOnlineCamtimer.cancel();
                    scanOnlineCamtimer = null;
                }
            }
        }


        public void unBindPort() {
            if (server != null)
                server.close();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning = true;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            isRunning = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                startServer();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Integer type = values[0];
            if (type == 5) {
                //心跳
                if (mOnReceiveUDPMsgListener != null) {
                    //判断是否已经在数组中
                    if(!camsOnline.contains(address)) {
                        camsOnline.add(address);
                    }

                    mOnReceiveUDPMsgListener.camIsOnline(cameraName, msgValue, address, ownerId);
                }
                Log.d(TAG, "drift test: 收到心跳");
            } else {//主动查询
                Log.d(TAG, "drift test: 收到主动查询");
                if (mOnReceiveUDPMsgListener != null) {
                    mOnReceiveUDPMsgListener.numberOfCamsOnline(arrayList);
                }
            }

        }

        private void startServer() throws IOException {

            threadLoop = true;
            try {
                server = new DatagramSocket(5555);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (threadLoop && server != null) {
                byte[] recvBuf = new byte[300];
                DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                server.receive(recvPacket);
                String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
                Log.d(TAG, "Drift test: Server Receive " + recvStr +"Cam IP " + recvPacket.getAddress());
                int port = recvPacket.getPort();
                InetAddress addr = recvPacket.getAddress();

                if (recvStr.contains("|")) {
                    String[] strSplit = recvStr.split("\\|");
                    int type = Integer.parseInt(strSplit[0]);//Integer.valueOf(strSplit[0]).intValue();
                    if (strSplit.length ==5 && type == 5) {//心跳包,只接收有5个值的心跳包
                        if (threadLoop) {
                            serialNum = strSplit[1];
                            address = recvPacket.getAddress().toString();
                            address = address.replace("/", "");
                            /*
                            5|FO99801909AJ0356|X1_Q|129/192.168.2.130|N
                            “5|25KVQGNAAAA5QG00|GHOST_4K+|RTMP”
                            */
                            cameraName = strSplit[2];
                            if(cameraName.contains("_")) {
                                String[] CameraNameSplit = strSplit[2].split("_");
                                cameraName = CameraNameSplit[0];
                            }
                            cameraName = cameraName + "_" + strSplit[1].substring(strSplit[1].length()-6);
                            ownerId = strSplit[3];
                            msgValue = strSplit[4];
                            publishProgress(type);
                        }
                    }
                }
            }
            if (server != null)
                server.close();
        }


    }

    public void initTimer()
    {
        if(loopTimer!=null)
            return;
        loopTimer = new Timer(true);

        TimerTask task = new TimerTask() {
            public void run() {
//                Log.d(TAG,"drift test: come to loop timer");
                for(int i=0; i<camsOnline.size(); i++)
                {
                    String camIP = camsOnline.get(i);
                    getCamStatus(camIP);
                }
            }

        };
        loopTimer.schedule(task, 3000, 3000);
    }

    public void freeTimer()
    {
        if (loopTimer != null) {
            loopTimer.cancel();
            loopTimer = null;
        }
    }

    private void getCamStatus(String ipAddr)
    {
        new LocalController().getCamStaus(ipAddr, new LocalListener.OnGetCamStatusListener() {
            @Override
            public void onGetCamStatus(boolean success, CamStatus status, String serverIp) {
                Log.d(TAG, "drift test:success is "+success+" serverIp is "+serverIp);

                if(!success)
                {
                    camsOnline.remove(serverIp);
                    if (mOnReceiveUDPMsgListener != null) {
                        mOnReceiveUDPMsgListener.camIsOffline(serverIp);
                    }
                    return;
                }
                else
                {

                }
            }
        });
    }
}
