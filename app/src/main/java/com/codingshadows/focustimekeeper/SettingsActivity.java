package com.codingshadows.focustimekeeper;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final TextView showPrivacyPolicyBT = findViewById(R.id.privacyPolicyTextView);
        showPrivacyPolicyBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicy();
            }
        });

        final TextView logoutBT = findViewById(R.id.newActivityTextView);
        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        final TextView deleteAccountBT = findViewById(R.id.deleteAccountTextView);
        deleteAccountBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        final TextView permissionsBT = findViewById(R.id.permissionsTextView);
        permissionsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions();
            }
        });

        final TextView documentationBT = findViewById(R.id.documentationTextView);
        documentationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentation();
            }
        });
    }


    private void documentation() {
        Intent intent = new Intent(SettingsActivity.this, DocumentationActivity.class);
        startActivity(intent);
    }

    private void permissions() {
        Intent intent = new Intent(SettingsActivity.this, PermissionDetailsActivity.class);
        startActivity(intent);
    }


    private void showPrivacyPolicy() {
        Intent intent = new Intent(SettingsActivity.this, PrivacyPolicyNoButtonsActivity.class);
        startActivity(intent);
    }

    private void logout() {

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Toast.makeText(this, "Delogare cu reusita!", Toast.LENGTH_LONG).show();
        File username = new File(getFilesDir(), Class_FileLocations.userUsername);
        username.getAbsoluteFile().delete();
        File password = new File(getFilesDir(), Class_FileLocations.userPassword);
        password.getAbsoluteFile().delete();
        File remember = new File(getFilesDir(), Class_FileLocations.rememberUserFile);
        remember.getAbsoluteFile().delete();
        finishAffinity();
    }

    private void deleteAccount() {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        try {
            Toast.makeText(this, "Contul tau a fost sters!", Toast.LENGTH_LONG).show();
            user.delete();
            File username = new File(getFilesDir(), Class_FileLocations.userUsername);
            username.getAbsoluteFile().delete();
            File password = new File(getFilesDir(), Class_FileLocations.userPassword);
            password.getAbsoluteFile().delete();
            File remember = new File(getFilesDir(), Class_FileLocations.rememberUserFile);
            remember.getAbsoluteFile().delete();
            finishAffinity();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

}
