package com.codingshadows.focustimekeeper;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final Button registerBT = findViewById(R.id.registerButton_REGISTER);
        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register()
    {
        mAuth = FirebaseAuth.getInstance();
        EditText emailET = findViewById(R.id.emailEditText_REGISTER);
        EditText passwordET = findViewById(R.id.passwordEditText_REGISTER);
        EditText confirmPasswET = findViewById(R.id.password2EditText_REGISTER);
        EditText serviceNumberET = findViewById(R.id.serviceNumberEditText_REGISTER);
        final String serviceNumber = serviceNumberET.getText().toString().trim();
        final String email = emailET.getText().toString().trim();
        final String password = passwordET.getText().toString().trim();
        final String checkPassw = confirmPasswET.getText().toString().trim();

        //region Check data
        if(serviceNumber.equals("")) {
            serviceNumberET.setError("Please fill this!");
            Toast.makeText(this, serviceNumber, Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkPassw.equals(password))
        {
            passwordET.setError("Passwords don't match!");
            confirmPasswET.requestFocus();
            confirmPasswET.setError("Passwords don't match!");
            return;
        }

        if (email.isEmpty()) {
            emailET.setError("Email is required");
            emailET.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Please enter a valid email");
            emailET.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordET.setError("Minimum lenght of password should be 6");
            passwordET.requestFocus();
            return;
        }

        if(email.contains("@mailinator.com"))
        {
            emailET.setError("Please enter your real email!");
            return;
        }

        //endregion

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Please check your email to activate your account!", Toast.LENGTH_LONG).show();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();


                    String userID = user.getUid();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(serviceNumber)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                    }
                                }
                            });
                    pushUserData(userID, email, serviceNumber);
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, LoginRegisterActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    private void pushUserData(String userID, String email , String username)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("UserID", userID);
        userData.put("Email", email);
        userData.put("Username", username);
        userData.put("Verified", false);
        userData.put("Achievements", "");
        userData.put("Badges", "");
        userData.put("Current badge", "");
        userData.put("Highest time", 0);
        userData.put("Focus points", 0);
        userData.put("Characters owned", "");
        userData.put("Focus coins", "");

        db.collection("Users").document(userID).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
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
