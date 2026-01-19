package com.drift.camcontroldemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drift.foreamlib.local.ctrl.LocalController;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LinkMeetingActivity extends AppCompatActivity {
    private static final String TAG = "LinkMeetingActivity";
    private static final String JOIN_MEETING_URL = "http://app.jusiai.com/meeting/api/v1/join";
    private static final String LEAVE_MEETING_URL = "http://app.jusiai.com/meeting/api/v1/leave";

    private EditText etMeetingId;
    private EditText etMeetingPassword;
    private Button btnMeetingAction;
    private ImageView ivBack;

    private boolean isInMeeting = false;
    private String deviceIpAddress;
    private String currentMeetingId;
    private LocalController localController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_meeting);

        deviceIpAddress = getIntent().getStringExtra("device_ip");
        if (TextUtils.isEmpty(deviceIpAddress)) {
            deviceIpAddress = "192.168.42.1";
        }

        localController = new LocalController();

        initViews();
        setupListeners();
    }

    private void initViews() {
        etMeetingId = findViewById(R.id.et_meeting_id);
        etMeetingPassword = findViewById(R.id.et_meeting_password);
        btnMeetingAction = findViewById(R.id.btn_meeting_action);
        ivBack = findViewById(R.id.iv_back);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnMeetingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInMeeting) {
                    leaveMeeting();
                } else {
                    joinMeeting();
                }
            }
        });
    }

    private void joinMeeting() {
        final String meetingId = etMeetingId.getText().toString().trim();
        final String meetingPassword = etMeetingPassword.getText().toString().trim();

        if (TextUtils.isEmpty(meetingId)) {
            Toast.makeText(this, R.string.meeting_id_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(meetingPassword)) {
            Toast.makeText(this, R.string.meeting_password_hint, Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(JOIN_MEETING_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("meeting_id", meetingId);
                    jsonParam.put("meeting_password", meetingPassword);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        br.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        int code = jsonResponse.optInt("code", -1);
                        String message = jsonResponse.optString("message", "");
                        final String rtmpUrl = jsonResponse.optString("rtmp_url", "");
                        final String rtspUrl = jsonResponse.optString("rtsp_url", "");

                        Log.d(TAG, "Join meeting response: code=" + code + ", message=" + message);

                        if (code == 0 || code == 200) {
                            currentMeetingId = meetingId;

                            localController.startPushStreamWithURL(deviceIpAddress, rtmpUrl, "1080", "4000000", new LocalController.OnCommonResListener() {
                                @Override
                                public void onCommonRes(boolean success) {
                                    Log.d(TAG, "Start push stream result: " + success);
                                }
                            });

                            startDeviceRtsp(rtspUrl);

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    isInMeeting = true;
                                    updateMeetingStatus();
                                    Toast.makeText(LinkMeetingActivity.this, R.string.join_meeting_success, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            final String errorMsg = message;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LinkMeetingActivity.this,
                                        getString(R.string.join_meeting_failed) + ": " + errorMsg,
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LinkMeetingActivity.this, R.string.join_meeting_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Join meeting error", e);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LinkMeetingActivity.this, R.string.join_meeting_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void leaveMeeting() {
        if (TextUtils.isEmpty(currentMeetingId)) {
            Toast.makeText(this, R.string.leave_meeting_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(LEAVE_MEETING_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("meeting_id", currentMeetingId);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        br.close();

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        int code = jsonResponse.optInt("code", -1);
                        String message = jsonResponse.optString("message", "");

                        Log.d(TAG, "Leave meeting response: code=" + code + ", message=" + message);

                        if (code == 0 || code == 200) {
                            stopDeviceRtmp();
                            stopDeviceRtsp();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    isInMeeting = false;
                                    currentMeetingId = null;
                                    updateMeetingStatus();
                                    Toast.makeText(LinkMeetingActivity.this, R.string.leave_meeting_success, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            final String errorMsg = message;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LinkMeetingActivity.this,
                                        getString(R.string.leave_meeting_failed) + ": " + errorMsg,
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LinkMeetingActivity.this, R.string.leave_meeting_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Leave meeting error", e);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LinkMeetingActivity.this, R.string.leave_meeting_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void startDeviceRtsp(final String rtspUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "http://" + deviceIpAddress + "/cgi-bin/foream_remote_control?start_rtsp=" + rtspUrl;
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Start device RTSP response code: " + responseCode);

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Start device RTSP error", e);
                }
            }
        }).start();
    }

    private void stopDeviceRtmp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "http://" + deviceIpAddress + "/cgi-bin/foream_remote_control?stop_rtmp";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Stop device RTMP response code: " + responseCode);

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Stop device RTMP error", e);
                }
            }
        }).start();
    }

    private void stopDeviceRtsp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = "http://" + deviceIpAddress + "/cgi-bin/foream_remote_control?stop_rtsp";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Stop device RTSP response code: " + responseCode);

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Stop device RTSP error", e);
                }
            }
        }).start();
    }

    private void updateMeetingStatus() {
        if (isInMeeting) {
            etMeetingId.setEnabled(false);
            etMeetingPassword.setEnabled(false);
            btnMeetingAction.setText(R.string.leave_meeting);
        } else {
            etMeetingId.setEnabled(true);
            etMeetingPassword.setEnabled(true);
            btnMeetingAction.setText(R.string.join_meeting);
        }
    }
}
