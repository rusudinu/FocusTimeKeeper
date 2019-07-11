package com.codingshadows.focustimekeeper;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
    private static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView myWebView = findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        //Choose Mobile/Desktop client.
        webSettings.setUserAgentString(DESKTOP_USER_AGENT);

        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://codingshadows.com/privacy_policy_focus_time_keeper.html");

        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        Button acceptBT = findViewById(R.id.acceptButton);
        Button declineBT = findViewById(R.id.declineButton);

        acceptBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeStringAsFile("true", Class_FileLocations.privacyPolicyAccepted);
                Intent intent = new Intent(PrivacyPolicyActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        declineBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeStringAsFile("false", Class_FileLocations.privacyPolicyAccepted);
                finishAndRemoveTask();
            }
        });
    }

    public void writeStringAsFile(final String fileContents, String fileName) {
        Context context = getApplicationContext();
        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {

        }
    }
}
