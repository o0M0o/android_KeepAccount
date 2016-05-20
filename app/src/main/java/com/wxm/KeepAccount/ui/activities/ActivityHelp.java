package com.wxm.KeepAccount.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.wxm.KeepAccount.R;

public class ActivityHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView wv = (WebView)findViewById(R.id.ac_help_webvw);
        WebSettings wSet = wv.getSettings();
        wSet.setJavaScriptEnabled(true);
        wv.loadUrl("file:///data/data/com.wxm.keepaccount/help.html");
    }

}
