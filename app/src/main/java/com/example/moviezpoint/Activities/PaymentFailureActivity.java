package com.example.moviezpoint.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.moviezpoint.R;

public class PaymentFailureActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failure);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
