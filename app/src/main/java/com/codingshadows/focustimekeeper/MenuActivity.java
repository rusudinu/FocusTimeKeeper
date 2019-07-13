package com.codingshadows.focustimekeeper;

import android.content.Intent;
import android.graphics.Point;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();

        wmlp.gravity = Gravity.TOP | Gravity.LEFT;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        getWindow().setElevation(10);
        getWindow().setLayout((int) (width * .8), height);


        TextView profileTV = findViewById(R.id.profileTextView);
        profileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });

        ImageView menuButton = findViewById(R.id.menuButton2);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        TextView shopBT = findViewById(R.id.shopTextView);
        shopBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShop();
            }
        });

        TextView settingsBT = findViewById(R.id.settingsTextView);
        settingsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });

        TextView stayFocusedBT = findViewById(R.id.stayFocusedTextView);
        stayFocusedBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStayFocused();
            }
        });

        TextView contactBT = findViewById(R.id.contactTextView);
        contactBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContact();
            }
        });

        TextView friendTV = findViewById(R.id.profileFriendTextView);
        friendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendProfile();
            }
        });

        TextView makeProgramTV = findViewById(R.id.newWeekDayTextView);
        makeProgramTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showMakeProgram();
            }
        });

        TextView dailyPerformanceTV = findViewById(R.id.dailyPerformance);
        dailyPerformanceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPerformance();
            }
        });

        TextView tutorialTV = findViewById(R.id.tutorial);
        tutorialTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTutorial();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.exit);
    }

    private void showTutorial(){
        Intent intent = new Intent(MenuActivity.this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }

    private void showPerformance()
    {
        Intent intent = new Intent(MenuActivity.this, PerformanceActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMakeProgram()
    {
        Intent intent = new Intent(MenuActivity.this, MakeProgramActivity.class);
        startActivity(intent);
        finish();
    }

    private void showContact() {
        Intent intent = new Intent(MenuActivity.this, ContactActivity.class);
        startActivity(intent);
        finish();
    }

private void showProfile()
{
    Intent intent = new Intent(MenuActivity.this, UserProfileActivity.class);
    startActivity(intent);
    finish();
}

private void showFriendProfile()
{
    Intent intent = new Intent(MenuActivity.this, UserFriendProfileActivity.class);
    startActivity(intent);
    finish();
}


    private void showSettings() {
        Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void showStayFocused() {
        Intent intent = new Intent(MenuActivity.this, StayFocusedActivity.class);
        startActivity(intent);
        finish();
    }

    private void showShop()
    {
        Intent intent = new Intent(MenuActivity.this, ShopActivity.class);
        startActivity(intent);
        finish();
    }
}


