package wxm.KeepAccount.ui.acinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import wxm.KeepAccount.R;

/**
 * 本activity用于展示应用帮助信息
 * 为优化代码架构，activity启动时根据intent参数加载不同帮助信息
 */
public class ACHelp extends AppCompatActivity {
    static public final String STR_HELP_TYPE            = "HELP_TYPE";

    static public final String STR_HELP_MAIN            = "help_main";
    static public final String STR_HELP_START           = "help_start";
    static public final String STR_HELP_DAILYDETAIL     = "help_dailydetail";
    static public final String STR_HELP_RECORD          = "help_record";
    public static final String STR_HELP_BUDGET          = "budget";


    //private static String TAG = "ACHelp";
    private static final String ENCODING = "utf-8";
    //private static final String MIMETYPE = "text/html; charset=UTF-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_help);
        load_help("file:///android_asset/help_main.html");
        /*
        // load help html
        Intent it = getIntent();
        String help_type = it.getStringExtra(STR_HELP_TYPE);
        if(null != help_type)   {
            switch (help_type)  {
                case STR_HELP_MAIN :
                    load_help("file:///android_asset/help_main.html");
                    break;

                case STR_HELP_START :
                    load_help("file:///android_asset/help_start.html");
                    break;

                case STR_HELP_DAILYDETAIL :
                    load_help("file:///android_asset/help_dailydetail.html");
                    break;

                case STR_HELP_RECORD :
                    load_help("file:///android_asset/help_record.html");
                    break;

                default:
                    load_help("file:///android_asset/help_main.html");
                    break;
            }
        }
        */
    }

    /**
     * 加载帮助html
     * @param url  帮助html路径
     */
    private void load_help(String url)  {
        WebView wv = (WebView) findViewById(R.id.ac_help_webvw);
        if(null != wv) {
            WebSettings wSet = wv.getSettings();
            wSet.setDefaultTextEncodingName(ENCODING);

            wv.loadUrl(url);
        }
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