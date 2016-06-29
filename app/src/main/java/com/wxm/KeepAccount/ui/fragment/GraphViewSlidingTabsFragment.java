package com.wxm.KeepAccount.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.ui.base.fragment.GVContentFragment;
import com.wxm.KeepAccount.ui.base.fragment.SlidingTabsColorsFragment;

import java.util.List;

/**
 * 图表视图表现数据
 * Created by wxm on 2016/5/30.
 */
public class GraphViewSlidingTabsFragment extends SlidingTabsColorsFragment {
    private static final String TAG = "GVSlidingTabsFragment ";

    protected static class ListViewPagerItem extends SamplePagerItem {
        public ListViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            super(title, indicatorColor, dividerColor);
        }

        @Override
        protected Fragment createFragment() {
            return GVContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    @Override
    public void notifyDataChange()  {
        FragmentManager fragmentManager = getChildFragmentManager();
        //FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment f : fragments) {
            GVContentFragment gvf = (GVContentFragment)f;
            gvf.updateView();
        }
        //transaction.commit();
    }
}

