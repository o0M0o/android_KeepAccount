package com.wxm.KeepAccount.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;
import com.wxm.KeepAccount.ui.base.activities.TabActivityBase;

/**
 * tab版本的main activity
 * Created by 123 on 2016/5/16.
 */
public class ActivityMainTab extends TabActivityBase {
    public static final String TAG = "ActivityMainTab";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintab);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsColorsFragment fragment = new SlidingTabsColorsFragment();
            transaction.replace(R.id.tabfl_content, fragment);
            transaction.commit();
        }
    }
}
