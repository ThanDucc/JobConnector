package com.example.jobconnector.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.jobconnector.R;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        WebView webView = findViewById(R.id.webview);

        webView.loadUrl("https://www.privacypolicies.com/live/75492327-36dd-4624-ad9f-2019ce4d73e4");
    }
}