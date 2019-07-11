package com.codingshadows.focustimekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerformanceActivity extends AppCompatActivity {
    private int activitiesCompleted = 0;
    private int totalActivities = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    private void getData(String dayID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PROGRAM").document(getUID()).collection(dayID).document("PERFORMANCE").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("Activities completed") != null && documentSnapshot.get("Activities total") != null) {
                    String actCompleted = documentSnapshot.get("Activities completed").toString();
                    String actTotal = documentSnapshot.get("Activities total").toString();
                    activitiesCompleted = Integer.valueOf(actCompleted);
                    totalActivities = Integer.valueOf(actTotal);
                }
            }
        });
    }

    private void showData()
    {

    }
}
