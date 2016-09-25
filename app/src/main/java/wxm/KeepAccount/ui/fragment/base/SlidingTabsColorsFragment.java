/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wxm.KeepAccount.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.wxm.andriodutillib.SlidingTab.SlidingTabLayout;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;

/**
 * A basic sample which shows how to use {}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsColorsFragment extends Fragment {
    private static final String TAG = "SlidingTabsFragment";

    public static final String KEY_TITLE = "title";
    public static final String KEY_INDICATOR_COLOR = "indicator_color";
    public static final String KEY_DIVIDER_COLOR = "divider_color";

    protected SlidingTabLayout mSlidingTabLayout;

    /**
     * This class represents a tab to be displayed by {@link ViewPager} and it's associated
     * {@link SlidingTabLayout}.
     */
    protected  static abstract class SamplePagerItem {
        protected final CharSequence mTitle;
        protected final int mIndicatorColor;
        protected final int mDividerColor;

        public SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        /**
         * @return the title which represents this tab. In this sample this is used directly by
         * {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
         */
        public CharSequence getTitle() {
            return mTitle;
        }

        /**
         * @return the color to be used for indicator on the {@link SlidingTabLayout}
         */
        int getIndicatorColor() {
            return mIndicatorColor;
        }

        /**
         * @return the color to be used for right divider on the {@link SlidingTabLayout}
         */
        int getDividerColor() {
            return mDividerColor;
        }

        /**
         * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
         */
        abstract protected  Fragment createFragment();
       /* Fragment createFragment()   {
            return null;
        }*/
    }

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    protected ViewPager mViewPager;

    /**
     * List of {@link SamplePagerItem} which represent this sample's tabs.
     */
    protected final List<SamplePagerItem> mTabs = new ArrayList<>();

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_main, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of
     * {@link SampleFragmentPagerAdapter}. The {@link SlidingTabLayout} is then given the
     * {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i(TAG, String.format("savedInstanceState = %s",
                            null == savedInstanceState ? "null" : savedInstanceState.toString()));

        // BEGIN_INCLUDE (setup_viewpager)
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        /*
          A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
          above, but is designed to give continuous feedback to the user when scrolling.
         */
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // BEGIN_INCLUDE (tab_colorizer)
        // Set a TabColorizer to customize the indicator and divider colors. Here we just retrieve
        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }

        });
        // END_INCLUDE (tab_colorizer)
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    public void jumpToTabName(String tabname)   {
        int cur_pos = getCurViewPostion();
        int c = mTabs.size();
        for (int i = 0; i < c; ++i) {
            SamplePagerItem si = mTabs.get(i);
            if (i != cur_pos && si.getTitle().toString().equals(tabname)) {
                View v = this.getView();
                assert null != v;

                View vv = v.findViewById(R.id.viewpager);
                assert null != vv;

                ViewPager vp = UtilFun.cast(vv);
                vp.setCurrentItem(i, true);
                break;
            }
        }
    }

    public void notifyDataChange()  {
        /* FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment f : fragments) {
            transaction.remove(f);
        }
        transaction.commit();*/
        View v = this.getView();
        assert null != v;

        View vv = v.findViewById(R.id.viewpager);
        assert null != vv;

        ViewPager vp = UtilFun.cast(vv);
        vp.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
    }

    protected int getCurViewPostion()  {
        View v = this.getView();
        assert null != v;

        View vv = v.findViewById(R.id.viewpager);
        assert null != vv;

        ViewPager vp = UtilFun.cast(vv);
        return vp.getCurrentItem();
    }

    /**
     * The {@link FragmentPagerAdapter} used to display pages in this sample. The individual pages
     * are instances of {@link Fragment} which just display three lines of text. Each page is
     * created by the relevant {@link SamplePagerItem} for the requested position.
     * <p>
     * The important section of this class is the {@link #getPageTitle(int)} method which controls
     * what is displayed in the {@link SlidingTabLayout}.
     */
    protected class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the {@link android.support.v4.app.Fragment} to be displayed at {@code position}.
         * <p>
         * Here we return the value returned from {@link SamplePagerItem#createFragment()}.
         */
        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we return the value returned from {@link SamplePagerItem#getTitle()}.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
        // END_INCLUDE (pageradapter_getpagetitle)

    }

}