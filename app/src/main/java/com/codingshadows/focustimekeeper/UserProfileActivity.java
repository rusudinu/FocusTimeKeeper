package com.codingshadows.focustimekeeper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    Boolean verified = false;
    String username = "";
    String achievements = "";
    String badges = "";
    String currentBadge = "";
    int focusPoints = 0;
    String charactersOwned = "";
    long highestFocusTime = 0;
    String focusCoins = "";
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        if(intent.getStringExtra("ID") != null)
        {
            uid = intent.getStringExtra("ID");
        }
        hideAll();
        getData();

    }

    private void getData() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("Users").document(getUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    verified = Boolean.valueOf(documentSnapshot.get("Verified").toString());
                    username = documentSnapshot.get("Username").toString();
                    achievements = documentSnapshot.get("Achievements").toString();
                    badges = documentSnapshot.get("Badges").toString();
                    focusPoints = Integer.valueOf(documentSnapshot.get("Focus points").toString());
                    charactersOwned = documentSnapshot.get("Characters owned").toString();
                    highestFocusTime = Long.valueOf(documentSnapshot.get("Highest time").toString());
                    currentBadge = documentSnapshot.get("Current badge").toString();
                    if(documentSnapshot.get("Focus coins") != null)
                    {
                        focusCoins = documentSnapshot.get("Focus coins").toString();
                    }
                    else
                    {
                        db.collection("Users").document(getUID()).update("Focus coins","");
                    }
                    showAll();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString() + "  " + uid, Toast.LENGTH_LONG).show();
            Log.d("error", e.toString() + "  " + uid);
        }

    }

    private void displayFocusCoins()
    {
        if(focusCoins.contains("Archer coin"))
        {
            ImageView imv = findViewById(R.id.focusCoinImageView);
            imv.setVisibility(View.VISIBLE);
        }
    }



    private void hideAll() {
        hideVerifiedBadge();
        TextView usernameTV = findViewById(R.id.titleTextView);
        usernameTV.setVisibility(View.INVISIBLE);
        TextView highestFocusTimeTV = findViewById(R.id.highestTimeTextView);
        highestFocusTimeTV.setVisibility(View.INVISIBLE);
        TextView badgeTV = findViewById(R.id.badgeTextView);
        badgeTV.setVisibility(View.INVISIBLE);
        TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.INVISIBLE);
        ImageView imv = findViewById(R.id.focusCoinImageView);
        imv.setVisibility(View.INVISIBLE);

    }

    private void showAll() {
        TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.VISIBLE);
        TextView usernameTV = findViewById(R.id.titleTextView);
        usernameTV.setVisibility(View.VISIBLE);
        usernameTV.setText(username);
        showVerifiedBadge();
        showBadge();
        showHighestTime();
        showFocusPoints();
        displayFocusCoins();
    }

    private void showHighestTime() {
        long highestFocusTimeBK = highestFocusTime;
        long hours = 0;
        long minutes = 0;
        long seconds = highestFocusTimeBK / 1000;

        while (seconds >= 60) {
            seconds = seconds - 60;
            minutes = minutes + 1;
        }

        while (minutes >= 60) {
            minutes = minutes - 60;
            hours = hours + 1;
        }

        String hoursS = "";
        String minutesS = "";
        String secondsS = "";

        if(hours < 10)
        {
            hoursS = "0" + hours;
        }
        else hoursS = "" + hours;

        if(minutes < 10)
        {
            minutesS = "0" + minutes;
        }
        else minutesS = "" + minutes;

        if(seconds < 10)
        {
            secondsS = "0" + seconds;
        }
        else secondsS = "" + seconds;

        String showSTR = "Timp maxim de concentrare: " + hoursS + ":" + minutesS + ":" + secondsS;
        TextView highestFocusTimeTV = findViewById(R.id.highestTimeTextView);
        highestFocusTimeTV.setVisibility(View.VISIBLE);
        highestFocusTimeTV.setText(showSTR);

    }


    private String getUID() {
        if(!uid.equals("")) return uid;
        else
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            return user.getUid();
        }
    }

    //region badges stuff (display)
    private void hideVerifiedBadge() {
        ImageView verifiedIMV = findViewById(R.id.verifiedImageView);
        verifiedIMV.setVisibility(View.INVISIBLE);
    }

    private void showBadge() {
        if (badges.length() >= 3) {
            if (!currentBadge.equals("")) {
                TextView badgeTV = findViewById(R.id.badgeTextView);
                badgeTV.setVisibility(View.VISIBLE);
                badgeTV.setText(currentBadge);
            }
        }
    }

    //TODO SPAWN ACHIEVEMENTS THERE
    private void showVerifiedBadge() {
        if (verified) {
            ImageView verifiedIMV = findViewById(R.id.verifiedImageView);
            verifiedIMV.setVisibility(View.VISIBLE);
        }
    }
    //endregion

    private void showFocusPoints() {
        TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.VISIBLE);
        focusPointsTV.setText("0");
        animateFocusPoints();
    }

    private int currentFocusPoints = 0;

    private void animateFocusPoints() {
        new CountDownTimer(1, 1) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (currentFocusPoints == focusPoints) {
                } else {
                    if (focusPoints - currentFocusPoints > 100) {
                        currentFocusPoints = currentFocusPoints + 100;
                    } else currentFocusPoints++;
                    TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
                    focusPointsTV.setText("" + currentFocusPoints);
                    animateFocusPoints();
                }
            }
        }.start();
    }

}
