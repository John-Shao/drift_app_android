package com.drift.camcontroldemo;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.drift.util.PreferenceUtil;
import com.mylhyl.circledialog.CircleDialog;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.drift.app.ForeamApp;
import com.drift.define.Intents;
import com.drift.foreamlib.api.ForeamCamCtrl;
import com.drift.foreamlib.api.ForeamCamFileCtrl;
import com.drift.util.AlertDialogHelper;
import com.drift.foreamlib.boss.model.CamStatus;
import com.drift.foreamlib.local.ctrl.LocalController;
import com.drift.foreamlib.local.ctrl.LocalListener;
import com.drift.foreamlib.util.CommonDefine;
import com.drift.foreamlib.boss.model.HTMLFile;
import com.xw.repo.BubbleSeekBar;

import org.easydarwin.video.EasyPlayerClient;

import com.drift.camcontroldemo.LinkFileListActivity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import com.drift.fragment.PlayFragment;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Call;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.BufferedSource;

import android.os.Environment;

import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.RequestParams;
import cn.finalteam.okhttpfinal.StringHttpRequestCallback;
//import cn.finalteam.okhttpfinal.sample.http.Api;
import cn.finalteam.toolsfinal.JsonFormatUtils;
import cn.finalteam.okhttpfinal.FileDownloadCallback;

import com.drift.fragment.PlayFragment;
import com.drift.util.FileUtil;

public class LinkCamDetailActivity extends AppCompatActivity implements PlayFragment.OnDoubleTapListener {

    private static String TAG = "LinkCamDetailActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x111;
    private RelativeLayout rlVideoviewContainer;
//    private VideoView mVideoView;
    private RelativeLayout rlBack;
//    private ImageView tvBack;
    private TextView tvSetting;
    private RelativeLayout rlZoom;
    private TextView tvZoom;
    private BubbleSeekBar bsbZoom;
    private TextView tvZoomVal;
    private RelativeLayout rlExposure;
    private TextView tvExposure;
    private BubbleSeekBar bsbExposure;
    private TextView tvExposureVal;
    private RelativeLayout rlBitrate;
    private TextView tvBitrate;
    private BubbleSeekBar bsbBitrate;
    private TextView tvBitrateVal;
    private RelativeLayout rlFilter;
    private TextView tvFilter;
    private BubbleSeekBar bsbFilter;
    private TextView tvFilterVal;
    private RelativeLayout rlRes;
    private TextView tvRes;
    private TextView tvResVal;
    private RelativeLayout rlFps;
    private TextView tvFps;
    private TextView tvFpsVal;
    private RelativeLayout rlLed;
    private Switch switchLed;
    private Switch switchLocalRecord;
    private TextView tvRecordMode;
    private RelativeLayout rlSetting;
    private RelativeLayout rlLive;
    private RelativeLayout rlReboot;
    private RelativeLayout rlPowerOff;
//    private Button btnFps;
//    private TextureView textureView;// = findViewById(R.id.texture_view);
    private ImageButton ibTakephoto;
//    private TextView tvSyncFiles;
    private PlayFragment mRenderFragment;


    private String camIP;
    private CamStatus mCamStatus;
//    private CameraStatusNew cameraStatusNew;
//    private CameraSettingNew cameraSettingNew;

    private boolean isStopStream = false;
    private enum ACTIVITY_STATE {RESUME, PAUSE};
    private ACTIVITY_STATE avtivity_state = ACTIVITY_STATE.RESUME;
//    private int mBufferPercentage;//buffer的百分比

//    private int reloadTimes;
//    private Timer timer;
//    private boolean bFirstLoading;

//    private EasyPlayerClient client;

    private Timer loopTimer;

    private boolean bIgnoreOnce;
    private boolean bPartUpate;

    String rtspUrl;

    /*
     * XL Pro, N1, N2, 4K+
     * */
    private String[] choicesStr4K = new String[]{"24", "25"};
    private String[] choicesStr4KUHD = new String[]{"24", "25", "30"};
    private String[] choicesStr27K = new String[]{"24", "25", "30", "50"};
    private String[] choicesStr1080P = new String[]{"24", "25", "30", "50", "60", "100", "120"};
    private String[] choicesStr720P = new String[]{"25", "30", "50", "60", "200", "240"};
    private String[] choicesStrWVGA = new String[]{"25", "30"};

    /*
    * 1080P       30， 60     （A12 项目，只有1080P30， A9项目，有1080P@30，@60 两个选项）
      720P         30, 60          （A9，A12 项目都开放）
    * */
    private String[] choicesA9LiveStr1080P = new String[]{"30", "60"};
    private String[] choicesLiveStr720P = new String[]{"30", "60"};
    private String[] choicesA12LiveStr1080P = new String[]{"30"};

    /*
     * X3 视频分辨率
     * */
    private String[] choicesX3Str1080P = new String[]{"30"};
    private String[] choicesX3Str720P = new String[]{"30"};

    /*
     * XL 视频分辨率 等新固件出来后再处理
     *
     */


    private String[] choicesStr = null;
    private boolean bFirstInit = true;

    private List<String> mFolderList = new ArrayList<String>();
    List<HTMLFile> mList = new ArrayList<HTMLFile>();
    private int folderFetchingIndex = 0;
    private Map monMap = new HashMap();

    private long mLastReceivedLength;
    private final Handler mHandler = new Handler();
    private boolean bLocalRecord = false;

    private final Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long length = mRenderFragment.getReceivedStreamLength();

            if (length == 0) {
                mLastReceivedLength = 0;
            }

            if (length < mLastReceivedLength) {
                mLastReceivedLength = 0;
            }

//            mBinding.streamBps.setText((length - mLastReceivedLength) / 1024 + "Kbps");
            mLastReceivedLength = length;

            mHandler.postDelayed(this, 1000);
        }
    };

    private Runnable mResetRecordStateRunnable = new Runnable() {
        @Override
        public void run() {
//            ImageView mPlayAudio = mBinding.liveVideoBarRecord;
//            mPlayAudio.setImageState(new int[]{}, true);
//            mPlayAudio.removeCallbacks(mResetRecordStateRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_cam_detail);

        camIP = getIntent().getExtras().getString(Intents.LINK_CAM_IP);
        mCamStatus = (CamStatus) getIntent().getExtras().getSerializable(Intents.LINK_CAM_INFO);
        //同步相机时间
        setTime();

        //设置流的分辨率和码率,只针对没有设置过的情况
        if (!PreferenceUtil.getBoolean(PreferenceUtil.HasResetDefaultRes(camIP), false))
        {
            PreferenceUtil.putBoolean(PreferenceUtil.HasResetDefaultRes(camIP), true);
            if (mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC)) {
             /*
            enum VIDEO_RES {
             VRES_1080P,  //0
             VRES_960P,   //1
             VRES_720P,
             VRES_WVGA,
             VIDEO_RES_OPTION_NUM
            };
            */
                if (mCamStatus.getmStreamSetting().getStream_res() != 2) {//720P
                    setStreamResolution(2);
                }

                //where stream bitrate value is in Byte, 1000000 means 1Mbyte, 8Mbps
                //2000000
                if (mCamStatus.getmStreamSetting().getStream_bitrate() != 2000000) {//2M
                    setBitRate((int) 2000000 / 8);
                }

                setStreamBitrate(2 * 1000000 / 8);

            } else if (mCamStatus.getModelName().equals(CommonDefine.DriftGhostXLPro) || mCamStatus.getModelName().equals(CommonDefine.DriftGhost4KPlus)) {
             /*
                VRES_4K = 0,
                VRES_4KUHD,  //1
                VRES_27K,    //2
                VRES_1080P,   //3
                VRES_720P,    //4
                VRES_WVGA,    //5
            */
                if (mCamStatus.getmStreamSetting().getStream_res() != 0) {//1080P
                    setStreamResolution(3);
                }

                //where stream bitrate value is in Byte, 1000000 means 1Mbyte, 8Mbps
                //4000000
                if (mCamStatus.getmStreamSetting().getStream_bitrate() != 4000000) {//4M
                    setBitRate((int) 4000000 / 8);
                }

                setStreamBitrate(4 * 1000000 / 8);
            } else if (mCamStatus.getModelName().equals(CommonDefine.N1) || mCamStatus.getModelName().equals(CommonDefine.N2)) {//N1和N2本地录像功能按钮不可见
             /*
                VRES_4K = 0,
                VRES_4KUHD,  //1
                VRES_27K,    //2
                VRES_1080P,   //3
                VRES_720P,    //4
                VRES_WVGA,    //5
            */
                if (mCamStatus.getmStreamSetting().getStream_res() != 0) {//1080P
                    setStreamResolution(3);
                }

                //where stream bitrate value is in Byte, 1000000 means 1Mbyte, 8Mbps
                //4000000
                if (mCamStatus.getmStreamSetting().getStream_bitrate() != 4000000) {//4M
                    setBitRate((int) 4000000 / 8);
                }
                switchLocalRecord.setVisibility(View.INVISIBLE);
                tvRecordMode.setVisibility(View.INVISIBLE);
            }
        }
        rtspUrl = "rtsp://"+camIP+"/live";

        if (savedInstanceState == null) {
            ResultReceiver rr = getIntent().getParcelableExtra("rr");

            if (rr == null) {
                rr = new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);

                        if (resultCode == PlayFragment.RESULT_REND_START) {
                            onPlayStart();
                        } else if (resultCode == PlayFragment.RESULT_REND_STOP) {
                            onPlayStop();
                        } else if (resultCode == PlayFragment.RESULT_REND_VIDEO_DISPLAY) {
                            onVideoDisplayed();
                        }
                    }
                };
            }

            PlayFragment fragment = PlayFragment.newInstance(rtspUrl, 2, 0, rr);
            fragment.setOnDoubleTapListener(this);

            getSupportFragmentManager().beginTransaction().add(R.id.render_holder, fragment).commit();
            mRenderFragment = fragment;
        } else {
            mRenderFragment = (PlayFragment) getSupportFragmentManager().findFragmentById(R.id.render_holder);
        }

        rlVideoviewContainer = (RelativeLayout) findViewById(R.id.rl_videoview_container);
//        mVideoView = (VideoView) findViewById(R.id.newshoot_surface);
        rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvSetting = (TextView) findViewById(R.id.tv_setting);
        rlZoom = (RelativeLayout) findViewById(R.id.rl_zoom);
//        tvZoom = (TextView) findViewById(R.id.tv_zoom);
        bsbZoom = (BubbleSeekBar) findViewById(R.id.bsb_zoom);
        tvZoomVal = (TextView) findViewById(R.id.tv_zoom_val);
        rlExposure = (RelativeLayout) findViewById(R.id.rl_exposure);
//        tvExposure = (TextView) findViewById(R.id.tv_exposure);
        bsbExposure = (BubbleSeekBar) findViewById(R.id.bsb_exposure);
        tvExposureVal = (TextView) findViewById(R.id.tv_exposure_val);
        rlBitrate = (RelativeLayout) findViewById(R.id.rl_bitrate);
//        tvBitrate = (TextView) findViewById(R.id.tv_bitrate);
        bsbBitrate = (BubbleSeekBar) findViewById(R.id.bsb_bitrate);
        tvBitrateVal = (TextView) findViewById(R.id.tv_bitrate_val);
        rlFilter = (RelativeLayout) findViewById(R.id.rl_filter);
//        tvFilter = (TextView) findViewById(R.id.tv_filter);
        bsbFilter = (BubbleSeekBar) findViewById(R.id.bsb_filter);
        tvFilterVal = (TextView) findViewById(R.id.tv_filter_val);
//        rlSettingBg = (RelativeLayout) findViewById(R.id.rl_setting_bg);
        rlRes = (RelativeLayout) findViewById(R.id.rl_res);
        rlRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换分辨率,如果打开了录像模式,需要切换录影分辨率,否则设置的是流分辨率
                showResOption();
            }
        });

        rlFps = (RelativeLayout) findViewById(R.id.rl_fps);
        rlFps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //针对分辨率,确定不同的帧率范围,并进行切换
                showFpsOption();
            }
        });
        tvRecordMode = (TextView) findViewById(R.id.tv_record_mode);
        switchLocalRecord = (Switch) findViewById(R.id.switch_local_record);
        switchLocalRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {//切换到本地录像模式
                    setLocalRecord(true);
                    tvRecordMode.setText(R.string.link_local_record);
                    setResBtnStatus(false);
                    setFpsBtnStatus(false);
                }
                else
                {//切换到相机录像模式
                    setLocalRecord(false);
                    tvRecordMode.setText(R.string.link_remote_record);
                    setResBtnStatus(true);
                    setFpsBtnStatus(true);
                }
            }
        });

        rlLed = (RelativeLayout) findViewById(R.id.rl_led);
        switchLed = (Switch) findViewById(R.id.switch_led);
        switchLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {//打开led
                    setLed(1);
                }
                else
                {//关闭led
                    setLed(0);
                }
            }
        });
        rlSetting = (RelativeLayout) findViewById(R.id.rl_setting);
        findViewById(R.id.ib_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getCamFolder(camIP);
                //先赋值,再进入
                ForeamApp.getInstance().setCurrentCamIP(camIP);
                Intent intent = new Intent(getActivity( ), LinkFileListActivity.class);
                startActivity(intent);
            }
        });
        rlLive = (RelativeLayout) findViewById(R.id.rl_live);
        findViewById(R.id.ib_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity( ), LinkLiveActivity.class);
                intent.putExtra("camIP",camIP);
                intent.putExtra("streamRes",mCamStatus.getmStreamSetting().getStream_res());
                intent.putExtra("streamBitrate",mCamStatus.getmStreamSetting().getStream_bitrate());
                startActivity(intent);
            }
        });
        findViewById(R.id.ib_meeting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LinkMeetingActivity.class);
                intent.putExtra("device_ip", camIP);
                startActivity(intent);
            }
        });
        rlReboot = (RelativeLayout) findViewById(R.id.rl_reboot);
        findViewById(R.id.ib_reboot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reboot();
            }
        });
        rlPowerOff = (RelativeLayout) findViewById(R.id.rl_power_off);
        findViewById(R.id.ib_power_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                powerOff();
            }
        });
//        textureView = (TextureView)findViewById(R.id.texture_view);
        ibTakephoto = (ImageButton)findViewById(R.id.ib_takephoto);
        ibTakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CameraStatusNew cameraStatusNew = camInfoList.get(position);
                if(bLocalRecord)
                {//本地录影模式
                    LocalRecordOrStop();
                }
                else {
                    //相机录影模式
                    if (mCamStatus.getmCameraStatus().getCapture_mode() == 0) {
                        if (mCamStatus.getmCameraStatus().getRec_time() == 0) {//录像或者拍照均可

                            if (checkMemoryFull()) {
                                Toast.makeText(getActivity(), R.string.card_full, Toast.LENGTH_SHORT).show();
                            }

                            startRecord(camIP);
                            mCamStatus.getmCameraStatus().setRec_time(1);
                            ibTakephoto.setImageResource(R.drawable.link_stoprecord_btn);
                            setResBtnStatus(false);
                            setFpsBtnStatus(false);
                        } else {
                            stopRecord(camIP);
                            mCamStatus.getmCameraStatus().setRec_time(0);
                            ibTakephoto.setImageResource(R.drawable.link_startrecord_btn);
                            setResBtnStatus(true);
                            setFpsBtnStatus(true);
                        }
                    } else {
                        if (checkMemoryFull()) {
                            Toast.makeText(getActivity(), R.string.card_full, Toast.LENGTH_SHORT).show();
                        }

                        startRecord(camIP);
                        ibTakephoto.setImageResource(R.drawable.link_takephoto_btn);
                    }
                }
            }
        });

        rlRes = (RelativeLayout) findViewById(R.id.rl_res);
        tvRes = (TextView) findViewById(R.id.tv_res);
        tvResVal = (TextView) findViewById(R.id.tv_res_val);
        rlFps = (RelativeLayout) findViewById(R.id.rl_fps);
        tvFps = (TextView) findViewById(R.id.tv_fps);
        tvFpsVal = (TextView) findViewById(R.id.tv_fps_val);
        if(mCamStatus.getmCameraSettingNew().getHd_record()==1)
        {
            tvRes.setText(R.string.Link_Record_Res);
            tvFps.setText(R.string.Link_Record_Fps);
        }
        else
        {
            tvRes.setText(R.string.Link_Live_Res);
            tvFps.setText(R.string.Link_Live_Fps);
        }

        /**
         * 参数说明
         * 第一个参数为Context,第二个参数为KEY
         * 第三个参数为的textureView,用来显示视频画面
         * 第四个参数为一个ResultReceiver,用来接收SDK层发上来的事件通知;
         * 第五个参数为I420DataCallback,如果不为空,那底层会把YUV数据回调上来.
         */
//       client = new EasyPlayerClient(this, "EasyPlayer is free!", textureView, null, null);
//        rtspUrl = "rtsp://"+camIP+"/live";
//        //直接播放rtsp?
//        client.play(rtspUrl);

        bsbZoom.getConfigBuilder()
                .min(0)
                .max(10)
                .progress(1)
                .trackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_white))
                .secondTrackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .thumbColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .hideBubble()
                .touchToSeek()
                .build();

        bsbZoom.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                setZoom(progress);
            }

            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                tvZoomVal.setText(progress + "x");
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        bsbExposure.getConfigBuilder()
                .min(-2)
                .max(2)
                .progress(1)
                .trackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_white))
                .secondTrackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .thumbColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .hideBubble()
                .touchToSeek()
                .build();

        bsbExposure.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                int exposureValueSet = 0;
                switch (progress)
                {
                    case -2:
                        exposureValueSet = 4;
                        break;
                    case -1:
                        exposureValueSet = 3;
                        break;
                    case 0:
                        exposureValueSet = 0;
                        break;
                    case 1:
                        exposureValueSet = 1;
                        break;
                    case 2:
                        exposureValueSet = 2;
                        break;
                }
                setExposure(exposureValueSet);
            }

            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                tvExposureVal.setText(progress + "");
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        //替代为mic
        bsbBitrate.getConfigBuilder()
                .min(0)
                .max(5)
                .progress(1)
                .trackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_white))
                .secondTrackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .thumbColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .hideBubble()
                .touchToSeek()
                .build();

        bsbBitrate.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
//                int bitrateValueSet;
//                if(progress==0)
//                    bitrateValueSet = 1000000/8;
//                else if(progress==1)
//                    bitrateValueSet =2500000 / 8;
//                else
//                    bitrateValueSet = 4000000/8;
//                setBitRate(progress);
//                setVideoResolution(progress);
                setMic(progress);
                //更新设置变量的值
                mCamStatus.getmCameraSettingNew().setMic(progress);
                //设置后更新frame rate的值
//                updateFpsWithRes();
            }

            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
//                String resolutionValueDisplay = "4K";
         /*
                VRES_4K = 0,
                VRES_4KUHD,  //1
                VRES_27K,    //2
                VRES_1080P,   //3
                VRES_720P,    //4
                VRES_WVGA,    //5
            */
//                switch (progress)
//                {
//                    case 0:
//                        resolutionValueDisplay = "4K";
//                        break;
//                    case 1:
//                        resolutionValueDisplay = "4KUHD";
//                        break;
//                    case 2:
//                        resolutionValueDisplay = "2.7K";
//                        break;
//                    case 3:
//                        resolutionValueDisplay = "1080P";
//                        break;
//                    case 4:
//                        resolutionValueDisplay = "720P";
//                        break;
//                    case 5:
//                        resolutionValueDisplay = "WVGA";
//                        break;
//                    default:
//                        break;
//                }
//                tvBitrateVal.setText(resolutionValueDisplay);
                tvBitrateVal.setText(progress + "");
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

        bsbFilter.getConfigBuilder()
                .min(0)
                .max(2)
                .progress(1)
                .trackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_white))
                .secondTrackColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .thumbColor(ContextCompat.getColor(getApplicationContext(), R.color.color_link_red))
                .hideBubble()
                .touchToSeek()
                .build();

        bsbFilter.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                setFilter(progress);
            }

            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                if (progress == 0)
                    tvFilterVal.setText(R.string.item_normal);
                else if (progress == 1)
                    tvFilterVal.setText(R.string.item_vivid);
                else
                    tvFilterVal.setText(R.string.item_lowlight);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }
        });

//        new BTTask( ).execute( );
//        getCamSetting();
        getCamStatus(camIP);

        //初始化月份的hash数组
        monMap.put("Jan", "01");
        monMap.put("Feb", "02");
        monMap.put("Mar", "03");
        monMap.put("Apr", "04");
        monMap.put("May", "05");
        monMap.put("Jun", "06");
        monMap.put("Jul", "07");
        monMap.put("Aug", "08");
        monMap.put("Sep", "09");
        monMap.put("Oct", "10");
        monMap.put("Nov", "11");
        monMap.put("Dec", "12");
    }

    public void setTime() {
        Log.d(TAG, "set Time");
        new LocalController().setTime(camIP, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "zoom set success");
            }
        });
    }

    public void setZoom(int value)
    {
        Log.d(TAG, "zoom value is"+value);
        new LocalController().setZoom(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "zoom set success");
            }
        });
    }

    public void setExposure(int value)
    {
        new LocalController().setExposure(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "exposure set success");
            }
        });
    }

    public void setBitRate(int value)
    {
        new LocalController().setBitrate(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "bitrate set success");
                getCamStatus(camIP);
            }
        });
    }

    public void setVideoResolution(int value)
    {
//        client.stop();
        new LocalController().setVideoResolution(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "video resolution set success");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getCamStatus(camIP);
//                        client.play(rtspUrl);
                    }
                }, 1000);
            }
        });
    }

    public void setStreamResolution(int value)
    {
        if(mRenderFragment!=null)
            mRenderFragment.stopLoadStream();
        new LocalController().setStreamResolution(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "stream resolution set success");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(avtivity_state == ACTIVITY_STATE.RESUME) {
                            getCamStatus(camIP);
                            mRenderFragment.reloadStream();
                        }
                    }
                }, 3000);
            }
        });
    }

    //Bitrate在app端不做控制项
    public void setVideoBitrate(int value)
    {
        new LocalController().setVideoBitrate(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "video bitrate set success");

            }
        });
    }

    public void setStreamBitrate(int value)
    {
        new LocalController().setStreamBitrate(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "video bitrate set success");

            }
        });
    }



    public void setVideoFramerate(int value)
    {
//        client.stop();
        new LocalController().setVideoFramerate(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "video framerate set success");
//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        client.play(rtspUrl);
//                    }
//                }, 1000);
            }
        });
    }

    public void setStreamFramerate(int value)
    {
        if(mRenderFragment!=null)
            mRenderFragment.stopLoadStream();
        new LocalController().setStreamFramerate(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "stream framerate set success");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(avtivity_state == ACTIVITY_STATE.RESUME) {
                            getCamStatus(camIP);
                            mRenderFragment.reloadStream();
                        }
                    }
                }, 3000);
            }
        });
    }


    public void setFilter(int value)
    {
        new LocalController().setFilter(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "filter set success");
            }
        });
    }

    public void setMic(int value)
    {
        new LocalController().setMicSensitivity(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "MicSensitivity set success");
            }
        });
    }

//    public void getCamSetting()
//    {
//        new LocalController().getCamsetting(camIP, new LocalListener.OnGetCamSettingListener() {
//            @Override
//            public void onGetCamSetting(boolean success, CameraSettingNew status) {
//                cameraSettingNew = status;
//                cameraSettingNew.setVideo_res(mCamStatus.getmVideoSetting().getVideo_res());
//                cameraSettingNew.setVideo_framerate(mCamStatus.getmVideoSetting().getVideo_framerate());
//                cameraSettingNew.setVideo_bitrate(mCamStatus.getmVideoSetting().getVideo_bitrate());
//                initControlUI(cameraSettingNew);
//            }
//        });
//    }
private void getCamStatus(String ipAddr)
{
    new LocalController().getCamStaus(ipAddr, new LocalListener.OnGetCamStatusListener() {
        @Override
        public void onGetCamStatus(boolean success, CamStatus status, String serverIp) {
            Log.d(TAG, "kc test:success is "+success+" serverIp is "+serverIp);
            if(success) {
//                mCamStatus = status;
                mCamStatus.setmCameraSetting(status.getmCameraSetting());
                mCamStatus.setmVideoSetting(status.getmVideoSetting());
                mCamStatus.setmStreamSetting(status.getmStreamSetting());
                mCamStatus.setmCameraStatus(status.getmCameraStatus());

                if(!bPartUpate) {
                    initControlUI(mCamStatus);
                    bPartUpate = true;
                }
                else
                {//only update the info, not UI.
                    //帧率和分辨率
//                    tvFpsVal.setText(convertFpsValue(mCamStatus.getmVideoSetting().getVideo_framerate())+"");
                    setResolutionStatus(mCamStatus.getmStreamSetting().getStream_res());
                    tvFpsVal.setText(mCamStatus.getmStreamSetting().getStream_framerate()+"");
                }
            }
        }
    });
}

    private void setLocalRecord(boolean value)
    {
        bLocalRecord = value;
    }

    public void setLed(int value)
    {
        new LocalController().setLed(camIP, ""+value, new LocalListener.OnCommonResListener() {
            @Override
            public void onCommonRes(boolean success) {
                Log.d(TAG, "led set success");
            }
        });
    }

    public void reboot()
    {
        AlertDialogHelper.showConfirmDialog(this, this.getString(R.string.title_hint), this.getString(R.string.Control_reboot_ask), this.getString(R.string.yes), new DialogInterface.OnClickListener() {// 确认

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // mCamSettingBar.saveSetting();
//                    PreferenceUtil.putString("CAMSTATE", "online");
                new LocalController().reboot(camIP,  new LocalListener.OnCommonResListener() {
                    @Override
                    public void onCommonRes(boolean success) {
                        //停止直播就返回主界面
//                        Log.d(TAG, "reboot set success");
//                        Intent intent = new Intent(mContext, MainActivity.class);
//                        mContext.startActivity(intent);
//                        ((Activity)mContext).overridePendingTransition(R.anim.stay, R.anim.trans_out_right);
                        finish();
                    }
                });
            }
        }, this.getString(R.string.no), new DialogInterface.OnClickListener() {// 取消

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    public void powerOff()
    {
        AlertDialogHelper.showConfirmDialog(this, this.getString(R.string.title_hint), this.getString(R.string.Control_shutdown_ask), this.getString(R.string.yes), new DialogInterface.OnClickListener() {// 确认

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // mCamSettingBar.saveSetting();
//                    PreferenceUtil.putString("CAMSTATE", "online");
                new LocalController().shutdown(camIP,  new LocalListener.OnCommonResListener() {
                    @Override
                    public void onCommonRes(boolean success) {
                        //停止直播就返回主界面
//                        Log.d(TAG, "shutdown set success");
//                        Intent intent = new Intent(mContext, MainActivity.class);
//                        mContext.startActivity(intent);
//                        ((Activity)mContext).overridePendingTransition(R.anim.stay, R.anim.trans_out_right);
                        finish();
                    }
                });
            }
        }, this.getString(R.string.no), new DialogInterface.OnClickListener() {// 取消

            @Override
            public void onClick(DialogInterface dialog, int which) {

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



    public void initControlUI(CamStatus camStatus)
    {
        if(mCamStatus.getModelName().equals(CommonDefine.DriftGhost4KPlus)) {
            if (camStatus.getmCameraSettingNew().getHd_record() == 1)
                ibTakephoto.setVisibility(View.VISIBLE);
            else
                ibTakephoto.setVisibility(View.INVISIBLE);
        }
        else
        {
            ibTakephoto.setVisibility(View.VISIBLE);
        }

        //变焦
        setZoomStatus(camStatus.getmCameraSettingNew().getDzoom());
        //曝光
        setExposureStatus(camStatus.getmCameraSettingNew().getExposure());
//        setBitrateStatus(cameraSettingNew.getStream_bitrate());
//        setResolutionStatus(cameraStatusNew.getVideo_res());
//        btnFps.setText(convertFpsValue(cameraStatusNew.getVideo_framerate())+"");
        //分辨率和帧率
        if(mCamStatus.getmCameraSettingNew().getHd_record()==1) {
            setResolutionStatus(mCamStatus.getmVideoSetting().getVideo_res());
            tvFpsVal.setText(mCamStatus.getmVideoSetting().getVideo_framerate()+"");
        }
        else {
            setResolutionStatus(mCamStatus.getmStreamSetting().getStream_res());
            tvFpsVal.setText(mCamStatus.getmStreamSetting().getStream_framerate()+"");
        }
        //帧率
//        tvFpsVal.setText(convertFpsValue(mCamStatus.getmVideoSetting().getVideo_framerate())+"");
//        tvFpsVal.setText(mCamStatus.getmVideoSetting().getVideo_framerate()+"");
        //Mic
        setMicStatus(mCamStatus.getmCameraSetting().getMic());
        //滤镜
        setFilterStatus(mCamStatus.getmCameraSetting().getFilter());
        //led
        setLedStatus(mCamStatus.getmCameraSetting().getLed());
        //初始化拍照按钮设置
        if(mCamStatus.getmCameraStatus().getCapture_mode()==0) {
            if (mCamStatus.getmCameraStatus().getRec_time() == 0) {//录像或者拍照均可
                ibTakephoto.setImageResource(R.drawable.link_startrecord_btn);
            } else {
                ibTakephoto.setImageResource(R.drawable.link_stoprecord_btn);
            }
            setResBtnStatus(true);
            setFpsBtnStatus(true);
        }
        else
        {
            ibTakephoto.setImageResource(R.drawable.link_takephoto_btn);
            setResBtnStatus(false);
            setFpsBtnStatus(false);
        }
    }

    private void setZoomStatus(int val)
    {
        bsbZoom.setProgress(val);
        tvZoomVal.setText(val+"x");
    }

    private void setExposureStatus(int val)
    {
        int exposureValueDisplay = 0;
        switch (val)
        {
            case 4:
                exposureValueDisplay = -2;
                break;
            case 3:
                exposureValueDisplay = -1;
                break;
            case 0:
                exposureValueDisplay = 0;
                break;
            case 1:
                exposureValueDisplay = 1;
                break;
            case 2:
                exposureValueDisplay = 2;
                break;
        }
        bsbExposure.setProgress(exposureValueDisplay);
        tvExposureVal.setText(exposureValueDisplay+"");
    }

    private void setFilterStatus(int val)
    {
        bsbFilter.setProgress(val);
        if(val==0)
            tvFilterVal.setText(R.string.item_normal);
        else if(val==1)
            tvFilterVal.setText(R.string.item_vivid);
        else
            tvFilterVal.setText(R.string.item_lowlight);
    }

    private void setMicStatus(int val)
    {
        bsbBitrate.setProgress(val);
        tvBitrateVal.setText(val+"");
    }

    private void setBitrateStatus(int val)
    {
        double bitrateValueDisplay = 2.5;
        int bitrateProgress = 1;
        if(val==1000000)
        {
            bitrateValueDisplay = 1.0;
            bitrateProgress = 0;
        }
        else if(val==2500000) {
            bitrateValueDisplay = 2.5;
            bitrateProgress = 1;
        }
        else {
            bitrateValueDisplay = 4.0;
            bitrateProgress = 2;
        }
//        bsbBitrate.setProgress(bitrateProgress);
//        tvBitrateVal.setText(bitrateValueDisplay+"");
    }

    private void setResolutionStatus(int val)
    {
        String resolutionValueDisplay = "4K";
        if(mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC))
        {
             /*
               enum VIDEO_RES {
            VRES_1080P,  //0
             VRES_960P,   //1
             VRES_720P,
             VRES_WVGA,
             VIDEO_RES_OPTION_NUM
            };
            */
            String resStr = "";
            switch (val)
            {
                case 0:
                    resolutionValueDisplay = "1080P";
                    break;
                case 1:
                    resolutionValueDisplay = "960P";
                    break;
                case 2:
                    resolutionValueDisplay = "720P";
                    break;
                case 3:
                    resolutionValueDisplay = "WVGA";
                    break;
                default:
                    resolutionValueDisplay = "-";
                    break;
            }
//            return resStr;
        }
        else {
              /*
                VRES_4K = 0,
                VRES_4KUHD,  //1
                VRES_27K,    //2
                VRES_1080P,   //3
                VRES_720P,    //4
                VRES_WVGA,    //5
            */
            switch (val) {
                case 0:
                    resolutionValueDisplay = "4K";
                    break;
                case 1:
                    resolutionValueDisplay = "4KUHD";
                    break;
                case 2:
                    resolutionValueDisplay = "2.7K";
                    break;
                case 3:
                    resolutionValueDisplay = "1080P";
                    break;
                case 4:
                    resolutionValueDisplay = "720P";
                    break;
                case 5:
                    resolutionValueDisplay = "WVGA";
                    break;
                default:
                    break;
            }
        }

//        bsbBitrate.setProgress(val);
//        tvBitrateVal.setText(resolutionValueDisplay);
        tvResVal.setText(resolutionValueDisplay);
    }

    private void setLedStatus(int val)
    {
        if(val==0)
            switchLed.setChecked(false);
        else
            switchLed.setChecked(true);
    }

    //帧率转换
    private int convertFpsValue(int val)
    {
        int fpsStr = 25;
        if(mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC) || mCamStatus.getModelName().equals(CommonDefine.X3))
        {
             /*
           enum VIDEO_FRAME {
                 VFR_24,
                 VFR_25,
                 VFR_30,
                 VFR_50,
                 VFR_60,
                 VFR_HDR,
                 VFR_NUM
                };
                    */
//            String fpsStr = "";
            switch (val)
            {
                case 0:
                    fpsStr = 24;
                    break;
                case 1:
                    fpsStr = 25;
                    break;
                case 2:
                    fpsStr = 30;
                    break;
                case 3:
                    fpsStr = 50;
                    break;
                case 4:
                    fpsStr = 60;
                    break;
//                case 5:
//                    fpsStr = "HDR";
//                    break;
                default:
                    fpsStr = 30;
                    break;
            }
//            return fpsStr;
        }
        else {
            switch (val) {
                case 0:
                    fpsStr = 24;
                    break;
                case 1:
                    fpsStr = 25;
                    break;
                case 2:
                    fpsStr = 30;
                    break;
                case 3:
                    fpsStr = 48;
                    break;
                case 4:
                    fpsStr = 50;
                    break;
                case 5:
                    fpsStr = 60;
                    break;
                case 6:
                    fpsStr = 100;
                    break;
                case 7:
                    fpsStr = 120;
                    break;
                case 8:
                    fpsStr = 200;
                    break;
                case 9:
                    fpsStr = 240;
                    break;
                default:
                    fpsStr = 0;
                    break;
            }
        }
        return fpsStr;
    }

    private int convertFpsToIndex(int val)
    {
        int fpsStr = 0;
        switch(val)
        {
            //0:
            case 25://1:
                fpsStr = 1;//125;
                break;
            case 30://2:
                fpsStr = 2;//30;
                break;
            case 48://3:
                fpsStr = 3;//48;
                break;
            case 50://4:
                fpsStr = 4;//50;
                break;
            case 60://5:
                fpsStr = 5;//60;
                break;
            case 100://6:
                fpsStr = 6;//100;
                break;
            case 120://7:
                fpsStr = 7;//120;
                break;
            case 200://8:
                fpsStr = 8;//200;
                break;
            case 240://9:
                fpsStr = 9;//240;
                break;
            default:
                fpsStr = 0;//24;
                break;
        }
        return fpsStr;
    }



    //点击按钮切换视频分辨率
    private void switchRes()
    {
        if(mCamStatus.getmCameraStatus().getRec_time()==1)
        {
//            AlertDialogHelper.showAlertDialog(getActivity(), R.string.note, R.string.Link_stop_record_to_setting, null);
            return;
        }
        int res = mCamStatus.getmVideoSetting().getVideo_res();
        String resolutionValueDisplay = "4K";
        if(mCamStatus.getmCameraSettingNew().getHd_record()==1)
        {
            res = mCamStatus.getmVideoSetting().getVideo_res();
            setVideoResolution(res);

        }
        else
        {
            res = mCamStatus.getmStreamSetting().getStream_res();
            setStreamResolution(res);

            if(mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC)) {
                 /*
                enum VIDEO_RES {
                 VRES_1080P,  //0
                 VRES_960P,   //1
                 VRES_720P,
                 VRES_WVGA,
                 VIDEO_RES_OPTION_NUM
                };
                */
                switch (res)
                {
                    case 0:
                        resolutionValueDisplay = "1080P";
                        setStreamBitrate(3 * 1000000 / 8);
                        break;
                    case 1:
                        resolutionValueDisplay = "960P";
                        break;
                    case 2:
                        resolutionValueDisplay = "720P";
                        setStreamBitrate(2 * 1000000 / 8);
                        break;
                    case 3:
                        resolutionValueDisplay = "WVGA";
                        break;
                    default:
                        resolutionValueDisplay = "-";
                        break;
                }

            }
            else {
               /*
                VRES_4K = 0,
                VRES_4KUHD,  //1
                VRES_27K,    //2
                VRES_1080P,   //3
                VRES_720P,    //4
                VRES_WVGA,    //5
        */
                switch (res) {
                    case 0:
                        resolutionValueDisplay = "4K";
                        break;
                    case 1:
                        resolutionValueDisplay = "4KUHD";
                        break;
                    case 2:
                        resolutionValueDisplay = "2.7K";
                        break;
                    case 3:
                        resolutionValueDisplay = "1080P";
                        setStreamBitrate(4 * 1000000 / 8);
                        break;
                    case 4:
                        resolutionValueDisplay = "720P";
                        setStreamBitrate(2 * 1000000 / 8);
                        break;
                    case 5:
                        resolutionValueDisplay = "WVGA";
                        break;
                    default:
                        break;
                }
            }
        }



        //设置后更新显示的分辨率
        tvResVal.setText(resolutionValueDisplay);

    }

    //点击按钮切换视频帧率
    private void switchFps()
    {
        if(mCamStatus.getmCameraStatus().getRec_time()==1)
        {
//            AlertDialogHelper.showAlertDialog(getActivity(), R.string.note, R.string.Link_stop_record_to_setting, null);
            return;
        }

        int fpsValue = mCamStatus.getmVideoSetting().getVideo_framerate();
        int fps = convertFpsToIndex(fpsValue);
        if(mCamStatus.getmCameraSettingNew().getHd_record()==1) {
            fpsValue = mCamStatus.getmVideoSetting().getVideo_framerate();
            fps = convertFpsToIndex(fpsValue);
            setVideoFramerate(convertFpsValue(fps));
        }
        else
        {
            fpsValue = mCamStatus.getmStreamSetting().getStream_framerate();
            fps = convertFpsToIndex(fpsValue);
            setStreamFramerate(convertFpsValue(fps));
        }
        tvFpsVal.setText(convertFpsValue(fps)+"");
    }

    private void switchDefaultStreamRes()
    {
        int res = mCamStatus.getmStreamSetting().getStream_res();

        String resolutionValueDisplay = "4K";

        if(mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC)) {
             /*
            enum VIDEO_RES {
             VRES_1080P,  //0
             VRES_960P,   //1
             VRES_720P,
             VRES_WVGA,
             VIDEO_RES_OPTION_NUM
            };
            */
            //默认选择720P
            res = 2;
            switch (res)
            {
                case 0:
                    resolutionValueDisplay = "1080P";
                    break;
                case 1:
                    resolutionValueDisplay = "960P";
                    break;
                case 2:
                    resolutionValueDisplay = "720P";
                    break;
                case 3:
                    resolutionValueDisplay = "WVGA";
                    break;
                default:
                    resolutionValueDisplay = "-";
                    break;
            }
        }
        else {
            //默认选择1080P
            res = 3;
            switch (res) {
                case 0:
                    resolutionValueDisplay = "4K";
                    break;
                case 1:
                    resolutionValueDisplay = "4KUHD";
                    break;
                case 2:
                    resolutionValueDisplay = "2.7K";
                    break;
                case 3:
                    resolutionValueDisplay = "1080P";
                    break;
                case 4:
                    resolutionValueDisplay = "720P";
                    break;
                case 5:
                    resolutionValueDisplay = "WVGA";
                    break;
                default:
                    break;
            }
        }
        mCamStatus.getmStreamSetting().setStream_res(res);

        setStreamResolution(res);
        //设置后更新显示的分辨率
        tvResVal.setText(resolutionValueDisplay);
    }

    //点击按钮切换视频帧率
    private void switchStreamFps()
    {
//        if(mCamStatus.equals("N2"))
//        {//N2不支持帧率设置
//            return;
//        }

        int fps = mCamStatus.getmStreamSetting().getStream_framerate();
        //根据分辨率确定帧率范围
        switch(mCamStatus.getmStreamSetting().getStream_res())
        {
            case 0://4K
            {//25 FPS， 24fps
                if(fps==0)
                    fps = 1;
                else
                    fps = 0;
                break;
            }
            case 1://4KUHD
            {//25, 30 FPS, 24fps
                switch(fps)
                {
                    case 0:
                        fps = 1;
                        break;
                    case 1:
                        fps=2;
                        break;
                    default:
                        fps=0;
                        break;
                }
            }
            break;
            case 2://2.7K
            {//25, 30 FPS,24fps,50fps
                switch(fps)
                {
                    case 0:
                        fps=1;
                        break;
                    case 1:
                        fps=2;
                        break;
                    case 2:
                        fps=4;
                        break;
                    default:
                        fps=0;
                        break;
                }
            }
            break;
            case 3://1080P
            {//25,30,50,60,100,120 FPS,24fps
                switch(fps)
                {
                    case 0:
                        fps=1;
                        break;
                    case 1:
                        fps=2;
                        break;
                    case 2:
                        fps=4;
                        break;
                    case 4:
                        fps=5;
                        break;
                    case 5:
                        fps=6;
                        break;
                    case 6:
                        fps=7;
                        break;
                    default:
                        fps=0;
                }
            }
            break;
            case 4://720P
            {//25,30,50,60 FPS,200fps, 240fps
                switch(fps)
                {
                    case 1:
                        fps=2;
                        break;
                    case 2:
                        fps=4;
                        break;
                    case 4:
                        fps=5;
                        break;
                    case 5:
                        fps=8;
                        break;
                    case 8:
                        fps=9;
                        break;
                    default:
                        fps=1;
                }
            }
            break;
            case 5://WVGA
            {//25,30 FPS
                switch(fps)
                {
                    case 1:
                        fps=2;
                        break;
                    default:
                        fps=1;
                        break;
                }
            }
            break;
            default:
                fps=0;
        }
        mCamStatus.getmStreamSetting().setStream_framerate(fps);
//        btnFps.setText(convertFpsValue(fps)+"");
        tvFpsVal.setText(convertFpsValue(fps)+"");
        setStreamFramerate(convertFpsValue(fps));
    }

//    @SuppressLint("ResourceAsColor")
    private void setResBtnStatus(boolean bEnable)
    {
        if(bEnable)
        {
            Resources res = getResources(); //resource handle
            Drawable resDrawable = res.getDrawable(R.drawable.link_res_btn);
            rlRes.setBackground(resDrawable);
            int txt_white = getResources().getColor(R.color.color_link_white);
            tvRes.setTextColor(txt_white);
            tvResVal.setTextColor(txt_white);
        }
        else
        {
            Resources res = getResources(); //resource handle
            Drawable resDrawable = res.getDrawable(R.drawable.link_res_btn_disable);
            rlRes.setBackground(resDrawable);
            int txt_grey = getResources().getColor(R.color.color_link_btn_disable);
            tvRes.setTextColor(txt_grey);
            tvResVal.setTextColor(txt_grey);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void setFpsBtnStatus(boolean bEnable)
    {
        if(bEnable)
        {
            Resources res = getResources(); //resource handle
            Drawable resDrawable = res.getDrawable(R.drawable.link_fps_btn);
            rlFps.setBackground(resDrawable);
            int txt_white = getResources().getColor(R.color.color_link_white);
            tvFps.setTextColor(txt_white);
            tvFpsVal.setTextColor(txt_white);
        }
        else
        {
            Resources res = getResources(); //resource handle
            Drawable resDrawable = res.getDrawable(R.drawable.link_fps_btn_disable);
            rlFps.setBackground(resDrawable);
            int txt_grey = getResources().getColor(R.color.color_link_btn_disable);
            tvFps.setTextColor(txt_grey);
            tvFpsVal.setTextColor(txt_grey);
        }
    }

    private boolean checkMemoryFull()
    {
        //Kb转为Mb,低于400Mb不允许拍照和录像
        if(mCamStatus.getmCameraStatus().getSd_free()/1024<400)
        {
            return true;
        }
        else return false;
    }

    public void initTimer()
    {
        if(loopTimer!=null)
            return;
        loopTimer = new Timer(true);

        TimerTask task = new TimerTask() {
            public void run() {
                Log.d(TAG,"kc test: come to loop timer");
                if(!bIgnoreOnce)
                    getCamStatus(camIP);
                else
                    bIgnoreOnce = false;
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

    @Override
    protected void onPause() {
        super.onPause();
//        freeTimer();
//        client.stop();
        avtivity_state = ACTIVITY_STATE.PAUSE;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        initTimer();
        if(bFirstInit)
            bFirstInit = false;
//        else
//            client.play(rtspUrl);
        avtivity_state = ACTIVITY_STATE.RESUME;
    }

    @Override
    public void onDestroy() {
//        client.stop();
        super.onDestroy();
    }

    public void enterFullScreen()
    {
        WindowManager.LayoutParams lp = getWindow( ).getAttributes( );
        //直接对它flags变量操作   LayoutParams.FLAG_FULLSCREEN 表示设置全屏
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置属性
        getWindow( ).setAttributes(lp);
        ViewGroup.LayoutParams params1 = rlVideoviewContainer.getLayoutParams( );
        params1.width = getRealWidth(getActivity( ));
        params1.height = getRealHeight(getActivity( ));
        Log.e(TAG, "kc test: params1.width" + params1.width + "params1.height" + params1.height);
        View decorView = getActivity( ).getWindow( ).getDecorView( );
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        rlVideoviewContainer.setLayoutParams(params1);
    }

    public void exitFullScreen()
    {
        //获得 WindowManager.LayoutParams 属性对象
        WindowManager.LayoutParams lp2 = getWindow( ).getAttributes( );
        //LayoutParams.FLAG_FULLSCREEN 强制屏幕状态条栏弹出
        lp2.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置属性
        getWindow( ).setAttributes(lp2);
        //不允许窗口扩展到屏幕之外  clear掉了
        getWindow( ).clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ViewGroup.LayoutParams params1 = rlVideoviewContainer.getLayoutParams( );

        params1.height =  getWindowManager( ).getDefaultDisplay( )
                .getWidth( )*9/16;//保持16:9的比例
        params1.width = getWindowManager( ).getDefaultDisplay( )
                .getWidth( );
        Log.e(TAG, "kc test: params1.width" + params1.width + "params1.height" + params1.height);
        rlVideoviewContainer.setLayoutParams(params1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullScreen();
        }
        else
        {
            exitFullScreen();
        }
        super.onConfigurationChanged(newConfig);
    }

    public static int getRealWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay( );
        int screenWidth = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics( );
            display.getRealMetrics(dm);
            screenWidth = dm.widthPixels;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
            } catch (Exception e) {
                DisplayMetrics dm = new DisplayMetrics( );
                display.getMetrics(dm);
                screenWidth = dm.widthPixels;
            }
        }
        return screenWidth;
    }

    public static int getRealHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay( );
        int screenHeight = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics( );
            display.getRealMetrics(dm);
            screenHeight = dm.heightPixels;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
                DisplayMetrics dm = new DisplayMetrics( );
                display.getMetrics(dm);
                screenHeight = dm.heightPixels;
            }
        }
        return screenHeight;
    }

    protected Activity getActivity() {
        return this;
    }

    private void showResOption()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Link_Live_Res);
        CharSequence[] choicesStr = {"4K","4KUHD", "2.7K", "1080P", "720P", "WVGA"};
        CharSequence[] choicesX3Str = {"1080P", "720P"};
        CharSequence[] choicesLiveStr = {"1080P", "720P"};

        int mCurSelection = mCamStatus.getmVideoSetting().getVideo_res();
        if(mCamStatus.getmCameraSettingNew().getHd_record()==1)
        {
            mCurSelection = mCamStatus.getmVideoSetting().getVideo_res();
            if(mCamStatus.getModelName().equals(CommonDefine.X3)||mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC))
            {//X3
                int resVal  = mCamStatus.getmStreamSetting().getStream_res();
                if(resVal == 3)
                {//1080P
                    mCurSelection = 0;
                }
                else
                {//720P
                    mCurSelection = 1;
                }

                builder.setSingleChoiceItems(choicesX3Str, mCurSelection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int res = 0;
                        //需要先转译成分辨率
                        if(whichButton==0)
                            res = 3;
                        else
                            res = 4;
                        mCamStatus.getmVideoSetting().setVideo_res(res);
                        switchRes();
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
            else
            {//XL Pro/4K+
                builder.setSingleChoiceItems(choicesStr, mCurSelection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mCamStatus.getmVideoSetting().setVideo_res(whichButton);
                        switchRes();
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        }
        else
        {
            int resVal  = mCamStatus.getmStreamSetting().getStream_res();
            //需要先转译成索引
            if(mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC)) {
                 /*
                enum VIDEO_RES {
                 VRES_1080P,  //0
                 VRES_960P,   //1
                 VRES_720P,
                 VRES_WVGA,
                 VIDEO_RES_OPTION_NUM
                };
                */
                if(resVal == 0)
                {//1080P
                    mCurSelection = 0;
                }
                else
                {//720P
                    mCurSelection = 1;
                }
            }
            else
            {
                if(resVal == 3)
                {//1080P
                    mCurSelection = 0;
                }
                else
                {//720P
                    mCurSelection = 1;
                }
            }
            builder.setSingleChoiceItems(choicesLiveStr, mCurSelection, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int res = 0;
                    //需要先转译成分辨率
                    if(mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC))
                    {
                             /*
                            enum VIDEO_RES {
                             VRES_1080P,  //0
                             VRES_960P,   //1
                             VRES_720P,
                             VRES_WVGA,
                             VIDEO_RES_OPTION_NUM
                            };
                            */
                        if (whichButton == 0)
                            res = 0;
                        else
                            res = 2;
                    }
                    else {
                        if (whichButton == 0)
                            res = 3;
                        else
                            res = 4;
                    }
                    mCamStatus.getmStreamSetting().setStream_res(res);
                    switchRes();
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }

    }

    private void showFpsOption()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.Link_Live_Fps);
//        String[]  choicesStr4K =  new String[]{"24", "25"};
//        String[] choicesStr4KUHD = new String[]{"24", "25", "30"};
//        String[] choicesStr27K = new String[]{"24", "25", "30", "50"};
//        String[] choicesStr1080P = new String[]{"24", "25", "30", "50", "60", "100", "120"};
//        String[] choicesStr720P = new String[]{"25", "30", "50", "60", "200", "240"};
//        String[] choicesStrWVGA = new String[]{"25", "30"};
//
//        String[] choicesStr = null;
        int fpsValue = mCamStatus.getmVideoSetting().getVideo_framerate();
        if(mCamStatus.getmCameraSettingNew().getHd_record()==1) {
            //录影模式
            if (mCamStatus.getModelName().equals(CommonDefine.DriftGhostXLPro) || mCamStatus.getModelName().equals(CommonDefine.DriftGhost4KPlus)) {
                switch (mCamStatus.getmVideoSetting().getVideo_res()) {
                    case 0:
                        choicesStr = choicesStr4K;
                        break;
                    case 1:
                        choicesStr = choicesStr4KUHD;
                        break;
                    case 2:
                        choicesStr = choicesStr27K;
                        break;
                    case 3:
                        choicesStr = choicesStr1080P;
                        break;
                    case 4:
                        choicesStr = choicesStr720P;
                        break;
                    case 5:
                        choicesStr = choicesStrWVGA;
                        break;
                }
            }
            fpsValue = mCamStatus.getmVideoSetting().getVideo_framerate();
        }
        else
        {//直播模式
            if (mCamStatus.getModelName().equals(CommonDefine.X3) || mCamStatus.getModelName().equals(CommonDefine.DriftGhostDC)) {
                if(mCamStatus.getmStreamSetting().getStream_res()==2)
                    choicesStr = choicesX3Str720P;
                else
                    choicesStr = choicesX3Str1080P;
            }
            else if (mCamStatus.getModelName().equals(CommonDefine.DriftGhostXLPro) || mCamStatus.getModelName().equals(CommonDefine.DriftGhost4KPlus)) {
                if(mCamStatus.getmStreamSetting().getStream_res()==3)
                    choicesStr = choicesA9LiveStr1080P;
                else
                    choicesStr = choicesLiveStr720P;
                }
            else
            {
                if(mCamStatus.getmStreamSetting().getStream_res()==3)
                    choicesStr = choicesA12LiveStr1080P;
                else
                    choicesStr = choicesLiveStr720P;
            }
            fpsValue = mCamStatus.getmStreamSetting().getStream_framerate();
        }

//        int fps = convertFpsToIndex(fpsValue);
        int mCurSelection = getIndexOfStrArray(choicesStr, fpsValue+"");
        if(mCurSelection < 0)
            mCurSelection = 0;
        builder.setSingleChoiceItems(choicesStr, mCurSelection, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                mCurSelection = whichButton;
//                itemData.cur_position = mCurSelection;
//
//                onSetSomething(itemData);
//                if (itemData.callback != null)
//                    itemData.callback.onSetSomething(itemData);
//                dialog.dismiss();
                String fpsValueStr = choicesStr[whichButton];
                int fpsValue = Integer.parseInt(fpsValueStr);
                if(mCamStatus.getmCameraSettingNew().getHd_record()==1)
                    mCamStatus.getmVideoSetting().setVideo_framerate(fpsValue);
                else
                    mCamStatus.getmStreamSetting().setStream_framerate(fpsValue);
                switchFps();
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private int getIndexOfStrArray(String[] Array, String value)
    {
        int index = -1;
        for(int i=0; i<Array.length; i++)
        {
            if(value.equals(Array[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    /* ====================== PlayFragment ====================== */

    /*
     * state：1、连接中，2、连接错误，3、连接线程退出
     * */
    public void onEvent(PlayFragment playFragment, int state, int err, String msg) {
//        mBinding.msgTxt.append(String.format("[%s]\t%s\t\n",
//                new SimpleDateFormat("HH:mm:ss").format(new Date()),
//                msg));
    }

    public void onRecordState(int status) {
//        ImageView mPlayAudio = mBinding.liveVideoBarRecord;
//        mPlayAudio.setImageState(status == 1 ? new int[]{android.R.attr.state_checked} : new int[]{}, true);
//        mPlayAudio.removeCallbacks(mResetRecordStateRunnable);
    }

    // 是否横屏
    private boolean isLandscape() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == ORIENTATION_LANDSCAPE;
    }

    // 横屏
    private void landscape() {
//        mBinding.enterFullscreen.setSelected(true);

//        LinearLayout container = mBinding.playerContainer;

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setNavVisibility(false);

        // 横屏情况下,播放窗口横着排开
//        container.setOrientation(LinearLayout.HORIZONTAL);
//        mBinding.renderFl.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//        mBinding.renderFl.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//        mBinding.renderHolder.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//        mBinding.renderHolder.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//        mRenderFragment.enterFullscreen();
    }

    // 竖屏
    private void vertical() {
//        mBinding.enterFullscreen.setSelected(false);
//
//        LinearLayout container = mBinding.playerContainer;
//
//        // 竖屏,取消全屏状态
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setNavVisibility(true);
//
//        // 竖屏情况下,播放窗口竖着排开
//        container.setOrientation(LinearLayout.VERTICAL);
//        mBinding.renderFl.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.render_wnd_height);
//        mBinding.renderFl.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//        mBinding.renderHolder.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//        mBinding.renderHolder.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//        mRenderFragment.quiteFullscreen();
    }

    /* ====================== 播放控制 ====================== */

    private void onVideoDisplayed() {
//        mBinding.liveVideoBarTakePicture.setEnabled(true);
//        mBinding.liveVideoBarRecord.setEnabled(true);
//        mBinding.msgTxt.append(String.format("[%s]\t%s\n",new SimpleDateFormat("HH:mm:ss").format(new Date()),"播放中"));
    }

    private void onPlayStart() {
//        boolean enable = mRenderFragment.isAudioEnable();
//        mBinding.liveVideoBarEnableAudio.setImageState(enable ? new int[]{android.R.attr.state_pressed} : new int[]{}, true);
//        mBinding.liveVideoBarEnableAudio.setEnabled(true);
//        mHandler.postDelayed(mTimerRunnable, 1000);
//
//        mBinding.liveVideoBarTakePicture.setEnabled(false);
//        mBinding.liveVideoBarRecord.setEnabled(false);
    }

    private void onPlayStop() {
//        mBinding.liveVideoBarEnableAudio.setEnabled(false);
//        mHandler.removeCallbacks(mTimerRunnable);
    }

    /* ====================== 按钮事件 ====================== */

    // 打开文件
    public void onOpenFileDirectory(View view) {
//        Intent i = new Intent(this, MediaFilesActivity.class);
//        i.putExtra("play_url", url);
//        startActivity(i);
    }

    // 开启/关闭音频
    public void onEnableOrDisablePlayAudio(View view) {
        boolean enable = mRenderFragment.toggleAudioEnable();
        ImageView mPlayAudio = (ImageView) view;
        mPlayAudio.setImageState(enable ? new int[]{android.R.attr.state_pressed} : new int[]{}, true);
    }

    // 截屏
    public void onTakePicture(View view) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mRenderFragment.takePicture(FileUtil.getPictureName(rtspUrl).getPath());

//            if (mSoundPool != null) {
//                mSoundPool.play(mTalkPictureSound, mAudioVolumn, mAudioVolumn, 1, 0, 1.0f);
//            }
        } else {
            requestWriteStorage(true);
        }
    }

    // 开启/关闭录像
    public void LocalRecordOrStop() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (mRenderFragment != null) {
                boolean recording = mRenderFragment.onRecordOrStop();

//                ImageView mPlayAudio = (ImageView) view;
//                mPlayAudio.setImageState(recording ? new int[]{android.R.attr.state_checked} : new int[]{}, true);
                if(recording)
                {
                    ibTakephoto.setImageResource(R.drawable.link_stoprecord_btn);
                    setResBtnStatus(false);
                    setFpsBtnStatus(false);
//                    mPlayAudio.postDelayed(mResetRecordStateRunnable, 200);
                }
                else
                {
                    ibTakephoto.setImageResource(R.drawable.link_startrecord_btn);
                    setResBtnStatus(true);
                    setFpsBtnStatus(true);
                }
            }
        } else {
            requestWriteStorage(false);
        }
    }

    public void onTakePictureThumbClicked(View view) {
        String path = (String) view.getTag();
//        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "gallery_image_view");
//        Intent intent = new Intent(this, ImageActivity.class);
//        intent.putExtra("extra-uri", Uri.fromFile(new File(path)));
////        ActivityCompat.startActivity(this, intent, compat.toBundle());
//        startActivity(intent);
//        getSupportFragmentManager().beginTransaction().add(android.R.id.content, ImageFragment.newInstance(Uri.fromFile(new File(path)))).addToBackStack(null).commit();
    }

    // 全屏
    public void onFullscreen(View view) {
        ImageButton btn = (ImageButton) view;
        btn.setSelected(!btn.isSelected());

        if (btn.isSelected()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
    }

    /* ====================== OnDoubleTapListener ====================== */

    @Override
    public void onDoubleTab(PlayFragment f) {

    }

    @Override
    public void onSingleTab(PlayFragment f) {
//        if (mBinding.liveVideoBar.getVisibility() == View.GONE) {
//            mBinding.liveVideoBar.setVisibility(View.VISIBLE);
//        } else {
//            mBinding.liveVideoBar.setVisibility(View.GONE);
//        }
    }

    /* ====================== private method ====================== */

    private void requestWriteStorage(final boolean toTakePicture) {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            new AlertDialog.Builder(this).setMessage(toTakePicture ? "EasyPlayer需要使用写文件权限来抓拍" : "EasyPlayer需要使用写文件权限来录像").setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(LinkCamDetailActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE + (toTakePicture ? 0 : 1));
                }
            }).show();
        } else {
            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE + (toTakePicture ? 0 : 1));

            // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }


}