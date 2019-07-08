package com.codingshadows.focustimekeeper;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class StayFocusedActivity extends AppCompatActivity {

    private long secondsWhenStopped = 0;
    private long coinsGivenFor = 0;
    private boolean screenVisibile = true;
    private long highestTime = 0;
    private boolean chronometerRunning = false;
    String achievements = "";
    String badges = "";
    private int  notifID = 100;
    private Boolean doNotStartChr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stay_focused);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        createNotifChannel();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        final Chronometer simpleChronometer = findViewById(R.id.simpleChronometer);
        simpleChronometer.setVisibility(View.INVISIBLE);


        final TextView tv = findViewById(R.id.permissionSmallTextTextView);
        final Button bt1 = findViewById(R.id.acceptPermissionButton);
        final Button bt2 = findViewById(R.id.denyPermissionButton);

        tv.setVisibility(View.VISIBLE);
        bt1.setVisibility(View.VISIBLE);
        bt2.setVisibility(View.VISIBLE);

        screenVisibile = true;

        if (!isAccessGranted()) {
            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);


                        progressBar.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.INVISIBLE);
                        bt1.setVisibility(View.INVISIBLE);
                        bt2.setVisibility(View.INVISIBLE);
                        simpleChronometer.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                            progressBar.setVisibility(View.VISIBLE);
                            tv.setVisibility(View.INVISIBLE);
                            bt1.setVisibility(View.INVISIBLE);
                            bt2.setVisibility(View.INVISIBLE);
                            simpleChronometer.setVisibility(View.VISIBLE);
                        } catch (Exception ex) {
                            Toast.makeText(StayFocusedActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.VISIBLE);
                            tv.setVisibility(View.INVISIBLE);
                            bt1.setVisibility(View.INVISIBLE);
                            bt2.setVisibility(View.INVISIBLE);
                            simpleChronometer.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });

            bt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            tv.setVisibility(View.INVISIBLE);
            bt1.setVisibility(View.INVISIBLE);
            bt2.setVisibility(View.INVISIBLE);
            simpleChronometer.setVisibility(View.VISIBLE);
        }


     //prevent from rotating
        getHighestTime();



        getUserAchievements();


        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopTimer();
            }
        });

        ImageView menuBT = findViewById(R.id.focusedMenuImageView);
        menuBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operateMenu();
            }
        });



       // simpleChronometer.start();
        simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                animateCircle();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        screenVisibile = true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkApps();
        screenVisibile = false;
    }

    private void checkApps() {
        new CountDownTimer(100, 60) {

            public void onTick(long millisUntilFinished) {
                getUsage();
                checkForAchievements();
            }

            public void onFinish() {
                checkApps();
            }
        }.start();
    }


    private void operateMenu() {
        Intent intent = new Intent(StayFocusedActivity.this, MenuActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //region usage things
    private void getUsage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000 * 10000, time);
            if (appList != null && appList.size() == 0) {
                Log.d("Executed app", "######### NO APP FOUND ##########");
            }
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    // Toast.makeText(this, usageStats.getPackageName(), Toast.LENGTH_SHORT).show();
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    String currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    if (currentApp.contains("facebook")) {
                        stopTimerNoRemember();
                        showWhyFail("Ai deschis Facebook!");
                    } else if (currentApp.contains("chrome")) {
                        stopTimerNoRemember();
                        showWhyFail("Ai deschis Chrome!");
                    } else if (currentApp.contains("whatsapp")) {
                        stopTimerNoRemember();
                        showWhyFail("Ai deschis Whatsapp!");
                    } else if (currentApp.contains("telegram")) {
                        stopTimerNoRemember();
                        showWhyFail("Ai deschis Telegram!");
                    } else if (currentApp.contains("messenger")) {
                        Chronometer simpleChronometer = findViewById(R.id.simpleChronometer);
                        simpleChronometer.stop();
                        showWhyFail("Ai deschis Facebook Messenger!");
                    } else if (currentApp.contains("netflix")) {
                        stopTimerNoRemember();
                        showWhyFail("Ai deschis Netflix!");
                    } else if (currentApp.contains("reddit")) {
                        stopTimerNoRemember();
                        showWhyFail("Ai deschis Reddit!");
                    }
                }
            }
        }
    }
    //endregion


    private void showWhyFail(String reason) {
        TextView tv = findViewById(R.id.showFailTextView);
        String checkStr = tv.getText().toString();
        if (!checkStr.contains(reason)) {
            tv.append(reason + "\n");
            showNotificationInBar("Focus: Time Keeper - Cronometru oprit!", reason);
        }
    }

    private void startStopTimer() {
        if (chronometerRunning) {
            chronometerRunning = false;
            stopTimer();
        } else {
            chronometerRunning = true;
            restartTimer();
        }
    }


    private void stopTimerNoRemember() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        Chronometer simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.stop();
        secondsWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
        addCoins();
        if(Math.abs(secondsWhenStopped) >= getHighestTime())
        {
            updateHighestTime(Math.abs(secondsWhenStopped));
            showNotificationInBar("Focus: Time Keeper","Ai atins un nou maxim!");
        }
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        secondsWhenStopped = 0;
        doNotStartChr = true;
    }



    //region coins
    long newAmount = 0;
    long coins = 0;
    private void calcCoins()
    {
        long amountTime = Math.abs(secondsWhenStopped) - Math.abs(coinsGivenFor);
        coinsGivenFor = Math.abs(secondsWhenStopped);
        coins = (int) amountTime / 5000;
        showFocusPoints(coins);
    }

    private void addCoins()
    {
        calcCoins();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(getUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                newAmount = Long.valueOf(documentSnapshot.get("Focus points").toString()) + coins;
                pushCoins(newAmount);
            }
        });

    }

    private void pushCoins(long coinz)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(getUID()).update("Focus points", coinz).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void documentReference) {
            }
        });
    }
//endregion



    private void stopTimer() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        Chronometer simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.stop();
        secondsWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
        addCoins();
        if(Math.abs(secondsWhenStopped) >= getHighestTime())
        {
            updateHighestTime(Math.abs(secondsWhenStopped));
            showNotificationInBar("Focus: Time Keeper","Ai atins un nou maxim!");
        }
    }

    private void restartTimer() {
        if(!doNotStartChr)
        {
            hideFocusPoints();
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
            Chronometer simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
            simpleChronometer.setBase(SystemClock.elapsedRealtime() + secondsWhenStopped);
            simpleChronometer.start();
            secondsWhenStopped = 0;
        }
        else
        {
            showNotification("Trebuie sa repornesti aceasta fereastra daca doresti sa pornesti cronometrul din nou!");
        }
    }

    int animateVal = 0;

    private void animateCircle() {
        if (chronometerRunning) {
            Chronometer simpleChronometer = findViewById(R.id.simpleChronometer);
            long elapsedMS = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
            long elapsedS = elapsedMS / 1000;
            long elapsedM = 0;

            while (elapsedS >= 60) {
                elapsedM = elapsedM + 1;
                elapsedS = elapsedS - 60;
            }
            int elapsedSeconds = (int) elapsedS;
            int elapsedH = 0;
            while (elapsedM >= 60) {
                elapsedM = elapsedM - 60;
                elapsedH++;
            }

            //check for achievements
            checkForAchievements();

            if (animateVal == 60) {
                animateVal = 0;
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setProgress(elapsedSeconds);
                animateVal++;
            } else {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setProgress(elapsedSeconds);
                animateVal++;
            }
        }
    }


    private void checkForAchievements() {

        if (chronometerRunning) {
            Chronometer simpleChronometer = findViewById(R.id.simpleChronometer);
            long elapsedMS = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
            long elapsedS = elapsedMS / 1000;
            long elapsedM = 0;

            while (elapsedS >= 60) {
                elapsedM = elapsedM + 1;
                elapsedS = elapsedS - 60;
            }
            int elapsedSeconds = (int) elapsedS;
            int elapsedH = 0;
            while (elapsedM >= 60) {
                elapsedM = elapsedM - 60;
                elapsedH++;
            }

            if (elapsedM >= 1) {
                if (!achievements.contains("Practitioner")) {
                    showNotification("Felicitari! Ai un achievement nou: Practitioner");
                    achievements = achievements + "Practitioner" + ",";
                    addAchievementsBadges("Practitioner");
                    showNotificationInBar("Focus: Time Keeper", "Felicitari! Ai primit un nou achievement: Practitioner.");
                }
            }

            if (elapsedM >= 30) {
                if (!achievements.contains("Dedicated")) {
                    showNotification("Felicitari! Ai un achievement nou: Dedicated");
                    achievements = achievements + "Dedicated" + ",";
                    addAchievementsBadges("Dedicated");
                    showNotificationInBar("Focus: Time Keeper", "Felicitari! Ai primit un nou achievement: Dedicated.");
                }
            }

            if (elapsedH >= 1) {
                if (!achievements.contains("Zealous")) {
                    showNotification("Felicitari! Ai un achievement nou: Zealous");
                    achievements = achievements + "Zealous" + ",";
                    addAchievementsBadges("Zealous");
                    showNotificationInBar("Focus: Time Keeper", "Felicitari! Ai primit un nou achievement: Zealous.");
                }
            }

            if (elapsedH >= 2) {
                if (!achievements.contains("Savant of Neutrality")) {
                    showNotification("Felicitari! Ai un achievement nou: Savant of Neutrality");
                    achievements = achievements + "Savant of Neutrality" + ",";
                    badges = badges + "Savant of Neutrality" + ",";
                    addAchievementsBadges("Savant of Neutrality");
                    showNotificationInBar("Focus: Time Keeper", "Felicitari! Ai primit un nou achievement: Savant of Neutrality.");
                }
            }
        }
    }

    private void showFocusPoints(long fP)
    {
        TextView tv = findViewById(R.id.showFocusPointsTextView);
        tv.setText("Ai primit " + fP + " Focus Points!");
    }

    private void hideFocusPoints()
    {
        TextView tv = findViewById(R.id.showFocusPointsTextView);
        tv.setText("");
    }




//every 10 minutes add 5 coins
    private void showNotification(String notifText)
    {
        Intent intent = new Intent(StayFocusedActivity.this, BottomNotificaionBar.class);
        intent.putExtra("message", notifText);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);

    }

    private void getUserAchievements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("Users").document(getUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    achievements = documentSnapshot.get("Achievements").toString();
                    badges = documentSnapshot.get("Badges").toString();
                    if(achievements.contains("zero")) achievements.replace("zero", "");
                    if(badges.contains("zero")) badges.replace("zero", "");
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addAchievementsBadges(String achievName) {
        if(achievements.contains("zero")) achievements.replace("zero", "");
        if(badges.contains("zero")) badges.replace("zero", "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(getUID()).update("Achievements", achievements).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void documentReference) {
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        if(achievName.equals("Savant of Neutrality"))
        {
            db.collection("Users").document(getUID()).update("Badges", badges).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void documentReference) {
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
            db.collection("Users").document(getUID()).update("Current badge", "Savant of Neutrality").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void documentReference) {
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }


    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }



    private long getHighestTime()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("Users").document(getUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                   highestTime = Long.valueOf(documentSnapshot.get("Highest time").toString());
                }
            });
        } catch (Exception e) {

        }
        return highestTime;
    }

    private void updateHighestTime(long newHigh)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(getUID()).update("Highest time", newHigh).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void showNotificationInBar(String notificationTitle, String notificationText)
    {
        Notification.Builder nb = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(16777215)
                .setVisibility(Notification.VISIBILITY_PUBLIC);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            nb.setChannelId("2321");
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notifID, nb.build());
        notifID++;
    }

    private void createNotifChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  "Focus: Time Keeper notifications";
            String description = "no description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("2321", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
