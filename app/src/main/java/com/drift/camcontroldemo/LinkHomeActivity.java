package com.drift.camcontroldemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drift.adapter.LinkCamListAdapter;
import com.drift.app.ForeamApp;
import com.drift.define.Intents;
import com.drift.util.ActivityUtil;
import com.drift.foreamlib.api.CamInfo;
import com.drift.foreamlib.api.ForeamCamCtrl;
import com.drift.foreamlib.boss.model.CamStatus;
import com.drift.foreamlib.local.ctrl.LocalController;
import com.drift.foreamlib.local.ctrl.LocalListener;
import com.drift.foreamlib.util.CommonDefine;
import com.drift.adapter.LinkCamListItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import io.vov.vitamio.Vitamio;

public class LinkHomeActivity extends AppCompatActivity {

    private static String TAG = "LinkHomeActivity";
    private ImageView ivLogo;
    private ImageView ivArrowUp;
    private RecyclerView rvList;
    private ImageView ivArrowDown;
    private TextView tvVersion;
    private RelativeLayout rlAddCamera;

//    protected CameraSettingNew cameraSetting = null;
//    protected CameraStatusNew cameraStatusNew = null;

    private ArrayList<CamStatus> camInfoList;
    private LinkCamListAdapter m_videoListRecycleAdapter;

    private ForeamCamCtrl mForeamCamCtrl;

    private ArrayList<String> camsOnline;

    private Timer loopTimer;

    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;
    private static final int MY_PERMISSIONS_REQUEST_NETWORK_STATE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_home);

        ivLogo = (ImageView) findViewById(R.id.iv_logo);
        ivArrowUp = (ImageView) findViewById(R.id.iv_arrow_up);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        ivArrowDown = (ImageView) findViewById(R.id.iv_arrow_down);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        rlAddCamera = (RelativeLayout) findViewById(R.id.rl_add_camera);
        rlAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LinkHomeActivity.this, LinkAddCameraActivity.class);
                intent.putExtra("camsOnline", camsOnline);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in_right, R.anim.stay);
            }
        });

        camInfoList = new ArrayList<CamStatus>();
        camsOnline = new ArrayList<String>();

        tvVersion.setText("V"+ ForeamApp.getInstance().getVersionCode());
        //test code
//        camsOnline.add("192.168.1.104");
//        camsOnline.add("192.168.1.105");
//        camsOnline.add("192.168.1.107");
//        camsOnline.add("192.168.1.108");
        //test code
        mForeamCamCtrl = ForeamCamCtrl.getInstance();
        mForeamCamCtrl.setOnReceiveUDPMsgListener(mOnReceiveBoardcastMsgListener);
        mForeamCamCtrl.startReceive();

        initTimer();

        initRecycleViewAdapter();

        checkPublishPermission();

//        Vitamio.isInitialized(getApplicationContext( ));

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mForeamCamCtrl.stopReceive();
        mForeamCamCtrl.setOnReceiveUDPMsgListener(null);
        freeTimer();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mForeamCamCtrl.startReceive();
        mForeamCamCtrl.setOnReceiveUDPMsgListener(mOnReceiveBoardcastMsgListener);
        initTimer();
    }

    @Override
    protected void onDestroy()
    {
        mForeamCamCtrl.stopReceive();
        super.onDestroy();
    }

    private void initRecycleViewAdapter()
    {
        //初始化4个数据,全部赋值为不在线
        for (int index = 0; index < 4; ++index) {
//            initRecycleViewData(arrayPaths.get(index));
            CamStatus cameraStatus = new CamStatus();
            cameraStatus.setOffline(true);
            camInfoList.add(cameraStatus);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(LinkHomeActivity.this);
        rvList.setLayoutManager(layoutManager);
        rvList.addItemDecoration(new LinkCamListItemDecoration(dip2px(15),dip2px(15)));
        m_videoListRecycleAdapter = new LinkCamListAdapter(LinkHomeActivity.this, camInfoList);
        m_videoListRecycleAdapter.setOnRecordClickListener(new LinkCamListAdapter.OnRecordClickListener() {
            @Override
            public void OnRecordClickListener(View view, int position) {
//                CamStatus cameraStatusNew = camInfoList.get(position);
//
//                if(cameraStatusNew.getmCameraStatus().getCapture_mode()==0) {
//                    if (cameraStatusNew.getmCameraStatus().getRec_time() == 0) {//录像或者拍照均可
//                        startRecord(cameraStatusNew.getCamIP());
//                        cameraStatusNew.getmCameraStatus().setRec_time(1);
//                        m_videoListRecycleAdapter.notifyItemChanged(position);
//                    } else {
//                        stopRecord(cameraStatusNew.getCamIP());
//                        cameraStatusNew.getmCameraStatus().setRec_time(0);
//                        m_videoListRecycleAdapter.notifyItemChanged(position);
//                    }
//                }
//                else
//                {
//                    startRecord(cameraStatusNew.getCamIP());
//                }
            }
        });

       m_videoListRecycleAdapter.setOnSettingClickListener(new LinkCamListAdapter.OnSettingClickListener() {
           @Override
           public void OnSettingClickListener(View view, int pos) {
                Log.d(TAG, "drift test: come co get Setting. pos is " + pos);
               CamStatus cameraStatusNew = camInfoList.get(pos);
               //只支持新的API,其他的无法进入
               if(cameraStatusNew.getmVideoSetting()!=null)
                   startPreviesActivity(cameraStatusNew.getCamIP(), cameraStatusNew);
           }
       });

        rvList.setAdapter(m_videoListRecycleAdapter);

    }

    ForeamCamCtrl.OnReceiveUDPMsgListener mOnReceiveBoardcastMsgListener = new ForeamCamCtrl.OnReceiveUDPMsgListener() {
        public void camIsOnline(String serialNum, String msgValue, String camIP, String ownerId) {
            Log.d(TAG, "收到心跳包，序列号是" + serialNum + " " + msgValue +" " + camIP +" " + ownerId);
//            String phoneId = PreferenceUtil.getString(PreferenceUtil.PHONE_ID);
//            if((!ownerId.equals("")) && ((phoneId==null) || (!phoneId.equals(ownerId))))
//            {//phone id 为空或者心跳包的owner id不一致,忽略掉
//                return;
//            }
//
//            bDeviceDetected = true;//更新状态
//
//            if (msgValue.equals("NCam")) {//相机已经连上WiFi
//                if (map.get(serialNum)==null) {
//                    map.put(serialNum,camIP+"|"+msgValue);
//                    camStatus = 1;
//                    randomTextView.addKeyWord(serialNum + "");
//                    randomTextView.show();
//                    bDeviceIsShown = true;
//                }
//            }
//            else
//            {//下面这种状态表示正在推流了
//                if (map.get(serialNum)==null) {
//                    map.put(serialNum,camIP+"|"+msgValue);
//                    camStatus = 1;
//                    randomTextView.addKeyWord(serialNum + "");
//                    randomTextView.show();
//                    bDeviceIsShown = true;
//                }
//            }
            //判断是否已经在相机组里,如果不在线,则添加进在线相机里
            if(!camsOnline.contains(camIP)){
                camsOnline.add(camIP);
                //超出相机信息数组的长度,需要增加相机信息元素
                if(camsOnline.size()>camInfoList.size())
                {
                    CamStatus cameraStatus = new CamStatus();
                    cameraStatus.setOffline(true);
                    camInfoList.add(cameraStatus);
                    ivArrowDown.setVisibility(View.VISIBLE);
                    ivArrowUp.setVisibility(View.VISIBLE);
                }
            }
            //需要更新相机状态数组
            int index = camsOnline.indexOf(camIP);
            CamStatus cameraStatusNew = camInfoList.get(index);
            if(cameraStatusNew.isOffline()) {
                cameraStatusNew.setOffline(false);
                cameraStatusNew.setCamIP(camIP);
                //将相机的机型也放入
                cameraStatusNew.setModelName(parseModelName(serialNum));
                getCamStatus(camIP);
                m_videoListRecycleAdapter.notifyItemChanged(index);
            }
        }

        public void camIsOffline(String camIP) {
            Log.d(TAG, "drift test: cam "+ camIP + " is Offline");
        }

        public void numberOfCamsOnline(ArrayMap<String, CamInfo> arrayList){

        }

    };

    public void initTimer()
    {
        if(loopTimer!=null)
            return;
        loopTimer = new Timer(true);

        TimerTask task = new TimerTask() {
            public void run() {
                Log.d(TAG,"drift test: come to loop timer");
                for(int i=0; i<camsOnline.size(); i++)
                {
                    CamStatus cameraStatusNew = camInfoList.get(i);
                    if(!cameraStatusNew.isOffline())
                    {//相机不是离线,需要获取相机状态
                        String ipAddr = cameraStatusNew.getCamIP();
                        getCamStatus(ipAddr);
                    }
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
                Log.d(TAG, "drift test: success is "+success+" serverIp is "+serverIp);
                //获取失败,直接退出
                int index = camsOnline.indexOf(serverIp);
                if(index<0)
                    return;
                CamStatus camStatus = camInfoList.get(index);
                Boolean ifNeedUpdate=false;

                if(!success)
                {
                    //这个是由在线变成离线,直接设为离线?那样是否会反复上线,离线?
//                    if(!camStatus.isOffline()) {
                        camStatus.setOffline(true);
                        m_videoListRecycleAdapter.notifyItemChanged(index);
                        return;
//                    }
                }

//                int index = camsOnline.indexOf(serverIp);
//                if(index<0)
//                    return;
//                CamStatus camStatus = camInfoList.get(index);
                //流设置是新增的,肯定是可以确定是否是新的api
                if(status ==null || status.getmStreamSetting()==null)
                {//老的API,不支持
                    if(!camStatus.isInit()) {
                        //如果已经初始化,就不用再初始化,闪屏了
                        camStatus.setInit(true);
                        camStatus.setmCameraSettingNew(status.getmCameraSetting());
                        camStatus.setmVideoSetting(status.getmVideoSetting());
                        camStatus.setmStreamSetting(status.getmStreamSetting());
                        camStatus.setmCameraStatus(status.getmCameraStatus());
                        m_videoListRecycleAdapter.notifyItemChanged(index);
                    }
                    return;
                }

//                Boolean ifNeedUpdate=false;
                if(success) {
                    if((!camStatus.isInit()) || (camStatus.getmCameraStatus().getCapture_mode()!=status.getmCameraStatus().getCapture_mode() )||
                            (camStatus.getmCameraStatus().getBattery()!=status.getmCameraStatus().getBattery()) ||
                                    (camStatus.getmCameraStatus().getSd_free()!=status.getmCameraStatus().getSd_free()) ||
                                            (camStatus.getmCameraStatus().getRec_time()!=status.getmCameraStatus().getRec_time()) ||
                                                    (camStatus.getmVideoSetting().getVideo_res()!=status.getmVideoSetting().getVideo_res()) ||
                                                            (camStatus.getmVideoSetting().getVideo_framerate()!=status.getmVideoSetting().getVideo_framerate()) ||
                                                                    (camStatus.getmVideoSetting().getVideo_bitrate()!=status.getmVideoSetting().getVideo_bitrate()))
                    {
                        ifNeedUpdate=true;
                    }
                    camStatus.setInit(true);

                    camStatus.setmCameraSettingNew(status.getmCameraSetting());
                    camStatus.setmVideoSetting(status.getmVideoSetting());
                    camStatus.setmStreamSetting(status.getmStreamSetting());
                    camStatus.setmCameraStatus(status.getmCameraStatus());

                }
                else
                {
                    ifNeedUpdate=true;
                    camStatus.setOffline(true);
                }
                //更新显示
                if(ifNeedUpdate)
                    m_videoListRecycleAdapter.notifyItemChanged(index);
            }
        });
    }

    private void startRecord(String ipAddr)
    {
        new LocalController().startRecord(ipAddr, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "kc test startRecord:success is "+success);

            }
        });
    }

    private void stopRecord(String ipAddr)
    {
        new LocalController().stopRecord(ipAddr, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "kc test stopRecord:success is "+success);

            }
        });
    }

    private void startPreviesActivity(String ipAddr, CamStatus cameraStatusNew) {
        Intent intent = new Intent(this, LinkCamDetailActivity.class);
        intent.putExtra(Intents.LINK_CAM_IP, ipAddr);
        intent.putExtra(Intents.LINK_CAM_INFO, cameraStatusNew);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_in_right, R.anim.stay);
    }

    /** dip转换成px */
    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dipValue * scale + 0.5f);
    }

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (Build.VERSION.SDK_INT >= 33)
                {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                    permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
                }
            }
//            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
//                permissions.add(Manifest.permission.CAMERA);
//            }
            //升级到10.0,需要提高到访问精确位置的权限
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
//            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
//                permissions.add(Manifest.permission.READ_PHONE_STATE);
//            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[0]),
                        100);
                return false;
            }
        }

        return true;
    }

    private String parseModelName(String value)
    {
        String mCamType = CommonDefine.DriftGhost4KPlus;
        if(value.contains("GX"))
            mCamType = CommonDefine.DriftGhostX;
        else if(value.contains("XL Pro"))
            mCamType = CommonDefine.DriftGhostXLPro;
        else if(value.contains("XL"))
            mCamType = CommonDefine.DriftGhostDC;
        else if(value.contains("X1"))
            mCamType = CommonDefine.X1;
        else if(value.contains("X3"))
            mCamType = CommonDefine.X3;
        return mCamType;
    }

}
