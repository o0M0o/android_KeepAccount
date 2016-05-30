package com.wxm.KeepAccount.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;

/**
 * Created by wxm on 2016/5/30.
 */
public class ListViewSlidingTabsFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "ListViewSlidingTabsFragment ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (populate_tabs)
        mTabs.add(new SamplePagerItem(
                getString(R.string.tab_cn_daily), // Title
                Color.GREEN, // Indicator color
                Color.GRAY// Divider color
        ));

        mTabs.add(new SamplePagerItem(
                getString(R.string.tab_cn_monthly), // Title
                Color.GREEN, // Indicator color
                Color.GRAY// Divider color
        ));

        mTabs.add(new SamplePagerItem(
                getString(R.string.tab_cn_yearly), // Title
                Color.GREEN, // Indicator color
                Color.GRAY// Divider color
        ));

        // END_INCLUDE (populate_tabs)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


}
