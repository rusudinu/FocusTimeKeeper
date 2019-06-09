package com.codingshadows.focustimekeeper;

import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class UserFriendProfileActivity extends AppCompatActivity {

    String searchEmail = "";

    Boolean verified = false;
    String username = "";
    String achievements = "";
    String badges = "";
    String currentBadge = "";
    int focusPoints = 0;
    String charactersOwned = "";
    long highestFocusTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friend_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        hideAll();


        final TextView searchBT = findViewById(R.id.searchTextView);
        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchET = findViewById(R.id.friendIDTextView);
                searchBT.setText("Te rugam asteapta ...");
                searchEmail = searchET.getText().toString();
                getEmailID();
            }
        });
    }

    private void getData(String docID) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(docID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    try
                    {
                        verified = Boolean.valueOf(Objects.requireNonNull(documentSnapshot.get("Verified")).toString());
                        username = Objects.requireNonNull(documentSnapshot.get("Username")).toString();
                        achievements = Objects.requireNonNull(documentSnapshot.get("Achievements")).toString();
                        badges = Objects.requireNonNull(documentSnapshot.get("Badges")).toString();
                        focusPoints = Integer.valueOf(Objects.requireNonNull(documentSnapshot.get("Focus points")).toString());
                        charactersOwned = Objects.requireNonNull(documentSnapshot.get("Characters owned")).toString();
                        highestFocusTime = Long.valueOf(Objects.requireNonNull(documentSnapshot.get("Highest time")).toString());
                        currentBadge = Objects.requireNonNull(documentSnapshot.get("Current badge")).toString();
                        showAll();
                    }catch (Exception e)
                    {
                        Toast.makeText(UserFriendProfileActivity.this, "Invalid ID!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

//todo the 0s

    private void getEmailID() {
        final String email = searchEmail;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int x = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        x++;
                        if(document.get("Email").toString().equals(email))
                        {
                            getData(document.getId());
                        }
                    }
                    if(x == 0) {
                        Toast.makeText(UserFriendProfileActivity.this, "Utilizatorul nu exista!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

    private void showAll()
    {
        EditText searchET = findViewById(R.id.friendIDTextView);
        searchET.setVisibility(View.INVISIBLE);
        TextView searchBT = findViewById(R.id.searchTextView);
        searchBT.setVisibility(View.INVISIBLE);
        TextView focusPointsTV = findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.VISIBLE);
        TextView usernameTV = findViewById(R.id.usernameTextView);
        usernameTV.setVisibility(View.VISIBLE);
        usernameTV.setText(username);
        showVerifiedBadge();
        showBadge();
        showHighestTime();
        showFocusPoints();//MAKE SURE IT'S THE LAST ONE
    }

    private void showHighestTime()
    {
        long highestFocusTimeBK = highestFocusTime;
        long hours = 0;
        long minutes = 0;
        long seconds = highestFocusTimeBK/1000;

        while(seconds >= 60)
        {
            seconds = seconds - 60;
            minutes = minutes + 1;
        }

        while(minutes >= 60)
        {
            minutes = minutes - 60;
            hours = hours + 1;
        }


        String showSTR = "" + hours + ":" + minutes + ":" +  seconds;
        TextView highestFocusTimeTV = findViewById(R.id.highestTimeTextView);
        highestFocusTimeTV.setVisibility(View.VISIBLE);
        highestFocusTimeTV.setText("Timp maxim de concentrare: " + showSTR);

    }



    //region badges stuff (display)
    private void hideVerifiedBadge()
    {
        ImageView verifiedIMV = findViewById(R.id.verifiedImageView);
        verifiedIMV.setVisibility(View.INVISIBLE);
    }

    private void showBadge()
    {
        if(badges.length() >= 3)
        {
            if(!currentBadge.equals(""))
            {
                TextView badgeTV = findViewById(R.id.badgeTextView);
                badgeTV.setVisibility(View.VISIBLE);
                badgeTV.setText(currentBadge);
            }
        }
    }
    private void showVerifiedBadge()
    {
        if(verified)
        {
            ImageView verifiedIMV = findViewById(R.id.verifiedImageView);
            verifiedIMV.setVisibility(View.VISIBLE);
        }
    }
    //endregion

    private void showFocusPoints()
    {
        TextView focusPointsTV= findViewById(R.id.focusPointsTextView);
        focusPointsTV.setVisibility(View.VISIBLE);
        focusPointsTV.setText("0");
        animateFocusPoints();
    }

    private int currentFocusPoints = 0;
    private void animateFocusPoints()
    {
        new CountDownTimer(1, 1) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(currentFocusPoints == focusPoints){}
                else
                {
                    if(focusPoints - currentFocusPoints > 100)
                    {
                        currentFocusPoints = currentFocusPoints + 100;
                    }
                    else currentFocusPoints++;
                    TextView focusPointsTV= findViewById(R.id.focusPointsTextView);
                    focusPointsTV.setText("" + currentFocusPoints);
                    animateFocusPoints();
                }
            }
        }.start();
    }

}
