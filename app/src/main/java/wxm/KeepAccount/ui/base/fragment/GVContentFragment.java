package wxm.KeepAccount.ui.base.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.ui.base.view.ChartsBase;
import wxm.KeepAccount.ui.base.view.DailyCharts;
import wxm.KeepAccount.ui.base.view.MonthlyCharts;
import wxm.KeepAccount.ui.base.view.YeaylyCharts;
import wxm.KeepAccount.ui.fragment.STGraphViewFragment;

/**
 * Simple Fragment used to display some meaningful content for each page in the sample's
 * {@link android.support.v4.view.ViewPager}.
 */
public class GVContentFragment extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_INDICATOR_COLOR = "indicator_color";
    private static final String KEY_DIVIDER_COLOR = "divider_color";

    private ChartsBase      cur_view;
    private FRMsgHandler    mMsgHandlder = new FRMsgHandler();

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
/*        FrameLayout content = new FrameLayout(this.getContext());
        //缩放控件放置在FrameLayout的上层，用于放大缩小图表
        FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameParm.gravity = Gravity.BOTTOM|Gravity.RIGHT;

        //图表显示范围在占屏幕大小的90%的区域内
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scrWidth = (int) (dm.widthPixels * 0.9);
        int scrHeight = (int) (dm.heightPixels * 0.95);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                scrWidth, scrHeight);

        //居中显示
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
        //final RelativeLayout chartLayout = new RelativeLayout(this.getContext());*/

        // 根据页签选择视图
        Bundle args = getArguments();
        String title = args.getCharSequence(KEY_TITLE).toString();
        Resources res =  getResources();
        if(title.equals(STGraphViewFragment.TAB_TITLE_DAILY))  {
            cur_view = new DailyCharts(this.getContext());

            //chartLayout.addView(cur_view, layoutParams);
            //((ViewGroup) content).addView(chartLayout);
            //this.getActivity().setContentView(content);
        }
        else if(title.equals(STGraphViewFragment.TAB_TITLE_MONTHLY))   {
            cur_view = new MonthlyCharts(this.getContext());

            //chartLayout.addView(cur_view, layoutParams);
            //((ViewGroup) content).addView(chartLayout);
            //this.getActivity().setContentView(content);
        }
        else if(title.equals(STGraphViewFragment.TAB_TITLE_YEARLY))    {
            cur_view = new YeaylyCharts(this.getContext());

            //chartLayout.addView(cur_view, layoutParams);
            //((ViewGroup) content).addView(chartLayout);
            //this.getActivity().setContentView(content);
        }

        return cur_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*Bundle args = getArguments();
        if (args != null) {
        }*/

        updateView();
    }


    /**
     * 加载并显示数据
     */
    public void updateView() {
        if(null != cur_view) {
            Message m = Message.obtain(ContextUtil.getMsgHandler(),
                            AppMsgDef.MSG_LOAD_ALL_RECORDS);
            m.obj = new Object[]    {mMsgHandlder};
            m.sendToTarget();
        }
    }


    public class FRMsgHandler extends Handler {
        private static final String TAG = "FRMsgHandler";
        //private GVContentFragment   mACCur;

        public FRMsgHandler() {
            super();
            //mACCur = cur;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMsgDef.MSG_REPLY: {
                    switch (msg.arg1) {
                        case AppMsgDef.MSG_LOAD_ALL_RECORDS:
                            Object[] rets = UtilFun.cast(msg.obj);
                            List<Object> lsret = UtilFun.cast(rets[0]);

                            cur_view.RenderChart(lsret);
                            break;

                        default:
                            Log.e(TAG, String.format("msg(%s) can not process", msg.toString()));
                            break;
                    }
                }
                break;

                default:
                    Log.e(TAG, String.format("msg(%s) can not process", msg.toString()));
                    break;
            }
        }
    }
}


