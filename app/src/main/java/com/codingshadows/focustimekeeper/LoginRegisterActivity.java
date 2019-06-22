package com.codingshadows.focustimekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LoginRegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        final Button loginBT = findViewById(R.id.registerButton_REGISTER);
        final Button registerBT = findViewById(R.id.registerButton_LOGIN);
        final TextView resetPasswordBT = findViewById(R.id.newActivityTextView);


        checkForExisting();
        resetPasswordBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        registerBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void login() {
        mAuth = FirebaseAuth.getInstance();
        EditText usernameET = findViewById(R.id.emailEditText_REGISTER);
        final EditText passwordET = findViewById(R.id.passwordEditText_REGISTER);

        final String email = usernameET.getText().toString().trim();
        final String password = passwordET.getText().toString().trim();

        //region Checking the validity of data
        if (email.isEmpty()) {
            usernameET.setError("Email is required");
            usernameET.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usernameET.setError("Please enter a valid email");
            usernameET.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordET.setError("Minimum length of password should be 6");
            passwordET.requestFocus();
            return;
        }

        //endregion

        showProgress("Preparing to sign you in ... ");
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {  // logged in !
                    showProgress("Getting your data ... ");
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  // get user data
                    checkEmail(user.getEmail(), password);
                } else {
                    showProgress("Login failed !");
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkEmail(final String email, final String password)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("LIST").document("GREEN").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String x = documentSnapshot.get("Email").toString();
                if(x.contains(email))
                {
                    writeStringAsFile("true", Class_FileLocations.rememberUserFile);
                    writeStringAsFile(password, Class_FileLocations.userPassword);
                    writeStringAsFile(email, Class_FileLocations.userUsername);

                    Intent intent = new Intent(LoginRegisterActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    finishAffinity();
                }
            }
        });
    }

    private void register() {
        Intent intent = new Intent(LoginRegisterActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }


    private void checkForExisting() {
        showProgress("Checking for an existing account ... ");
        final String email_sure = readFileAsString(Class_FileLocations.userUsername);
        final String password_sure = readFileAsString(Class_FileLocations.userPassword);
        final EditText usernameET = findViewById(R.id.emailEditText_REGISTER);
        final EditText passwordET = findViewById(R.id.passwordEditText_REGISTER);

        try {
            usernameET.setText(email_sure);
            passwordET.setText(password_sure);
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email_sure, password_sure).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {  //logged in!
                        showProgress("Getting your data ... ");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();  // get user data
                        boolean emailVerified = user.isEmailVerified();

                        if (!emailVerified) {
                            Intent intent = new Intent(LoginRegisterActivity.this, VerifyEmailActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showProgress("Logged in!");
                            Intent intent = new Intent(LoginRegisterActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        showProgress("Login failed !");
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {

        }
        showProgress(" ");
    }

    //region Read/Write files
    public void writeStringAsFile(final String fileContents, String fileName) {
        Context context = getApplicationContext();
        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {

        }
    }


    public String readFileAsString(String fileName) {
        Context context = getApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(context.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        return stringBuilder.toString();
    }
    //endregion

    private void showProgress(String text) {
        final TextView displayProgressTV = findViewById(R.id.progressTextView);
        displayProgressTV.setText(text);
        displayProgressTV.setVisibility(View.VISIBLE);
    }

    private void resetPassword()
    {
        String mail = "";

        final EditText usernameET = findViewById(R.id.emailEditText_REGISTER);
        if(usernameET.getText().toString().equals("") || usernameET.getText().toString().equals(" "))
        {
            usernameET.setError("Va rog sa introduceti adresa de mail!");
            return;
        }else
        {
            mail = usernameET.getText().toString().trim();
            FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginRegisterActivity.this, "Mesaj trimis!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}
