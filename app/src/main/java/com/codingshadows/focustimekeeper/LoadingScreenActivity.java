package com.codingshadows.focustimekeeper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LoadingScreenActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1;
    private FirebaseAuth mAuth;
    private String tag = "LoadingScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (checkCrack()) //TODO ENABLE.DISABLE
        {
            //android.os.Process.killProcess(android.os.Process.myPid());
            //finishAffinity();
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
            Log.e(tag, e.toString());
        } catch (IOException e) {
            Log.e(tag, e.toString());
        }
        return stringBuilder.toString();
    }
    //endregion


    private boolean checkCrack() {

        Context context = getApplicationContext();
        String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        if (BuildConfig.DEBUG) {
            return true;
        }

        try {
            if (!context.getPackageName().equals("com.codingshadows.focustimekeeper")) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        try {
            if (!String.valueOf(installer).equals("")) {
                if (!installer.startsWith("com.android.vending")) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
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
