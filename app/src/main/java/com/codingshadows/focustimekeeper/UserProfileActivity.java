package com.codingshadows.focustimekeeper;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Objects;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        hideAll();
        getData();

    }

    private void getData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                //    focusCoins = Objects.requireNonNull(documentSnapshot.get("Focus coins")).toString();
                    showAll();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    private void hideAll() {
        hideVerifiedBadge();
        TextView usernameTV = findViewById(R.id.usernameTextView);
        usernameTV.setVisibility(View.INVISIBLE);
        TextView highestFocusTimeTV = findViewById(R.id.highestTimeTextView);
        highestFocusTimeTV.setVisibility(View.INVISIBLE);
        TextView badgeTV = findViewById(R.id.badgeTextView);
        badgeTV.setVisibility(View.INVISIBLE);
        TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.INVISIBLE);

    }

    private void showAll() {
        TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.VISIBLE);
        TextView usernameTV = findViewById(R.id.usernameTextView);
        usernameTV.setVisibility(View.VISIBLE);
        usernameTV.setText(username);
        showVerifiedBadge();
        showBadge();
        showHighestTime();
        showFocusPoints();
        //MAKE SURE IT'S THE LAST ONE
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
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
        new CountDownTimer(1, 1) { // todo add some 0's

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
