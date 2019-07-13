package com.codingshadows.focustimekeeper;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView websiteIMV = findViewById(R.id.companyWebsiteImageView);
        websiteIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebsite();
            }
        });

        ImageView facebookIMV = findViewById(R.id.facebookImageView);
        facebookIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFacebook();
            }
        });

        ImageView twitterIMV = findViewById(R.id.twitterImageView);
        twitterIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTwitter();
            }
        });

        ImageView youtubeIMV = findViewById(R.id.youtubeImageView);
        youtubeIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYouTube();
            }
        });

        ImageView mailIMV = findViewById(R.id.emailImageView);
        mailIMV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }

    private void sendMail()
    {
        String[] TO = {"contact@codingshadows.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CodingShadows - Focus: Time Keeper contact");
        try {
            startActivity(Intent.createChooser(emailIntent, "Va rugam sa selectati o aplicatie pentru a ne trimite un email: "));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactActivity.this, "Nu s-a gasit nici o aplicatie prin care sa puteti trimite mesajul!", Toast.LENGTH_SHORT).show();
        }
    }


    private void showWebsite()
    {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://codingshadows.com/"));
        startActivity(viewIntent);
    }
    private void showYouTube()
    {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/channel/UCtnYygimBZPY7N_XgrlUemg"));
        startActivity(viewIntent);
    }

    private void showTwitter()
    {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://twitter.com/codingshadows"));
        startActivity(viewIntent);
    }

    private void showFacebook()
    {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.facebook.com/codingshadows/"));
        startActivity(viewIntent);
    }

}
