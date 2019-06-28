package com.codingshadows.focustimekeeper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class LoadingScreenActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);



        if(checkCrack()) //TODO ENABLE.DISABLE
        {
          android.os.Process.killProcess(android.os.Process.myPid());
          finishAffinity();
        }

        checkStorage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPrivacyPol();
        }
    }


    private void checkStorage() {
        if (ContextCompat.checkSelfPermission(LoadingScreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(LoadingScreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoadingScreenActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
            if (ContextCompat.checkSelfPermission(LoadingScreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoadingScreenActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        } else {
            checkPrivacyPol();
        }
    }

    private void checkPrivacyPol() {
        Boolean b1 = Boolean.valueOf(readFileAsString(Class_FileLocations.privacyPolicyAccepted));
        if (b1) {
            checkForExisting(); // check for an existing account
        } else {
            Intent it = new Intent(LoadingScreenActivity.this, PrivacyPolicyActivity.class);
            startActivity(it);
            finish();
        }
    }

    //region Read/Write files
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


    private boolean checkCrack() {

        Context context = getApplicationContext();
        String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        if(BuildConfig.DEBUG)
        {
            return true;
        }

        try {
            if (!context.getPackageName().equals("com.codingshadows.focustimekeeper")) {
                return true;
            }

        } catch (Exception e) {

        }
        try {
            if (!String.valueOf(installer).equals("")) {
                if (!installer.startsWith("com.android.vending")) {
                    return true;
                }
            }
        } catch (Exception e) {

        }


        return false;
    }

    private void checkForExisting() {
        final String email_sure = readFileAsString(Class_FileLocations.userUsername);
        final String password_sure = readFileAsString(Class_FileLocations.userPassword);

        try {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email_sure, password_sure).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoadingScreenActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            Intent intent = new Intent(LoadingScreenActivity.this, LoginRegisterActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
