package com.drift.camcontroldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHomeActivity();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, LinkHomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.trans_in_right, R.anim.stay);
    }
}