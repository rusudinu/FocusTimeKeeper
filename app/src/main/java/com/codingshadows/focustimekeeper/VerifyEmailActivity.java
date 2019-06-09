package com.codingshadows.focustimekeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mAuth = FirebaseAuth.getInstance();
        Button resendEmail = findViewById(R.id.resendEmailButton2);
        Button alreadyDid = findViewById(R.id.errorButton_VerifyEmail);
        final TextView messageTV = findViewById(R.id.displayMessageTextBoxVerifyEmail);



        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.sendEmailVerification();
                Toast.makeText(VerifyEmailActivity.this, "Email sent!", Toast.LENGTH_LONG).show();
                messageTV.setText("Mesajul a fost retrimis! \n\n Daca tot nu l-ai primit, te rugam sa ne contactezi la " +
                        "contact@codingshadows.com. \n Multumim!");
            }
        });

        alreadyDid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageTV.setText("Te rugam sa incerci sa redeschizi aplicatia! \n\n Daca dupa restart acest ecran apare din nou si contul tau este verificat, te rugam " +
                        "sa ne contactezi la  " +
                        "contact@codingshadows.com\n Multumim!");
            }
        });
    }
}
