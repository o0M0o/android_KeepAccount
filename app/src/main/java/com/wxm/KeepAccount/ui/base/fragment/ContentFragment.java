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

package com.wxm.KeepAccount.ui.base.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wxm.KeepAccount.BaseLib.AppGobalDef;
import com.wxm.KeepAccount.BaseLib.AppManager;
import com.wxm.KeepAccount.BaseLib.AppMsg;
import com.wxm.KeepAccount.BaseLib.AppMsgDef;
import com.wxm.KeepAccount.BaseLib.ContextUtil;
import com.wxm.KeepAccount.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class ContentFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    private View cur_view;

    /**
     * @return a new instance of {@link ContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static ContentFragment newInstance(CharSequence title, int indicatorColor,
                                              int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cur_view =  inflater.inflate(R.layout.pager_item, container, false);
        return cur_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            /*
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText("Title: " + args.getCharSequence(KEY_TITLE));

            int indicatorColor = args.getInt(KEY_INDICATOR_COLOR);
            TextView indicatorColorView = (TextView) view.findViewById(R.id.item_indicator_color);
            indicatorColorView.setText("Indicator: #" + Integer.toHexString(indicatorColor));
            indicatorColorView.setTextColor(indicatorColor);

            int dividerColor = args.getInt(KEY_DIVIDER_COLOR);
            TextView dividerColorView = (TextView) view.findViewById(R.id.item_divider_color);
            dividerColorView.setText("Divider: #" + Integer.toHexString(dividerColor));
            dividerColorView.setTextColor(dividerColor);
            */
            showListView(view, args);
        }
    }


    /**
     * 加载并显示数据
     */
    private void showListView(View vw, Bundle args) {
        Resources res =  getResources();
        String title = args.getCharSequence(KEY_TITLE).toString();
        ArrayList<HashMap<String, String>> mylist = null;
        if(res.getString(R.string.tab_cn_daily)
                .equals(title)) {
            AppMsg am = new AppMsg();
            am.msg = AppMsgDef.MSG_ALL_RECORDS_TO_DAYREPORT;
            am.sender = this;
            mylist =
                    (ArrayList<HashMap<String, String>>) AppManager.getInstance().ProcessAppMsg(am);
        }
        else if(res.getString(R.string.tab_cn_monthly)
                .equals(title)) {
            AppMsg am = new AppMsg();
            am.msg = AppMsgDef.MSG_ALL_RECORDS_TO_MONTHREPORT;
            am.sender = this;
            mylist =
                    (ArrayList<HashMap<String, String>>) AppManager.getInstance().ProcessAppMsg(am);
        }
        else if(res.getString(R.string.tab_cn_yearly)
                .equals(title)) {
            AppMsg am = new AppMsg();
            am.msg = AppMsgDef.MSG_ALL_RECORDS_TO_YEARREPORT;
            am.sender = this;
            mylist =
                    (ArrayList<HashMap<String, String>>) AppManager.getInstance().ProcessAppMsg(am);
        }

        if(null != mylist) {
            SimpleAdapter mSchedule = new SimpleAdapter(ContextUtil.getInstance(),
                    mylist,
                    R.layout.main_listitem,
                    new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                    new int[]{R.id.ItemTitle, R.id.ItemText});

            ListView lv = (ListView) vw.findViewById(R.id.tabvp_lv_main);
            lv.setAdapter(mSchedule);
        }
    }
}

