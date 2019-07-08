package com.codingshadows.focustimekeeper;

import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

public class ShopActivity extends AppCompatActivity {

    private int focusPoints = 0;
    private String focusCoins = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getData();
        ImageView imv = findViewById(R.id.rareFocusCoinImageView);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(focusPoints >= 200 && !focusCoins.contains("Archer coin"))
                {
                    focusPoints = focusPoints - 200;
                    focusCoins = focusCoins + "Archer coin,";
                    pushData();
                    Toast.makeText(ShopActivity.this, "Congrats!", Toast.LENGTH_SHORT).show();
                    getData();
                }
            }
        });
    }

    private void pushData()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(getUID()).update("Focus points", focusPoints);
        db.collection("Users").document(getUID()).update("Focus coins", focusCoins);
    }



    private void getData()
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.collection("Users").document(getUID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    focusPoints = Integer.valueOf(documentSnapshot.get("Focus points").toString());
                    if(documentSnapshot.get("Focus coins") != null) {
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
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }

    private void showAll()
    {
        TextView coinsTV = findViewById(R.id.focusPointsTextViewShop);
        coinsTV.setText("" + focusPoints);

        ImageView imv = findViewById(R.id.rareFocusCoinImageView);
        if(focusCoins.contains("Archer coin"))
        {
            TextView priceTV = findViewById(R.id.rareCoinPrice);
            priceTV.setVisibility(View.INVISIBLE);
            imv.setImageResource(R.drawable.rarefocuscoin);

        }
        else imv.setImageResource(R.drawable.rarefocuscoinlocked);
    }

}
