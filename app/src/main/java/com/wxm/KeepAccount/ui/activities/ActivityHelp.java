package com.wxm.KeepAccount.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.R;

/**
 * 本activity用于展示应用帮助信息
 * 为优化代码架构，activity启动时根据intent参数加载不同帮助信息
 */
public class ActivityHelp extends AppCompatActivity {
    private String HELP_MAIN_FILEPATH = "file:///android_asset/help_main.html";
    private String HELP_START_FILEPATH = "file:///android_asset/help_start.html";
    private String HELP_DAILYDETAIL_FILEPATH = "file:///android_asset/help_dailydetail.html";
    private String HELP_RECORD_FILEPATH = "file:///android_asset/help_dailydetail.html";
    //private static String TAG = "ActivityHelp";
    private static final String ENCODING = "utf-8";
    //private static final String MIMETYPE = "text/html; charset=UTF-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_help);

        // load help html
        Intent it = getIntent();
        String help_type = it.getStringExtra(AppGobalDef.STR_HELP_TYPE);
        if(null != help_type)   {
            if(help_type.equals(AppGobalDef.STR_HELP_MAIN))    {
                load_main_help();
            }
            else if(help_type.equals(AppGobalDef.STR_HELP_START))  {
                load_start_help();
            }
            else if(help_type.equals(AppGobalDef.STR_HELP_DAILYDETAIL))  {
                load_dailydetail_help();
            }
            else if(help_type.equals(AppGobalDef.STR_HELP_RECORD))  {
                load_dailydetail_help();
            }
        }
    }

    /**
     * 加载应用主帮助信息
     */
    private void load_main_help()   {
        WebView wv = (WebView) findViewById(R.id.ac_help_webvw);
        WebSettings wSet = wv.getSettings();
        wSet.setDefaultTextEncodingName(ENCODING);
        wv.loadUrl(HELP_MAIN_FILEPATH);
    }

    /**
     * 加载应用'开始'帮助信息
     */
    private void load_start_help()   {
        WebView wv = (WebView) findViewById(R.id.ac_help_webvw);
        WebSettings wSet = wv.getSettings();
        wSet.setDefaultTextEncodingName(ENCODING);
        wv.loadUrl(HELP_START_FILEPATH);
    }

    /**
     * 加载应用'日详情'帮助信息
     */
    private void load_dailydetail_help()   {
        WebView wv = (WebView) findViewById(R.id.ac_help_webvw);
        WebSettings wSet = wv.getSettings();
        wSet.setDefaultTextEncodingName(ENCODING);
        wv.loadUrl(HELP_DAILYDETAIL_FILEPATH);
    }

    /**
     * 加载应用'日详情'帮助信息
     */
    private void load_record_help()   {
        WebView wv = (WebView) findViewById(R.id.ac_help_webvw);
        WebSettings wSet = wv.getSettings();
        wSet.setDefaultTextEncodingName(ENCODING);
        wv.loadUrl(HELP_RECORD_FILEPATH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acm_help_actbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acm_mi_help_leave: {
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

}
