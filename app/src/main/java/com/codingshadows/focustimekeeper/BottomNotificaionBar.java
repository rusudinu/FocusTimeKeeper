package com.codingshadows.focustimekeeper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BottomNotificaionBar extends AppCompatActivity {

    private int notifDurationMS = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_notificaion_bar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;



        getWindow().setElevation(10);
        getWindow().setLayout(width, (int)(height*.1 + 5));



        TextView tv = findViewById(R.id.textView2);
        tv.setText(message);

        new CountDownTimer(notifDurationMS, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                finish();
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
            }
        }.start();
    }
}
