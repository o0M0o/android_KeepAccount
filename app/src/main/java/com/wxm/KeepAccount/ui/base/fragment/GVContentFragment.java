package com.wxm.KeepAccount.ui.base.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.wxm.KeepAccount.BaseLib.AppModel;
import com.wxm.KeepAccount.R;
import com.wxm.KeepAccount.ui.base.view.ChartsBase;
import com.wxm.KeepAccount.ui.base.view.DailyCharts;
import com.wxm.KeepAccount.ui.base.view.MonthlyCharts;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class GVContentFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    private ChartsBase cur_view;
    private ArrayList<HashMap<String, String>> gv_list = new ArrayList<>();
    private SimpleAdapter gv_adapter = null;


    /**
     * @return a new instance of {@link LVContentFragment}, adding the parameters into a bundle and
     * setting them as arguments.
     */
    public static GVContentFragment newInstance(CharSequence title, int indicatorColor,
                                                int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

        GVContentFragment fragment = new GVContentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        cur_view =  inflater.inflate(R.layout.pager_item, container, false);

        Bundle args = getArguments();
        String title = args.getCharSequence(KEY_TITLE).toString();
        ListView lv = (ListView) cur_view.findViewById(R.id.tabvp_lv_main);
        Resources res =  getResources();
        if(title.equals(res.getString(R.string.tab_cn_daily)))  {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> hmsel =
                            (HashMap<String, String>)parent.getItemAtPosition(position);

                    //String str= parent.getItemAtPosition(position).toString();
                    //String class_str= parent.getItemAtPosition(position).getClass().toString();
                    //Log.d(TAG, String.format("long click(%s) : %s", class_str, str));

                    Intent intent = new Intent(parent.getContext(), ActivityDailyDetail.class);
                    intent.putExtra(AppGobalDef.STR_SELECT_ITEM,
                            hmsel.get(AppGobalDef.ITEM_TITLE));
                    startActivityForResult(intent, 1);
                }
            });
        }

        // 设置listview adapter
        gv_adapter= new SimpleAdapter(ContextUtil.getInstance(),
                gv_list,
                R.layout.main_listitem,
                new String[]{AppGobalDef.ITEM_TITLE, AppGobalDef.ITEM_TEXT},
                new int[]{R.id.ItemTitle, R.id.ItemText}) {
            @Override
            public int getViewTypeCount() {
                int org_ct = getCount();
                return org_ct < 1 ? 1 : org_ct;
            }

            @Override
            public int getItemViewType(int position) {
                return position;
            }
        };

        lv.setAdapter(gv_adapter);
        return cur_view;
        */

        FrameLayout content = new FrameLayout(this.getContext());
        //缩放控件放置在FrameLayout的上层，用于放大缩小图表
        FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameParm.gravity = Gravity.BOTTOM|Gravity.RIGHT;

        //图表显示范围在占屏幕大小的90%的区域内
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scrWidth = (int) (dm.widthPixels * 0.9);
        int scrHeight = (int) (dm.heightPixels * 0.9);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                scrWidth, scrHeight);

        //居中显示
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
        final RelativeLayout chartLayout = new RelativeLayout(this.getContext());


        // 根据页签选择视图
        Bundle args = getArguments();
        String title = args.getCharSequence(KEY_TITLE).toString();
        Resources res =  getResources();
        if(title.equals(res.getString(R.string.tab_cn_daily)))  {
            cur_view = new DailyCharts(this.getContext());
            chartLayout.addView(cur_view, layoutParams);

            ((ViewGroup) content).addView(chartLayout);
            //this.getActivity().setContentView(content);
        }
        else if(title.equals(res.getString(R.string.tab_cn_monthly)))   {
            cur_view = new MonthlyCharts(this.getContext());
            chartLayout.addView(cur_view, layoutParams);

            ((ViewGroup) content).addView(chartLayout);
            //this.getActivity().setContentView(content);
        }

        return cur_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        Bundle args = getArguments();
        if (args != null) {
        }
        */

        updateView();
    }


    /**
     * 加载并显示数据
     */
    public void updateView() {
        cur_view.RenderChart(AppModel.getInstance().GetAllRecords());
    }
}


