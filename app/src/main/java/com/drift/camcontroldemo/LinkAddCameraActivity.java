package com.drift.camcontroldemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewTreeObserver;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import com.drift.app.ForeamApp;
import com.drift.foreamlib.api.CamInfo;
import com.drift.foreamlib.api.ForeamCamCtrl;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LinkAddCameraActivity extends AppCompatActivity {

    private static String TAG = "LinkAddCameraActivity";

    private RelativeLayout rlNav;
    private RelativeLayout rlBack;
    private ImageView ivBack;
    private TextView tvTipsTitle;
    private TextView tvTips;
    private TextView tvNoteTitle;
    private TextView tvNotes;
    private RelativeLayout rlInput;
    private TextView tvWifiName;
    private TextView tvWifiPassword;
    private ImageView ivQrcodeImage;
    private RelativeLayout rlConfirm;

    private ForeamCamCtrl mForeamCamCtrl;
    private String qrCodeString;
    private String ssid;
    private String pwd;

    private ArrayList<String> camsOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_add_camera);
        camsOnline = (ArrayList<String>) getIntent().getSerializableExtra("camsOnline");

        rlNav = (RelativeLayout) findViewById(R.id.rl_nav);
        rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTipsTitle = (TextView) findViewById(R.id.tv_tips_title);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        tvNoteTitle = (TextView) findViewById(R.id.tv_note_title);
        tvNotes = (TextView) findViewById(R.id.tv_notes);
        if(!ForeamApp.isInChinesEnvir())
        {
            tvNotes.setVisibility(View.INVISIBLE);
            tvNoteTitle.setVisibility(View.INVISIBLE);
        }
        rlInput = (RelativeLayout) findViewById(R.id.rl_input);
        tvWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        tvWifiPassword = (TextView) findViewById(R.id.tv_wifi_password);
        ivQrcodeImage = (ImageView) findViewById(R.id.iv_qrcode_image);
        ViewTreeObserver vto = ivQrcodeImage.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivQrcodeImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = ivQrcodeImage.getMeasuredWidth();
                ViewGroup.LayoutParams params = ivQrcodeImage.getLayoutParams();
                params.height = width; // 设置为正方形
                ivQrcodeImage.setLayoutParams(params);
            }
        });
        rlConfirm = (RelativeLayout) findViewById(R.id.rl_confirm);

        ssid = getConnectedSsid(this);
        //判断是否需要清掉双引号
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {

            ssid = ssid.substring(1, ssid.length() - 1);
        }
        getEtWifiName().setText(ssid);

        mForeamCamCtrl = ForeamCamCtrl.getInstance();

        rlConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) LinkAddCameraActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getEtWifiPassword().getWindowToken( ), 0);
                ssid = getEtWifiName().getText().toString();
                pwd = getEtWifiPassword().getText().toString();
                qrCodeString = mForeamCamCtrl.generateQRCode(ssid, pwd, null,"RTSP");
                Bitmap mBitmap = createQRCode(qrCodeString, 600, 600);
                if (mBitmap != null) {
                    ivQrcodeImage.setImageBitmap(mBitmap);
                }
            }
        });

        mForeamCamCtrl = ForeamCamCtrl.getInstance();
        mForeamCamCtrl.setOnReceiveUDPMsgListener(mOnReceiveBoardcastMsgListener);
//        mForeamCamCtrl.startReceive();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mForeamCamCtrl.stopReceive();
        mForeamCamCtrl.setOnReceiveUDPMsgListener(null);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        mForeamCamCtrl.startReceive();
        mForeamCamCtrl.setOnReceiveUDPMsgListener(mOnReceiveBoardcastMsgListener);
    }

    private EditText getEtWifiName(){
        return (EditText) findViewById(R.id.et_wifi_name);
    }

    private EditText getEtWifiPassword(){
        return (EditText) findViewById(R.id.et_wifi_password);
    }

    /**
     * 创建二维码
     *
     * @param content   content
     * @param widthPix  widthPix
     * @param heightPix heightPix
     * @return 二维码
     */
    public static Bitmap createQRCode(String content, int widthPix, int heightPix) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }
            // 配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                    heightPix, hints);
            int[] pixels = new int[widthPix * heightPix];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getConnectedSsid(Context context) {
        if (context == null)
            return null;
        if (Build.VERSION.SDK_INT < 26) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info == null || info.getSSID() == null || info.getSSID().length() == 0 || info.getSSID().equals("<unknown ssid>")) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                String wifiName = wifiInfo.getExtraInfo();
                if (wifiName == null) wifiName = "";
                return wifiName;
            }
            return info.getSSID().replace("\"", "");
        } else //only used the new way on android 8.0 or above.
            return getSSIDByNetWorkId(context);
    }

    public static String getSSIDByNetWorkId(Context context) {
        String ssid = "";
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null != mWifiManager) {
            WifiInfo info = mWifiManager.getConnectionInfo();
//            int networkId = info.getNetworkId();
//            List<WifiConfiguration> netConfList = mWifiManager.getConfiguredNetworks();
//            for (WifiConfiguration wificonf : netConfList) {
//                if (wificonf.networkId == networkId) {
//                    ssid = wificonf.SSID;
//                    break;
//                }
//            }
            //直接通过连接的info就可以获取到ssid
            if(info != null)
            {
                ssid = info.getSSID();
            }
        }
        if (ssid.contains("\"")) {
            ssid = ssid.replace("\"", "");
        }
        return ssid;
    }

    ForeamCamCtrl.OnReceiveUDPMsgListener mOnReceiveBoardcastMsgListener = new ForeamCamCtrl.OnReceiveUDPMsgListener() {
        public void camIsOnline(String serialNum, String msgValue, String camIP, String ownerId) {
            Log.d(TAG, "收到心跳包，序列号是" + serialNum + " " + msgValue +" " + camIP +" " + ownerId);
            //判断是否已经在相机组里,如果不在线,则添加进在线相机里
            if(!camsOnline.contains(camIP)){
                finish();
            }
        }

        public void camIsOffline(String serialNum) {

        }

        public void numberOfCamsOnline(ArrayMap<String, CamInfo> arrayList){

        }

    };
}