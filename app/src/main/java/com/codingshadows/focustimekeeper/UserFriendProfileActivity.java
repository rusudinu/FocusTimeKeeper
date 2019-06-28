package com.codingshadows.focustimekeeper;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserFriendProfileActivity extends AppCompatActivity {

    String searchEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friend_profile);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


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
                            Intent intent = new Intent(UserFriendProfileActivity.this,UserProfileActivity.class);
                            intent.putExtra("ID",document.getId());
                            startActivity(intent);
                            finish();
                        }
                    }
                    if(x == 0) {
                        Toast.makeText(UserFriendProfileActivity.this, "Utilizatorul nu exista!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
