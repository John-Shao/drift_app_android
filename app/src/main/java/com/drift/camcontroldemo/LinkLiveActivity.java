package com.drift.camcontroldemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drift.foreamlib.local.ctrl.LocalController;
import com.drift.foreamlib.local.ctrl.LocalListener;

public class LinkLiveActivity extends AppCompatActivity {
    private static String TAG = "LinkLiveActivity";

    private RelativeLayout rlNav;
    private RelativeLayout rlBack;
    private ImageView ivBack;
    private RelativeLayout rlConfirm;

    private String liveUrl;
    private LocalController localController;
    private String camIP;
    private int streamRes;
    private int streamBitrate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_live);

        Intent i = getIntent();

        camIP = i.getStringExtra("camIP");
        streamRes = i.getIntExtra("streamRes",0);
        streamBitrate = i.getIntExtra("streamBitrate",0);
        localController = new LocalController();

        rlNav = (RelativeLayout) findViewById(R.id.rl_nav);
        rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivBack = (ImageView) findViewById(R.id.iv_back);
        rlConfirm = (RelativeLayout) findViewById(R.id.rl_confirm);
        rlConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) LinkLiveActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getEtLiveUrlValue().getWindowToken( ), 0);
                liveUrl = getEtLiveUrlValue().getText().toString();
                localController.startPushStreamWithURL(camIP, liveUrl, ""+streamRes, "" + (streamBitrate/8), new LocalListener.OnCommonResListener() {
                    @Override
                    public void onCommonRes(boolean success) {
                        Log.e(TAG, "kc test: bSuccess is" + success);
                        Toast.makeText(LinkLiveActivity.this, R.string.link_send_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LinkLiveActivity.this, LinkHomeActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        InputMethodManager imm = (InputMethodManager) LinkLiveActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getEtLiveUrlValue().getWindowToken( ), 0);
        super.onPause();
    }

    private EditText getEtLiveUrlValue(){
        return (EditText) findViewById(R.id.et_live_url_value);
    }
}